package com.chenum.log;

import com.chenum.constant.LogLevel;
import com.chenum.util.TimeUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class LogReader {


    private ReadAfterAdapter readAfterHandler;

    private long pointer;

    public LogReader setReadAfterHandler(ReadAfterAdapter readAfterHandler) {
        this.readAfterHandler = readAfterHandler;
        return this;
    }

    public void read(LogLevel logLevel) throws IOException, InterruptedException {
        try (RandomAccessFile randomAccessFile = new RandomAccessFile(new File(getFilePath(logLevel)), "r");){
            String line;
            randomAccessFile.seek(pointer);
            while ((line = randomAccessFile.readLine()) != null){
                byte[] originalBytes = line.getBytes(StandardCharsets.ISO_8859_1);
                readAfterHandler.handle(new String(originalBytes) + "\n");
            }
            pointer = randomAccessFile.getFilePointer();
        }
    }
    private String getFilePath(LogLevel logLevel) {
        StringBuilder sb = new StringBuilder();
        sb.append("./logs/")
                .append(logLevel.getContent())
                .append("/")
                .append(TimeUtil.now())
                .append("/")
                .append(logLevel.getContent())
                .append(".log");
        return sb.toString();
    }

    public LogReader setPointer(long pointer) {
        this.pointer = pointer;
        return this;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        new LogReader().read(LogLevel.INFO);

//        System.out.println(1 >> 1);
    }
}
