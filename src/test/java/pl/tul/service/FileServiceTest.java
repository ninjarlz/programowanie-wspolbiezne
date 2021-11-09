package pl.tul.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    @Test
    void singleThreadWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
        FileService fileService = new FileService(1, false);
        FileThread fileThread1 = new FileThread("A", 3000L);
        FileThread fileThread2 = new FileThread("B", 100L);
        FileThread fileThread3 = new FileThread("C", 50L);
        FileThread fileThread4 = new FileThread("D", 300L);
        FileThread fileThread5 = new FileThread("E", 3000L);
        FileThread fileThread6 = new FileThread("F", 600L);
        FileThread fileThread7 = new FileThread("G", 1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        fileService.addFileThread(fileThread3);
        fileService.addFileThread(fileThread4);
        fileService.addFileThread(fileThread5);
        fileService.addFileThread(fileThread6);
        fileService.addFileThread(fileThread7);
        fileService.shutdownExecutor();
        assertTrue(isNotWaitingForExecution(fileService, fileThread1));
        assertFalse(fileService.isTerminated());
        Thread.sleep(3100);
        assertTrue(isNotWaitingForExecution(fileService, fileThread3));
        assertFalse(fileService.isTerminated());
        Thread.sleep(150);
        assertTrue(isNotWaitingForExecution(fileService, fileThread2));
        assertFalse(fileService.isTerminated());
        Thread.sleep(200);
        assertTrue(isNotWaitingForExecution(fileService, fileThread4));
        assertFalse(fileService.isTerminated());
        Thread.sleep(400);
        assertTrue(isNotWaitingForExecution(fileService, fileThread6));
        assertFalse(fileService.isTerminated());
        Thread.sleep(700);
        assertTrue(isNotWaitingForExecution(fileService, fileThread7));
        assertFalse(fileService.isTerminated());
        Thread.sleep(1100);
        assertTrue(isNotWaitingForExecution(fileService, fileThread5));
        assertFalse(fileService.isTerminated());
        Thread.sleep(3100);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void multipleThreadsWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
        FileService fileService = new FileService(8, false);
        FileThread fileThread1 = new FileThread("A", 3000L);
        FileThread fileThread2 = new FileThread("B", 100L);
        FileThread fileThread3 = new FileThread("C", 50L);
        FileThread fileThread4 = new FileThread("D", 300L);
        FileThread fileThread5 = new FileThread("E", 3000L);
        FileThread fileThread6 = new FileThread("F", 600L);
        FileThread fileThread7 = new FileThread("G", 1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        fileService.addFileThread(fileThread3);
        fileService.addFileThread(fileThread4);
        fileService.addFileThread(fileThread5);
        fileService.addFileThread(fileThread6);
        fileService.addFileThread(fileThread7);
        fileService.shutdownExecutor();
        Thread.sleep(3100);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void singleThreadWaitingTimeIncludedSubmittedContinuouslyTest() throws Exception {
        FileService fileService = new FileService(1, true);
        FileThread fileThread1 = new FileThread("A", 3000L);
        FileThread fileThread2 = new FileThread("B", 1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        Thread.sleep(1500L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread3 = new FileThread("C", 200L);
        fileService.addFileThread(fileThread3);
        Thread.sleep(1000L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread4 = new FileThread("D", 10L);
        fileService.addFileThread(fileThread4);
        fileService.shutdownExecutor();
        Thread.sleep(2500L);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void singleThreadWaitingTimeNotIncludedSubmittedContinuouslyTest() throws Exception {
        FileService fileService = new FileService(1, false);
        FileThread fileThread1 = new FileThread("A", 3000L);
        FileThread fileThread2 = new FileThread("B", 1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        Thread.sleep(1500L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread3 = new FileThread("C", 200L);
        fileService.addFileThread(fileThread3);
        Thread.sleep(1000L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread4 = new FileThread("D", 10L);
        fileService.addFileThread(fileThread4);
        fileService.shutdownExecutor();
        Thread.sleep(2500L);
        assertTrue(fileService.isTerminated());
    }

    boolean isNotWaitingForExecution(FileService fileService, FileThread fileThread) {
        return fileService.getWaitingFileThreads()
                .stream().noneMatch(f -> f.getId().equals(fileThread.getId()));
    }
    
}