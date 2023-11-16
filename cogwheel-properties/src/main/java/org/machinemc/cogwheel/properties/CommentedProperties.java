package org.machinemc.cogwheel.properties;

import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CommentedProperties extends Properties {

    private final Map<String, @Nullable String[]> comments = new ConcurrentHashMap<>();
    private final SequencedSet<Object> orderedKeys = new LinkedHashSet<>();

    public void setComments(String key, @Nullable String[] value) {
        comments.put(key, value);
    }

    public Map<String, String[]> getComments() {
        return Collections.unmodifiableMap(comments);
    }

    public SequencedSet<Object> getOrderedKeys() {
        return orderedKeys;
    }

    public void store(Writer writer,
                      boolean colonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {

        char commentChar = exclamationMarkComments ? '!' : '#';
        String separator;
        if (colonSeparator) {
            separator = spacesBetweenSeparator ? ": " : ":";
        } else {
            separator = spacesBetweenSeparator ? " = " : "=";
        }

        String[] keys = orderedKeys.stream().map(String::valueOf).toArray(String[]::new);
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
                      boolean colonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {
        store(new OutputStreamWriter(os), colonSeparator, exclamationMarkComments, spacesBetweenSeparator, emptyLineBetweenEntries);
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

    @Override
    public synchronized Object put(Object key, Object value) {
        Object previous = super.put(key, value);
        orderedKeys.add(key);
        return previous;
    }

    @Override
    public synchronized Object remove(Object key) {
        Object previous = super.remove(key);
        orderedKeys.remove(key);
        return previous;
    }

    @Override
    public synchronized void putAll(Map<?, ?> t) {
        super.putAll(t);
        orderedKeys.addAll(t.keySet());
    }

    @Override
    public synchronized void clear() {
        super.clear();
        orderedKeys.clear();
    }

}
