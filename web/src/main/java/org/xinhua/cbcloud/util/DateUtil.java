package org.xinhua.cbcloud.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    /**
     * 将一个日期转换为指定格式的字符串
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToDateString(Date date, String format) {
        if (date == null || format == null) {
            return null;
        }
        DateFormat dateFormat;
        dateFormat = new SimpleDateFormat(format);
        return dateFormat.format(date);
    }

    /**
     * 获取当前日期，格式为"yyyy-MM-dd"
     *
     * @return String类型
     */
    public static String getCurrentDate() {
        return dateToDateString(new Date(), "yyyy-MM-dd");
    }

    /**
     * 获取当前日期，格式为"yyyy-MM-dd HH:mm:ss"
     *
     * @return String类型
     */
    public static String getCurrentDateNew() {
        return dateToDateString(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 字符串转换成日期
     *
     * @param str
     * @return date
     */
    public static Date StringToDate(String str) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
        Date date = null;
        try {
            date = format.parse(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return date;
    }
}
