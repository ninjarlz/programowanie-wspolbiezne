package pl.tul.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    @Test
    void singleThreadWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
        FileService fileService = new FileService(1, false);
        FileThread fileThread1 = new FileThread(3000L);
        FileThread fileThread2 = new FileThread(100L);
        FileThread fileThread3 = new FileThread(50L);
        FileThread fileThread4 = new FileThread(300L);
        FileThread fileThread5 = new FileThread(3000L);
        FileThread fileThread6 = new FileThread(600L);
        FileThread fileThread7 = new FileThread(1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        fileService.addFileThread(fileThread3);
        fileService.addFileThread(fileThread4);
        fileService.addFileThread(fileThread5);
        fileService.addFileThread(fileThread6);
        fileService.addFileThread(fileThread7);
        fileService.shutdownExecutor();
        assertFalse(fileService.isTerminated());
        Thread.sleep(4500);
        assertFalse(fileService.isTerminated());
        Thread.sleep(4500);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void multipleThreadsWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
        FileService fileService = new FileService(4, false);
        FileThread fileThread1 = new FileThread(3000L);
        FileThread fileThread2 = new FileThread(100L);
        FileThread fileThread3 = new FileThread(50L);
        FileThread fileThread4 = new FileThread(300L);
        FileThread fileThread5 = new FileThread(3000L);
        FileThread fileThread6 = new FileThread(600L);
        FileThread fileThread7 = new FileThread(1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        fileService.addFileThread(fileThread3);
        fileService.addFileThread(fileThread4);
        fileService.addFileThread(fileThread5);
        fileService.addFileThread(fileThread6);
        fileService.addFileThread(fileThread7);
        fileService.shutdownExecutor();
        Thread.sleep(9000);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void singleThreadWaitingTimeIncludedSubmittedContinuouslyTest() throws Exception {
        FileService fileService = new FileService(1, true);
        FileThread fileThread1 = new FileThread(3000L);
        FileThread fileThread2 = new FileThread(1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        Thread.sleep(1500L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread3 = new FileThread(200L);
        fileService.addFileThread(fileThread3);
        Thread.sleep(1000L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread4 = new FileThread(10L);
        fileService.addFileThread(fileThread4);
        fileService.shutdownExecutor();
        Thread.sleep(2500L);
        assertTrue(fileService.isTerminated());
    }

    @Test
    void singleThreadWaitingTimeNotIncludedSubmittedContinuouslyTest() throws Exception {
        FileService fileService = new FileService(1, false);
        FileThread fileThread1 = new FileThread(3000L);
        FileThread fileThread2 = new FileThread(1000L);
        fileService.addFileThread(fileThread1);
        fileService.addFileThread(fileThread2);
        Thread.sleep(1500L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread3 = new FileThread(200L);
        fileService.addFileThread(fileThread3);
        Thread.sleep(1000L);
        assertFalse(fileService.isTerminated());
        FileThread fileThread4 = new FileThread(10L);
        fileService.addFileThread(fileThread4);
        fileService.shutdownExecutor();
        Thread.sleep(2500L);
        assertTrue(fileService.isTerminated());
    }
}