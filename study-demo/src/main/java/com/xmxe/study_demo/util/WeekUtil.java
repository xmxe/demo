package com.xmxe.study_demo.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import cn.hutool.core.date.DateUtil;

public class WeekUtil {

    public static String nextday(String s) {
        String nextDate = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = df.parse(s);
            nextDate = df.format(nextday(dt));
        } catch (Exception e) {
            System.out.println("输⼊的⽇期格式有误！");
        }
        return nextDate;
    }

    public static String beforeDay(String s) {
        String nextDate = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dt = df.parse(s);
            nextDate = df.format(beforeDate(dt));
        } catch (Exception e) {
            System.out.println("输⼊的⽇期格式有误！");
        }
        return nextDate;
    }

    /**
     * 获取前n个月
     * 
     * @param n
     * @return
     */
    public static List<String> beforeMonths(int n) {
        Date date = new Date();
        List<String> listDate = new ArrayList<>();
        for (int i = -n + 1; i < 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, i);
            Date time = calendar.getTime();
            DateFormat dateformat = new SimpleDateFormat("yyyy-MM");
            String strdate = dateformat.format(time);
            listDate.add(strdate);
        }
        return listDate;
    }

    /**
     * 获取前n天
     * 
     * @param n
     * @return
     */
    public static List<String> beforeDays(int n) {
        Date date = new Date();
        List<String> listDate = new ArrayList<>();
        for (int i = -n + 1; i < 1; i++) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DATE, i);
            Date time = calendar.getTime();
            listDate.add(DateUtil.formatDate(time));
        }
        return listDate;
    }

    /**
     * 日期范围内的所有日期
     * 
     * @param startTime
     * @param endTime
     * @return
     */
    public static List<String> betweenDays(String startTime, String endTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 声明保存日期集合
        List<String> list = new ArrayList<String>();
        try {
            // 转化成日期类型
            Date startDate = sdf.parse(startTime);
            Date endDate = sdf.parse(endTime);

            // 用Calendar 进行日期比较判断
            Calendar calendar = Calendar.getInstance();
            while (startDate.getTime() <= endDate.getTime()) {
                // 把日期添加到集合
                list.add(sdf.format(startDate));
                // 设置日期
                calendar.setTime(startDate);
                // 把日期增加一天
                calendar.add(Calendar.DATE, 1);
                // 获取增加后的日期
                startDate = calendar.getTime();
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;

    }

    /**
     * 根据日期获取周数
     * 
     * @param time
     * @return
     */
    public static String getWeekInYear(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cl = Calendar.getInstance();
        cl.setFirstDayOfWeek(Calendar.MONDAY);
        try {
            cl.setTime(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int week = cl.get(Calendar.WEEK_OF_YEAR);
        cl.add(Calendar.DAY_OF_MONTH, -7);
        int year = cl.get(Calendar.YEAR);
        if (week < cl.get(Calendar.WEEK_OF_YEAR)) {
            year += 1;
        }
        return year + "-" + week;
    }

    /**
     * 根据日期得到日期在内的区间的周数
     * 
     * @param startDate
     * @param endDate
     * @return
     */
    public static Map<String, Object> getWeeksToDays(String startDate, String endDate) {
        LinkedHashSet<String> weekNum = new LinkedHashSet<>();
        // 每周对应的起止时间set
        LinkedHashSet<List<String>> weekDayScope = new LinkedHashSet<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            while (sdf.parse(endDate).compareTo(sdf.parse(startDate)) == 1) {
                // 每周起止日期
                List<String> dayOfWeekToStartAndEndDateList = new ArrayList<>();
                String weekStartDate = dateFormat.format(getThisWeekMonday(dateFormat.parse(startDate)));
                Calendar calendars = Calendar.getInstance();
                calendars.setTime(getThisWeekMonday(dateFormat.parse(startDate)));
                calendars.add(Calendar.DATE, 6);
                String weekEndDate = dateFormat.format(calendars.getTime());
                dayOfWeekToStartAndEndDateList.add(weekStartDate);
                dayOfWeekToStartAndEndDateList.add(weekEndDate);
                weekDayScope.add(dayOfWeekToStartAndEndDateList);

                // 周数
                weekNum.add(getWeekInYear(startDate));

                // 开始时间到下一周
                Date date;
                date = sdf.parse(startDate);
                // 日历时间工具类实例化创建，取得当前时间初值
                Calendar calendar = Calendar.getInstance();
                // 覆盖掉当前时间
                calendar.setTime(date);
                // +7
                calendar.add(Calendar.DATE, 7);
                // 转换回字符串
                startDate = sdf.format(calendar.getTime());
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        // 每周起止日期
        try {
            List<String> endDayOfWeekToStartAndEndDate = new ArrayList<>();
            String weekStartDate = dateFormat.format(getThisWeekMonday(dateFormat.parse(endDate)));
            Calendar calendars = Calendar.getInstance();
            calendars.setTime(getThisWeekMonday(dateFormat.parse(endDate)));
            calendars.add(Calendar.DATE, 6);
            String weekEndDate = dateFormat.format(calendars.getTime());
            endDayOfWeekToStartAndEndDate.add(weekStartDate);
            endDayOfWeekToStartAndEndDate.add(weekEndDate);
            weekDayScope.add(endDayOfWeekToStartAndEndDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // 周数
        weekNum.add(getWeekInYear(endDate));
        Map<String, Object> dayWeekEntity = new HashMap<>();
        dayWeekEntity.put("weekNum", weekNum);
        dayWeekEntity.put("weekDayScope", weekDayScope);
        return dayWeekEntity;
    }

    /**
     * 根据周数查日期
     * 慎用
     * 
     * @param time
     * @return
     */
    public static List<String> weekToDay(String time) {
        String[] split = time.split("-");
        if (split.length != 2) {
            return null;
        }
        Integer year = Integer.parseInt(split[0]);
        Integer week = Integer.parseInt(split[1]);
        List<String> date = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        int dayOfWeek = 7 - calendar.get(Calendar.DAY_OF_WEEK) + 2;// 算出第一周还剩几天 +1是因为1号是1天
        week = week - 2;// 周数减去第一周再减去要得到的周
        calendar.add(Calendar.DAY_OF_YEAR, week * 7 + dayOfWeek);
        date.add(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        date.add(new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        return date;
    }

    /**
     * 获取前n周的开始时间和结束时间
     * 
     * @param n
     * @return
     */
    public static List<String> beforeWeekDay(int n) {
        if (n < 0) {
            return new ArrayList<>();
        }
        List strList = new ArrayList();
        String startDate = DateUtil.formatDate(geLastWeekMonday(n * 7));
        String endDate = DateUtil.formatDate(geLastWeekMonday(n * 7 - 6));
        strList.add(startDate);
        strList.add(endDate);

        return strList;
    }

    private static Date nextday(Date d) {
        long daytime = 1 * 24 * 60 * 60 * 1000;
        Date nd = new Date(d.getTime() + daytime);
        return nd;
    }

    private static Date beforeDate(Date d) {
        long daytime = 1 * 24 * 60 * 60 * 1000;
        Date nd = new Date(d.getTime() - daytime);
        return nd;
    }

    private static Date geLastWeekMonday(int n) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getThisWeekMonday(new Date()));
        cal.add(Calendar.DATE, -n);
        return cal.getTime();
    }

    private static Date getThisWeekMonday(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        // 获得当前日期是一个星期的第几天
        int dayWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (1 == dayWeek) {
            cal.add(Calendar.DAY_OF_MONTH, -1);
        }
        // 设置一个星期的第一天，按中国的习惯一个星期的第一天是星期一
        cal.setFirstDayOfWeek(Calendar.MONDAY);
        // 获得当前日期是一个星期的第几天
        int day = cal.get(Calendar.DAY_OF_WEEK);
        // 根据日历的规则，给当前日期减去星期几与一个星期第一天的差值
        cal.add(Calendar.DATE, cal.getFirstDayOfWeek() - day);
        return cal.getTime();
    }

    /**
     * 根据第几周获取周一和周日的代码
     * 周的日期的格式202213，中间有个判断单独拿出来讲下，就是判断本年第一周的剩余天数是否大于3，根据这个准则来判断这一周属于不属于今年的第一周
     */
    public static Map<String, String> getTheSpecifiedYearAndWeekDate(String date) {
        date = date.replace("-", "");
        Integer year = Integer.valueOf(date.substring(0, 4));
        Integer week = Integer.valueOf(date.substring(4, 6));
        Map<String, String> timeMap = new HashMap<>();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, 0, 1);
        int zhouji = calendar.get(Calendar.DAY_OF_WEEK);
        System.out.println(calendar.get(Calendar.DAY_OF_WEEK));
        // 计算当年第一周还剩几天
        int dayOfWeek = 0;
        if (1 == zhouji) {
            dayOfWeek = 1;
        }
        if (2 == zhouji) {
            dayOfWeek = 7;
        }
        if (3 == zhouji) {
            dayOfWeek = 6;
        }
        if (4 == zhouji) {
            dayOfWeek = 5;
        }
        if (5 == zhouji) {
            dayOfWeek = 4;
        }
        if (6 == zhouji) {
            dayOfWeek = 3;
        }
        if (7 == zhouji) {
            dayOfWeek = 2;
        }
        System.out.println(dayOfWeek);
        if (dayOfWeek > 3) {
            week = week - 2;
        } else {
            week = week - 1;// 周数减去第一周再减去要得到的周
        }

        calendar.add(Calendar.DAY_OF_YEAR, week * 7 + dayOfWeek);
        timeMap.put("fisrstDay", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 6);
        timeMap.put("lastDay", new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime()));
        return timeMap;
    }

}
