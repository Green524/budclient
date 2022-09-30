package com.chenum.log;

import com.chenum.util.TimeUtil;

import java.io.*;

public class LogReader {


    public static void read() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("./logs/info/" + TimeUtil.now() + "/info.log"));
        String line = null;
        while ((line = reader.readLine()) != null){
            System.out.println(line);
        }
    }

    public static void main(String[] args) throws IOException {
        read();
    }
}
