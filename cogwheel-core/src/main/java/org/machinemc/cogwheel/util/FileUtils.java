package org.machinemc.cogwheel.util;

import java.io.File;
import java.io.IOException;

public final class FileUtils {

    private FileUtils() {
        throw new UnsupportedOperationException();
    }

    public static void createIfAbsent(File file) {
        if (file.exists())
            return;
        try {
            if (!file.getParentFile().exists() && !file.getParentFile().mkdirs())
                throw new IOException("Couldn't create parent directories for '" + file + "'");
            if (!file.createNewFile())
                throw new IOException("Couldn't create file '" + file + "'");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
