package com.chenum.constant;

public enum LogLevel {
    DEBUG("debug"),
    ERROR("error"),
    INFO("info"),
    TRACE("trace"),
    WARN("warn");

    private final String content;


    LogLevel(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }


}
