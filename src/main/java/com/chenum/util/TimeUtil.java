package com.chenum.util;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TimeUtil {

    public static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    private static final SimpleDateFormat sdf = new SimpleDateFormat(PATTERN);

    /**
     * 返回年月日（2022-09-30）
     * @return
     */
    public static String now(){
        return LocalDate.now().toString();
    }

    public static LocalDateTime parse(Date date){
        if (date == null){
            return LocalDateTime.now();
        }
        return LocalDateTime.parse(sdf.format(date), DateTimeFormatter.ofPattern(PATTERN));
    }

    public static String format(LocalDateTime date){
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(PATTERN);
        return dateTimeFormatter.format(date);
    }

}
