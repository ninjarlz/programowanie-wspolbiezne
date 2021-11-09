package pl.tul.service;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class FileService {

    private final ThreadPoolExecutor executorService;

    public FileService(int threadsNum, boolean isWaitingTimeIncludedInPriority) {
        Comparator<Runnable> fileThreadComparator = getThreadsComparator(isWaitingTimeIncludedInPriority);
        executorService = new ThreadPoolExecutor(threadsNum, threadsNum,
                30, TimeUnit.SECONDS,
                new PriorityBlockingQueue<>(10, fileThreadComparator));
    }

    public void addFileThread(FileThread fileThread) {
        executorService.execute(fileThread);
    }

    public void shutdownExecutor() {
        executorService.shutdown();
    }

    public boolean isTerminated() {
        return executorService.isTerminated();
    }

    public List<FileThread> getWaitingFileThreads() {
        return executorService.getQueue().stream().map(t -> {
            throwIfWrongThreadType(t);
            return (FileThread) t;
        }).collect(Collectors.toList());
    }

    private Comparator<Runnable> getThreadsComparator(boolean isWaitingTimeIncludedInPriority) {
        return (o1, o2) -> {
            throwIfWrongThreadType(o1);
            throwIfWrongThreadType(o2);
            FileThread fileThread1 = (FileThread) o1;
            FileThread fileThread2 = (FileThread) o2;
            return compareFileThreads(fileThread1, fileThread2, isWaitingTimeIncludedInPriority);
        };
    }

    private int compareFileThreads(FileThread fileThread1, FileThread fileThread2,
                                   boolean isWaitingTimeIncludedInPriority) {
        if (!isWaitingTimeIncludedInPriority) {
            return Long.compare(fileThread1.getFileSize(), fileThread2.getFileSize());
        }
        long fileThread1Priority = getFileThreadPriority(fileThread1);
        long fileThread2Priority = getFileThreadPriority(fileThread2);
        return Long.compare(fileThread1Priority, fileThread2Priority);
    }

    private long getFileThreadPriority(FileThread fileThread) {
        return fileThread.getFileSize() - fileThread.getWaitingTime();
    }

    private void throwIfWrongThreadType(Runnable runnable) {
        if (runnable instanceof FileThread) {
            return;
        }
        throw new IllegalArgumentException("Wrong thread type!");
    }
}
