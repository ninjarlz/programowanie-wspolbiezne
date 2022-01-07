package pl.tul.service;

import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Log4j2
public class FileService {

    private final ThreadPoolExecutor executorService;
    private final Map<String, FileClient> waitingFileClients = new ConcurrentHashMap<>();
    private final int threadsNum;
    private final List<FileThread> fileThreads = Collections.synchronizedList(new ArrayList<>());
    private final TriConsumer<FileClient, FileUpload, FileThread> fileUploadBeginCallback;
    private final Consumer<FileThread> fileUploadFinishedCallback;
    private final ReentrantLock lock = new ReentrantLock();
    private static final String FILE_THREAD_ID_PREFIX = "fileThread";

    public FileService(int threadsNum, TriConsumer<FileClient, FileUpload, FileThread> fileUploadBeginCallback,
                       TriConsumer<FileUpload, FileThread, Long> fileUploadProcessingCallback,
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
        lock.lock();
        try {
            waitingFileClients.put(fileClient.getId(), fileClient);
            if (!fileClient.getFileList().isEmpty() && shouldExecuteUploadOnAdd()) {
                Collections.sort(fileClient.getFileList());
                File file = fileClient.getFileList().get(0);
                executeFileUpload(new FileUpload(file.getId(), fileClient.getId(), file.getFileSize()));
            }
        } finally {
            lock.unlock();
        }
    }

    public void removeFileClient(FileClient fileClient) {
        lock.lock();
        try {
            waitingFileClients.remove(fileClient.getId());
        } finally {
            lock.unlock();
        }
    }

    public void addFile(File file) {
        lock.lock();
        try {
            if (!waitingFileClients.containsKey(file.getClientId())) {
                throw new IllegalArgumentException(String.format("There is no client with the id %s in the queue", file.getClientId()));
            }
            if (shouldExecuteUploadOnAdd()) {
                executeFileUpload(new FileUpload(file.getId(), file.getClientId(), file.getFileSize()));
                return;
            }
            FileClient fileClient = waitingFileClients.get(file.getClientId());
            List<File> files = Collections.synchronizedList(new ArrayList<>(fileClient.getFileList()));
            if (files.isEmpty()) {
                fileClient.resetWaitingTime();
            }
            files.add(file);
            Collections.sort(files);
            fileClient.setFileList(files);
        } finally {
            lock.unlock();
        }
    }

    private boolean shouldExecuteUploadOnAdd() {
        return fileThreads.stream().filter(FileThread::isActive).count() < threadsNum;
    }

    private List<FileUpload> getDataForAuction() {
        waitingFileClients.values().forEach(fileClient -> Collections.sort(fileClient.getFileList()));
        return waitingFileClients.values().stream().collect(ArrayList::new,
                (list, fileClient) -> {
                    if (!fileClient.getFileList().isEmpty()) {
                        File file = fileClient.getFileList().get(0);
                        list.add(new FileUpload(file.getId(), fileClient.getId(), file.getFileSize(), fileClient.getWaitingTime()));
                    }
                },
                ArrayList::addAll);
    }

    private FileUpload getFileUploadFromAuction(List<FileUpload> files) {
        int numberOfFileClients = waitingFileClients.values().size();
        Optional<FileUpload> optionalFile = files.stream().max(new AuctionComparator(numberOfFileClients));
        return optionalFile.orElseThrow(() -> new IllegalArgumentException("Files list cannot be empty"));
    }

    private void removeFileFromQueue(FileUpload fileUpload) {
        if (!waitingFileClients.containsKey(fileUpload.getClientId())) {
            return;
        }
        List<File> files = waitingFileClients.get(fileUpload.getClientId()).getFileList();
        files.removeIf(f -> f.getId().equals(fileUpload.getFileId()));
        Collections.sort(files);
    }

    private void performAuction() {
        lock.lock();
        try {
            List<FileUpload> fileUploads = getDataForAuction();
            if (fileUploads.isEmpty()) {
                return;
            }
            FileUpload fileUpload = getFileUploadFromAuction(fileUploads);
            removeFileFromQueue(fileUpload);
            executeFileUpload(fileUpload);
        } finally {
            lock.unlock();
        }
    }

    private void executeFileUpload(FileUpload fileUpload) {
        executorService.execute(() -> {
            long currentTimeMillis = System.currentTimeMillis();
            FileClient fileClient = waitingFileClients.get(fileUpload.getClientId());
            fileClient.resetWaitingTime();
            FileThread fileThread = fileThreads.stream().filter(f -> !f.isActive()).findFirst().orElseThrow();
            fileThread.setActive(true);
            fileUploadBeginCallback.accept(fileClient, fileUpload, fileThread);
            fileThread.execute(fileUpload);
            fileUploadFinishedCallback.accept(fileThread);
            fileThread.setActive(false);
            log.info("Execution for file - [id - {}, owner - {}, size - {} MB] took: "
                    + (System.currentTimeMillis() - currentTimeMillis), fileUpload.getFileId(), fileUpload.getClientId(), fileUpload.getFileSize());
            performAuction();
        });
    }
}
