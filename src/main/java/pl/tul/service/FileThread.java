package pl.tul.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class FileThread implements Runnable {

    public FileThread(long fileSize) {
        this.fileSize = fileSize;
        creationTimestamp = System.currentTimeMillis();
    }

    @Getter
    private final long fileSize;
    private final long creationTimestamp;

    public long getWaitingTime() {
        return System.currentTimeMillis() - creationTimestamp;
    }

    @Override
    public void run() {
        try {
            log.info(fileSize + "Mb file - start of execution");
            Thread.sleep(fileSize);
            log.info(fileSize + "Mb file - end of execution");
        } catch (InterruptedException e) {
            log.error("File thread upload failed -", e);
        }
    }
}
