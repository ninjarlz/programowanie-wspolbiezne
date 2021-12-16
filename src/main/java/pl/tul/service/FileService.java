package pl.tul.service;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Log4j2
public class FileService {

    private final ThreadPoolExecutor executorService;
    private final Map<String, FileClient> waitingFileClients = new ConcurrentHashMap<>();
    private final int threadsNum;
    private final List<FileThread> fileThreads = Collections.synchronizedList(new ArrayList<>());
    private final BiConsumer<File, FileThread> fileUploadBeginCallback;
    private final Consumer<FileThread> fileUploadFinishedCallback;
    private static final String FILE_THREAD_ID_PREFIX = "fileThread";

    public FileService(int threadsNum, BiConsumer<File, FileThread> fileUploadBeginCallback,
                       TriConsumer<File, FileThread, Long> fileUploadProcessingCallback,
                       Consumer<FileThread> fileUploadFinishedCallback) {
        this.threadsNum = threadsNum;
        for (int i = 1; i <= threadsNum; i++) {
            fileThreads.add(new FileThread(FILE_THREAD_ID_PREFIX + i, fileUploadProcessingCallback));
        }
        executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(threadsNum);
        this.fileUploadBeginCallback = fileUploadBeginCallback;
        this.fileUploadFinishedCallback = fileUploadFinishedCallback;
    }

    public void addFileClient(FileClient fileClient) {
        waitingFileClients.put(fileClient.getId(), fileClient);
        if (!fileClient.getFileList().isEmpty() && shouldExecuteUploadOnAdd()) {
            Collections.sort(fileClient.getFileList());
            executeFileUpload(fileClient.getFileList().get(0));
        }
    }

    public void removeFileClient(FileClient fileClient) {
        waitingFileClients.remove(fileClient.getId());
    }

    public void addFile(File file) {
        if (!waitingFileClients.containsKey(file.getClientId())) {
            throw new IllegalArgumentException(String.format("There is no client with the id %s in the queue", file.getClientId()));
        }
        if (shouldExecuteUploadOnAdd()) {
            executeFileUpload(file);
            return;
        }
        waitingFileClients.get(file.getClientId()).getFileList().add(file);
    }

    private boolean shouldExecuteUploadOnAdd() {
        return fileThreads.stream().filter(FileThread::isActive).count() < threadsNum;
    }

    private List<File> getDataForAuction() {
        waitingFileClients.values().forEach(fileClient -> Collections.sort(fileClient.getFileList()));
        return waitingFileClients.values().stream().collect(ArrayList::new,
                (list, fileClient) -> {
                    if (!fileClient.getFileList().isEmpty()) {
                        list.add(fileClient.getFileList().get(0));
                    }
                },
                ArrayList::addAll);
    }

    private int getFilesCount() {
        return waitingFileClients.values().stream().reduce(0, (sum, fileClient) -> sum += fileClient.getFileList().size(), Integer::sum);
    }

    private File getFileFromAuction(List<File> files) {
        int numberOfFiles = getFilesCount();
        Optional<File> optionalFile = files.stream().max(new AuctionComparator(numberOfFiles));
        File file = optionalFile.orElseThrow(() -> new IllegalArgumentException("Files list cannot be empty"));
        removeFileFromQueue(file);
        return file;
    }

    private void removeFileFromQueue(File file) {
        if (!waitingFileClients.containsKey(file.getClientId())) {
            return;
        }
        waitingFileClients.get(file.getClientId()).getFileList().remove(file);
    }

    private void performAuction() {
        List<File> files = getDataForAuction();
        if (files.isEmpty()) {
            return;
        }
        File file = getFileFromAuction(files);
        executeFileUpload(file);
    }

    private void executeFileUpload(File file) {
        executorService.execute(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            FileThread fileThread = fileThreads.stream().filter(f -> !f.isActive()).findFirst().orElseThrow();
            fileThread.setActive(true);
            fileUploadBeginCallback.accept(file, fileThread);
            fileThread.execute(file);
            fileUploadFinishedCallback.accept(fileThread);
            fileThread.setActive(false);
            log.info("Execution for file - [id - {}, owner - {}, size - {} MB] took: "
                    + (System.currentTimeMillis() - currentTimeMillis), file.getId(), file.getClientId(), file.getFileSize());
            performAuction();
        });
    }
}
