package pl.tul.service;

import lombok.Getter;

import java.awt.*;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class RandomGenerator {

    private static final String FILE_CLIENT_ID_PREFIX = "FILE-CLIENT-";
    private final AtomicLong fileClientIdCounter = new AtomicLong();
    private static final String FILE_ID_PREFIX = "FILE-";
    private final AtomicLong fileIdCounter = new AtomicLong();
    private static final long FILE_SIZE_SMALL_LOWER_LIMIT = 1000L;
    private static final long FILE_SIZE_SMALL_UPPER_LIMIT = 4000L;
    private static final long FILE_SIZE_BIG_LOWER_LIMIT = 15000L;
    private static final long FILE_SIZE_BIG_UPPER_LIMIT = 30000L;
    private static final long UPLOAD_UPDATE_STEP = 100;
    private final Random random = new Random();

    public String generateFileClientId() {
        return FILE_CLIENT_ID_PREFIX + fileClientIdCounter.incrementAndGet();
    }

    public String generateFileId() {
        return FILE_ID_PREFIX + fileIdCounter.incrementAndGet();
    }

    public long getRandomSmallFileSize() {
        long size = FILE_SIZE_SMALL_LOWER_LIMIT + (long) (random.nextDouble() * (FILE_SIZE_SMALL_UPPER_LIMIT - FILE_SIZE_SMALL_LOWER_LIMIT));
        return (size / UPLOAD_UPDATE_STEP) * UPLOAD_UPDATE_STEP;
    }

    public long getRandomBigFileSize() {
        long size = FILE_SIZE_BIG_LOWER_LIMIT + (long) (random.nextDouble() * (FILE_SIZE_BIG_UPPER_LIMIT - FILE_SIZE_BIG_LOWER_LIMIT));
        return (size / UPLOAD_UPDATE_STEP) * UPLOAD_UPDATE_STEP;

    }

    public Color getRandomColor() {
        final float hue = random.nextFloat();
        final float saturation = 0.9f;
        final float luminance = 1.0f;
        return Color.getHSBColor(hue, saturation, luminance);
    }
}
