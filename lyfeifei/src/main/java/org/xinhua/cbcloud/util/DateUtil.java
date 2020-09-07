package org.xinhua.cbcloud.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
    /**
     * 获取本月开始日期
     *
     * @return String
     **/
    public static String getMonthStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 0);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00";
    }

    /**
     * 获取本月最后一天
     *
     * @return String
     **/
    public static String getMonthEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    /**
     * 获取本周的第一天
     *
     * @return String
     **/
    public static String getWeekStart() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 0);
        cal.set(Calendar.DAY_OF_WEEK, 2);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 00:00:00";
    }

    /**
     * 获取本周的最后一天
     *
     * @return String
     **/
    public static String getWeekEnd() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_WEEK, cal.getActualMaximum(Calendar.DAY_OF_WEEK));
        cal.add(Calendar.DAY_OF_WEEK, 1);
        Date time = cal.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(time) + " 23:59:59";
    }

    /**
     * 获取本年的第一天
     *
     * @return String
     **/
    public static String getYearStart() {
        return new SimpleDateFormat("yyyy").format(new Date()) + "-01-01";
    }

    /**
     * 获取本年的最后一天
     *
     * @return String
     **/
    public static String getYearEnd() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, calendar.getActualMaximum(Calendar.MONTH));
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date currYearLast = calendar.getTime();
        return new SimpleDateFormat("yyyy-MM-dd").format(currYearLast) + " 23:59:59";
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
            //betweenTime.remove(endtime.replace(" 00:00:00", ""));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return betweenTime;
    }
}
