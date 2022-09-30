package com.chenum.util;

import java.time.LocalDate;

public class TimeUtil {

    /**
     * 返回年月日（2022-09-30）
     * @return
     */
    public static String now(){
        return LocalDate.now().toString();
    }

    public static void main(String[] args) {
        System.out.println(LocalDate.now().toString());
    }
}
