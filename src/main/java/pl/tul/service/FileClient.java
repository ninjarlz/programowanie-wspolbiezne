package pl.tul.service;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.List;

@Getter
public class FileClient {
    private final String id;
    @Setter
    private List<File> fileList;
    private long initialTimestamp;
    public long getWaitingTime() {
        if (fileList.isEmpty()) {
            return 0L;
        }
        return System.currentTimeMillis() - initialTimestamp;
    }

    public void resetWaitingTime() {
        initialTimestamp = System.currentTimeMillis();
    }

    public FileClient(String id, List<File> fileList) {
        this.id = id;
        this.initialTimestamp = System.currentTimeMillis();
        this.fileList = Collections.synchronizedList(fileList);
    }
}
