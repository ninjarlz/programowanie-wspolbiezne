package pl.tul.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class FileUpload {
    private String fileId;
    private String clientId;
    private long fileSize;
    private long waitingTime;

    public FileUpload(String fileId, String clientId, long fileSize) {
        this.fileId = fileId;
        this.clientId = clientId;
        this.fileSize = fileSize;
        this.waitingTime = 0L;
    }
}
