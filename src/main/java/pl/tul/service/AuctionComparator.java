package pl.tul.service;

import lombok.AllArgsConstructor;

import java.util.Comparator;

@AllArgsConstructor
public class AuctionComparator implements Comparator<FileUpload> {

    private int numberOfFiles;

    private double getPriority(FileUpload fileUpload) {
        return ((double) numberOfFiles) / fileUpload.getFileSize() + (1. / numberOfFiles) * fileUpload.getWaitingTime();
    }


    @Override
    public int compare(FileUpload o1, FileUpload o2) {
        return Double.compare(getPriority(o1), getPriority(o2));
    }
}