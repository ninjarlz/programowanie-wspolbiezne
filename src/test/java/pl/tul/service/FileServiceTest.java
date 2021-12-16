package pl.tul.service;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class FileServiceTest {

    @Test
    void sumTest() {

        long i = 100 + (long) (new Random().nextDouble() * (5000 - 100));
        long g = 100 + (long) (new Random().nextDouble() * (5000 - 100));
        long k = 100 + (long) (new Random().nextDouble() * (5000 - 100));
//        File file1 = new File(100, "A");
//        File file2 = new File(200, "A");
//        File file3 = new File(300, "A");
//
//        FileClient fileClient1 = new FileClient("A", new ArrayList<>(List.of(file1, file2, file3)));
//
//        File file4 = new File(400, "B");
//        File file5 = new File(500, "B");
//
//        FileClient fileClient2 = new FileClient("B", new ArrayList<>(List.of(file4, file5)));
//
//        FileService fileService = new FileService(5);
//        fileService.addFileClient(fileClient1);
//        fileService.addFileClient(fileClient2);
//        int i = fileService.getFilesCount();
//        int j  = 0;
    }
//
//    @Test
//    void singleThreadWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
//        FileService fileService = new FileService(1, false);
//        FileClient fileClient1 = new FileClient("A", 3000L);
//        FileClient fileClient2 = new FileClient("B", 100L);
//        FileClient fileClient3 = new FileClient("C", 50L);
//        FileClient fileClient4 = new FileClient("D", 300L);
//        FileClient fileClient5 = new FileClient("E", 3000L);
//        FileClient fileClient6 = new FileClient("F", 600L);
//        FileClient fileClient7 = new FileClient("G", 1000L);
//        fileService.addFileClient(fileClient1);
//        fileService.addFileClient(fileClient2);
//        fileService.addFileClient(fileClient3);
//        fileService.addFileClient(fileClient4);
//        fileService.addFileClient(fileClient5);
//        fileService.addFileClient(fileClient6);
//        fileService.addFileClient(fileClient7);
//        fileService.shutdownExecutor();
//        assertTrue(isNotWaitingForExecution(fileService, fileClient1));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(3100);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient3));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(150);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient2));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(200);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient4));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(400);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient6));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(700);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient7));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(1100);
//        assertTrue(isNotWaitingForExecution(fileService, fileClient5));
//        assertFalse(fileService.isTerminated());
//        Thread.sleep(3100);
//        assertTrue(fileService.isTerminated());
//    }
//
//    @Test
//    void multipleThreadsWaitingTimeNotIncludedAllSubmittedAtOnceTest() throws Exception {
//        FileService fileService = new FileService(8, false);
//        FileClient fileClient1 = new FileClient("A", 3000L);
//        FileClient fileClient2 = new FileClient("B", 100L);
//        FileClient fileClient3 = new FileClient("C", 50L);
//        FileClient fileClient4 = new FileClient("D", 300L);
//        FileClient fileClient5 = new FileClient("E", 3000L);
//        FileClient fileClient6 = new FileClient("F", 600L);
//        FileClient fileClient7 = new FileClient("G", 1000L);
//        fileService.addFileClient(fileClient1);
//        fileService.addFileClient(fileClient2);
//        fileService.addFileClient(fileClient3);
//        fileService.addFileClient(fileClient4);
//        fileService.addFileClient(fileClient5);
//        fileService.addFileClient(fileClient6);
//        fileService.addFileClient(fileClient7);
//        fileService.shutdownExecutor();
//        Thread.sleep(3100);
//        assertTrue(fileService.isTerminated());
//    }
//
//    @Test
//    void singleThreadWaitingTimeIncludedSubmittedContinuouslyTest() throws Exception {
//        FileService fileService = new FileService(1, true);
//        FileClient fileClient1 = new FileClient("A", 3000L);
//        FileClient fileClient2 = new FileClient("B", 1000L);
//        fileService.addFileClient(fileClient1);
//        fileService.addFileClient(fileClient2);
//        Thread.sleep(1500L);
//        assertFalse(fileService.isTerminated());
//        FileClient fileClient3 = new FileClient("C", 200L);
//        fileService.addFileClient(fileClient3);
//        Thread.sleep(1000L);
//        assertFalse(fileService.isTerminated());
//        FileClient fileClient4 = new FileClient("D", 10L);
//        fileService.addFileClient(fileClient4);
//        fileService.shutdownExecutor();
//        Thread.sleep(2500L);
//        assertTrue(fileService.isTerminated());
//    }
//
//    @Test
//    void singleThreadWaitingTimeNotIncludedSubmittedContinuouslyTest() throws Exception {
//        FileService fileService = new FileService(1, false);
//        FileClient fileClient1 = new FileClient("A", 3000L);
//        FileClient fileClient2 = new FileClient("B", 1000L);
//        fileService.addFileClient(fileClient1);
//        fileService.addFileClient(fileClient2);
//        Thread.sleep(1500L);
//        assertFalse(fileService.isTerminated());
//        FileClient fileClient3 = new FileClient("C", 200L);
//        fileService.addFileClient(fileClient3);
//        Thread.sleep(1000L);
//        assertFalse(fileService.isTerminated());
//        FileClient fileClient4 = new FileClient("D", 10L);
//        fileService.addFileClient(fileClient4);
//        fileService.shutdownExecutor();
//        Thread.sleep(2500L);
//        assertTrue(fileService.isTerminated());
//    }
//
//    boolean isNotWaitingForExecution(FileService fileService, FileClient fileClient) {
//        return fileService.getWaitingFileClients()
//                .stream().noneMatch(f -> f.getId().equals(fileClient.getId()));
//    }
//
}