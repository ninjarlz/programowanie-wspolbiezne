package pl.tul.service;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Getter
public class FileThread implements Runnable {

    public FileThread(String id, long fileSize) {
        this.id = id;
        this.fileSize = fileSize;
        creationTimestamp = System.currentTimeMillis();
    }

    private final String id;
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
