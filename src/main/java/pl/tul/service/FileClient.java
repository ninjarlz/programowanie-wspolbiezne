package pl.tul.service;

import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public class FileClient {
    private final String id;
    private final List<File> fileList;

    public FileClient(String id, List<File> fileList) {
        this.id = id;
        this.fileList = Collections.synchronizedList(fileList);
    }
}
