package org.xinhua.cbcloud.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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

    /**
     * 计算两个时间段中间的日期
     * @param starttime
     * @param endtime
     * @return
     */
    public static List<String> getBetweenTime(String starttime, String endtime) {
        List<String> betweenTime = new ArrayList<String>();
        try {
            Date sdate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(starttime);
            Date edate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(endtime);

            SimpleDateFormat outformat = new SimpleDateFormat("yyyy-MM-dd");

            Calendar sCalendar = Calendar.getInstance();
            sCalendar.setTime(sdate);
            int year = sCalendar.get(Calendar.YEAR);
            int month = sCalendar.get(Calendar.MONTH);
            int day = sCalendar.get(Calendar.DATE);
            sCalendar.set(year, month, day, 0, 0, 0);

            Calendar eCalendar = Calendar.getInstance();
            eCalendar.setTime(edate);
            year = eCalendar.get(Calendar.YEAR);
            month = eCalendar.get(Calendar.MONTH);
            day = eCalendar.get(Calendar.DATE);
            eCalendar.set(year, month, day, 0, 0, 0);

            while (sCalendar.before(eCalendar)) {
                betweenTime.add(outformat.format(sCalendar.getTime()));
                sCalendar.add(Calendar.DAY_OF_YEAR, 1);
            }
            betweenTime.add(outformat.format(eCalendar.getTime()));

            // 不包含当天
            betweenTime.remove(endtime.replace(" 00:00:00", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return betweenTime;
    }

    public static void main(String[] args) throws Exception {
        List<String> lists = getBetweenTime("2020-08-17 00:00:00", "2020-08-24 00:00:00");

        List<Map<String, Object>> tmpList = new LinkedList<>();

        Map<String, Object> tmp = new HashMap<>();
        for (int i = 0; i < 7; i++) {
            tmp.put("2020-08-17", "11");
            tmp.put("2020-08-18", "22");
//            tmp.put("2020-08-19", "0");
//            tmp.put("2020-08-20", "0");
//            tmp.put("2020-08-21", "0");
//            tmp.put("2020-08-22", "0");
//            tmp.put("2020-08-23", "0");
        }
        tmpList.add(tmp);
//        System.out.println(tmpList.toString());

        for (int i = 0; i < lists.size(); i++) {
            if (tmp.get(lists.get(i)) == null) {
                tmp.put(lists.get(i), "0");
                tmpList.get(0).put(lists.get(i), "0");
            }
        }

        System.out.println(tmpList.toString());

        List<Map<String, Object>> resultList = new ArrayList<>();
        for (Map.Entry map : tmpList.get(0).entrySet()) {
            Map<String, Object> resultTmp = new HashMap<>();
            resultTmp.put("data", map.getKey());
            resultTmp.put("value", map.getValue());
            resultList.add(resultTmp);
        }

        System.out.println(resultList.toString());
    }
}
