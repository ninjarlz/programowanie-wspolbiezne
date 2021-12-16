package pl.tul.service;

import lombok.Getter;

public class File implements Comparable<File> {

    public File(String id, long fileSize, String clientId) {
        this.id = id;
        this.fileSize = fileSize;
        this.clientId = clientId;
    }

    @Getter
    private final String id;
    @Getter
    private final long fileSize;
    @Getter
    private final String clientId;
    private final long initialTimestamp = System.currentTimeMillis();
    public long getWaitingTime() {
        return System.currentTimeMillis() - initialTimestamp;
    }

    @Override
    public int compareTo(File o) {
        return Long.compare(fileSize, o.fileSize);
    }
}
