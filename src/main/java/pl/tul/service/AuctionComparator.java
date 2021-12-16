package pl.tul.service;

import lombok.AllArgsConstructor;

import java.util.Comparator;

@AllArgsConstructor
public class AuctionComparator implements Comparator<File> {

    private int numberOfFiles;

    private double getPriority(File file) {
        return ((double) numberOfFiles) / file.getFileSize() + (1. / numberOfFiles) * file.getWaitingTime();
    }

    @Override
    public int compare(File o1, File o2) {
        return Double.compare(getPriority(o1), getPriority(o2));
    }
}