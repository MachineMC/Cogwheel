package org.machinemc.cogwheel.properties;

import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CommentedProperties extends Properties {

    private final Map<String, @Nullable String[]> comments = new ConcurrentHashMap<>();

    public void setComments(String key, @Nullable String[] value) {
        comments.put(key, value);
    }

    public Map<String, String[]> getComments() {
        return Collections.unmodifiableMap(comments);
    }

    public void store(Writer writer,
                      boolean semicolonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {

        Set<String> keys = keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        char commentChar = exclamationMarkComments ? '!' : '#';
        String separator;
        if (semicolonSeparator) {
            separator = spacesBetweenSeparator ? ": " : ":";
        } else {
            separator = spacesBetweenSeparator ? " = " : "=";
        }

        for (String key : keys) {
            String[] comments = this.comments.get(key);
            if (comments != null) {
                for (String comment : comments) {
                    String commentLine = comment == null ? "" : commentChar + " " + comment;
                    writer.write(commentLine + "\n");
                }
            }
            writer.write(key);
            writer.write(separator);
            writer.write(saveConvert(String.valueOf(get(key))));

            writer.write("\n");
            if (emptyLineBetweenEntries) writer.write("\n");
        }
    }

    public void store(OutputStream os,
                      boolean semicolonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {
        store(new OutputStreamWriter(os), semicolonSeparator, exclamationMarkComments, spacesBetweenSeparator, emptyLineBetweenEntries);
    }

    private static String saveConvert(String string) {
        return saveConvert(string, false, true);
    }

    private static String saveConvert(String string, boolean escapeSpace, boolean escapeUnicode) {
        int length = string.length();
        int bufferLength = length * 2;
        if (bufferLength < 0) bufferLength = Integer.MAX_VALUE;
        StringBuilder outBuffer = new StringBuilder(bufferLength);
        for (int i = 0; i < length; i++) {
            char aChar = string.charAt(i);
            if (aChar > 0x3D && aChar < 0x7F) {
                if (aChar == '\\') {
                    outBuffer.append("\\\\");
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch (aChar) {
                case ' ' -> {
                    if (i == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                }
                case '\t' -> outBuffer.append("\\t");
                case '\n' -> outBuffer.append("\\n");
                case '\r' -> outBuffer.append("\\r");
                case '\f' -> outBuffer.append("\\f");
                case '=', ':', '#', '!' -> outBuffer.append('\\').append(aChar);
                default -> {
                    if ((0x20 > aChar || aChar > 0x7E) && escapeUnicode) {
                        outBuffer.append("\\u%04X".formatted((int) aChar));
                    } else {
                        outBuffer.append(aChar);
                    }
                }
            }
        }
        return outBuffer.toString();
    }

}
