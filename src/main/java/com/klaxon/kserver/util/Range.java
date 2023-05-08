package com.klaxon.kserver.util;

public class Range {
    private final long rangeStart;
    private final long rangeEnd;

    public Range(long rangeStart, long rangeEnd) {
        this.rangeStart = rangeStart;
        this.rangeEnd = rangeEnd;
    }

    public long getRangeStart(long contentLength) {
        if (rangeStart >= 0) {
            return rangeStart;
        } else {
            return contentLength + rangeStart;
        }
    }

    public long getRangeEnd(long contentLength) {
        if (rangeEnd >= 0) {
            return rangeEnd;
        } else {
            return contentLength + rangeEnd;
        }
    }

    public static Range parse(String rangeHeader) {
        String[] parts = rangeHeader.substring("bytes=".length()).split("-");
        long start = Long.parseLong(parts[0]);
        long end = parts.length > 1 ? Long.parseLong(parts[1]) : -1;
        return new Range(start, end);
    }
}
