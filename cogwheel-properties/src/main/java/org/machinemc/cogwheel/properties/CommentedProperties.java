package org.machinemc.cogwheel.properties;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class CommentedProperties extends Properties {

    private final Map<String, String[]> comments = new ConcurrentHashMap<>();

    public void setComment(String key, String[] value) {
        comments.put(key, value);
    }

    public Map<String, String[]> getComments() {
        return Collections.unmodifiableMap(comments);
    }

    public void store(Writer writer,
                      String[] topComments,
                      boolean semicolonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {

        Set<String> keys = keySet().stream().map(String::valueOf).collect(Collectors.toSet());
        char commentChar = exclamationMarkComments ? '!' : '#';
        char separatorChar = semicolonSeparator ? ':' : '=';
        String separator = spacesBetweenSeparator ? " " + separatorChar + " " : String.valueOf(separatorChar);

        if (topComments != null) {
            for (String comment : topComments) writer.write(commentChar + " " + comment + "\n");
            writer.write("\n");
        }

        for (String key : keys) {
            String[] comments = this.comments.get(key);
            if (comments != null) {
                for (String comment : comments) writer.write(commentChar + " " + comment + "\n");
            }
            writer.write(key);
            writer.write(separator);
            writer.write(saveConvert(String.valueOf(get(key))));

            writer.write("\n");
            if (emptyLineBetweenEntries) writer.write("\n");
        }
    }

    public void store(OutputStream os,
                      String[] topComments,
                      boolean semicolonSeparator,
                      boolean exclamationMarkComments,
                      boolean spacesBetweenSeparator,
                      boolean emptyLineBetweenEntries) throws IOException {
        store(new OutputStreamWriter(os), topComments, semicolonSeparator, exclamationMarkComments, spacesBetweenSeparator, emptyLineBetweenEntries);
    }

    private static String saveConvert(String string) {
        return saveConvert(string, false, true);
    }

    private static String loadConvert(String converted) {
        char[] chars = converted.toCharArray();
        return loadConvert(chars, 0, chars.length, new StringBuilder());
    }

    private static String loadConvert(char[] in, int off, int len, StringBuilder out) {
        char aChar;
        int end = off + len;
        int start = off;
        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                break;
            }
        }
        if (off == end) {
            return new String(in, start, len);
        }
        out.setLength(0);
        off--;
        out.append(in, start, off - start);

        while (off < end) {
            aChar = in[off++];
            if (aChar == '\\') {
                aChar = in[off++];
                if(aChar == 'u') {
                    // Read the xxxx
                    if (off > end - 4)
                        throw new IllegalArgumentException(
                                "Malformed \\uxxxx encoding.");
                    int value = 0;
                    for (int i = 0; i < 4; i++) {
                        aChar = in[off++];
                        value = switch (aChar) {
                            case '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' -> (value << 4) + aChar - '0';
                            case 'a', 'b', 'c', 'd', 'e', 'f'                     -> (value << 4) + 10 + aChar - 'a';
                            case 'A', 'B', 'C', 'D', 'E', 'F'                     -> (value << 4) + 10 + aChar - 'A';
                            default -> throw new IllegalArgumentException("Malformed \\uxxxx encoding.");
                        };
                    }
                    out.append((char)value);
                } else {
                    if (aChar == 't') aChar = '\t';
                    else if (aChar == 'r') aChar = '\r';
                    else if (aChar == 'n') aChar = '\n';
                    else if (aChar == 'f') aChar = '\f';
                    out.append(aChar);
                }
            } else {
                out.append(aChar);
            }
        }
        return out.toString();
    }

    private static String saveConvert(String theString, boolean escapeSpace, boolean escapeUnicode) {
        int len = theString.length();
        int bufLen = len * 2;
        if (bufLen < 0) {
            bufLen = Integer.MAX_VALUE;
        }
        StringBuilder outBuffer = new StringBuilder(bufLen);
        HexFormat hex = HexFormat.of().withUpperCase();
        for(int x=0; x<len; x++) {
            char aChar = theString.charAt(x);
            if ((aChar > 61) && (aChar < 127)) {
                if (aChar == '\\') {
                    outBuffer.append('\\'); outBuffer.append('\\');
                    continue;
                }
                outBuffer.append(aChar);
                continue;
            }
            switch(aChar) {
                case ' ':
                    if (x == 0 || escapeSpace)
                        outBuffer.append('\\');
                    outBuffer.append(' ');
                    break;
                case '\t':outBuffer.append('\\'); outBuffer.append('t');
                    break;
                case '\n':outBuffer.append('\\'); outBuffer.append('n');
                    break;
                case '\r':outBuffer.append('\\'); outBuffer.append('r');
                    break;
                case '\f':outBuffer.append('\\'); outBuffer.append('f');
                    break;
                case '=':
                case ':':
                case '#':
                case '!':
                    outBuffer.append('\\'); outBuffer.append(aChar);
                    break;
                default:
                    if (((aChar < 0x0020) || (aChar > 0x007e)) & escapeUnicode ) {
                        outBuffer.append("\\u");
                        outBuffer.append(hex.toHexDigits(aChar));
                    } else {
                        outBuffer.append(aChar);
                    }
            }
        }
        return outBuffer.toString();
    }

}
