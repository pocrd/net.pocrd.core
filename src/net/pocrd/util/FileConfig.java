package net.pocrd.util;

import java.io.File;

public class FileConfig {
    protected FileConfig(String name) {

    }

    public void save() {

    }

    public static <T extends FileConfig> void fillConfig(FileConfig config) {
        File directory = new File(".");
        System.out.println(directory.getAbsolutePath());
    }
}
