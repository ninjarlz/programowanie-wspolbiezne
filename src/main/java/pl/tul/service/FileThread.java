package pl.tul.service;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.util.TriConsumer;

@Log4j2
public class FileThread {

    private static final long UPLOAD_UPDATE_STEP = 100;

    private final TriConsumer<File, FileThread, Long> fileUploadProcessingCallback;

    @Getter
    private final String id;

    @Getter
    @Setter
    private boolean isActive = false;

    public FileThread(String id, TriConsumer<File, FileThread, Long> fileUploadProcessingCallback) {
        this.id = id;
        this.fileUploadProcessingCallback = fileUploadProcessingCallback;
    }

    @SuppressWarnings("BusyWait")
    public void execute(File file) {
        try {
            log.info("File upload begin - id {}, owner {}, size {}", file.getId(), file.getClientId(), file.getFileSize());
            for (long i = 0; i < file.getFileSize(); i += UPLOAD_UPDATE_STEP) {
                long currentMillis = System.currentTimeMillis();
                fileUploadProcessingCallback.accept(file, this, i);
                long fileUploadProcessingCallbackTime = System.currentTimeMillis() - currentMillis;
                Thread.sleep(UPLOAD_UPDATE_STEP - fileUploadProcessingCallbackTime);
            }
            log.info("File upload finished - id {}, owner {}, size {}", file.getId(), file.getClientId(), file.getFileSize());
        } catch (InterruptedException e) {
            log.error("File upload error - id {}, owner {}, size {}, reason {}", file.getId(), file.getClientId(), file.getFileSize(), e);
        }
    }
}