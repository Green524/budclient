package com.chenum.task;

import com.chenum.cache.Cache;
import com.chenum.constant.LogLevel;
import com.chenum.log.LogReader;

import java.io.IOException;
import java.util.TimerTask;

public class AutoRefreshLogTask extends TimerTask {

    private final LogReader logReader;

    public AutoRefreshLogTask(LogReader logReader) {
        this.logReader = logReader;
    }

    @Override
    public void run() {
        LogLevel logLevel = (LogLevel) Cache.get("logLevel");
        try {
            logReader.read(logLevel);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
