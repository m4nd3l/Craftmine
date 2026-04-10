package dev.m4nd3l.craftmine.renderer.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

public class MFile {

    private final File file;

    public MFile(File file) { this.file = file; }
    public MFile(String... path) { this(new File(String.join(File.separator, path))); }
    public MFile(MFile file, String... path) { this(new File(file.getPath(), String.join(File.separator, path))); }

    // region STRINGS
    public String readString() {
        if (!file.exists()) return "";
        try {
            return Files.readString(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public void writeString(String text, boolean append) {
        try {
            file.getParentFile().mkdirs();

            if (append) {
                Files.writeString(
                        file.toPath(),
                        text,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } else {
                Files.writeString(
                        file.toPath(),
                        text,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // endregion
    // region BYTES
    public byte[] readBytes() {
        if (!file.exists()) return new byte[0];
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public void writeBytes(byte[] data, boolean append) {
        try {
            file.getParentFile().mkdirs();

            if (append) {
                Files.write(
                        file.toPath(),
                        data,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.APPEND
                );
            } else {
                Files.write(
                        file.toPath(),
                        data,
                        StandardOpenOption.CREATE,
                        StandardOpenOption.TRUNCATE_EXISTING
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    // endregion
    // region UTILS
    public File getFile() { return file; }
    public String getPath() { return file.getAbsolutePath(); }
    public boolean exists() { return file.exists(); }

    public void create() { try {file.createNewFile();} catch (IOException e) { System.err.println(e); } }
    public void create(String content) {
        try { file.createNewFile(); }
        catch (IOException e) { System.err.println(e); }
        writeString(content,false);
    }

    @Override
    public String toString() { return "MFile: " + file.getAbsolutePath(); }
    // endregion
}