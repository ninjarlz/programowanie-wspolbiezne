package pl.tul;

import pl.tul.service.FileThread;

public class Main {
    public static void main(String... args) {
        FileThread f = new FileThread(1000);
        f.run();
    }
}