package com.xmxe.study_demo.util;

import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class LocalDateUtils {
    private LocalDateUtils() {}

    /**
     * 中文日期格式
     */
    public final static String DATE_CHINESE_PATTERN = "yyyy年MM月dd日";

    /**
     * 中文日期格式
     */
    public final static String CHINESE_YEAR_MONTH = "yyyy年MM月";

    /**
     * 中文日期格式
     */
    public final static String CHINESE_MONTH_DAY = "MM月dd日 HH:mm";

    /**
     * 标准日期格式
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 短日期格式
     */
    public final static String DATE_SHORT_PATTERN = "yyyyMMdd";

    /**
     * 斜线日期格式
     */
    public final static String DATE_SLASH_PATTERN = "yyyy/MM/dd";

    /**
     * 标准日期时分秒毫秒格式
     */
    public final static String DATETIME_MILL_SECOND = "yyyy-MM-dd HH:mm:ss.SSS";

    /**
     * 标准时间格式
     */
    public final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /**
     * 特殊的格式 针对创建订单，拼凑的最晚支付时间
     */
    public final static String DATETIME_PATTERN_CREAT_ORDER = "yyyy-MM-dd HH:mm";

    /**
     * 短日期时间格式
     */
    public final static String DATETIME_SHORT_PATTERN = "yyyyMMddHHmmss";

    /**
     * 标准月日格式
     */
    public final static String MONTH_DAY = "MM-dd";

    /**
     * 标准年小时分钟格式
     */
    public final static String HOUR_MINUTE = "HH:mm";

    /**
     * 标准年小时分钟秒格式
     */
    public final static String HOUR_MINUTE_SECOND = "HH:mm:ss";

    public final static String HOUR_MINUTE_SECOND_SHORT = "HHmmss";

    /**
     * Number of milliseconds in a standard day.
     */
    public static final long MILLIS_PER_DAY = 24 * LocalDateUtils.MILLIS_PER_HOUR;

    /**
     * Number of milliseconds in a standard hour.
     */
    public static final long MILLIS_PER_HOUR = 60 * LocalDateUtils.MILLIS_PER_MINUTE;

    /**
     * Number of milliseconds in a standard minute.
     */
    public static final long MILLIS_PER_MINUTE = 60 * LocalDateUtils.MILLIS_PER_SECOND;

    /**
     * Number of milliseconds in a standard second.
     */
    public static final long MILLIS_PER_SECOND = 1000;

    /**
     * 一小时是多少分钟
     */
    public static final int MINUTES_OF_AN_HOUR = 60;

    /**
     * 标准年月格式
     */
    public final static String MONTH_PATTERN = "yyyy-MM";

    /**
     * 标准年格式
     */
    public final static String YEAR_PATTERN = "yyyy";

    /**
     * 标准月日格式
     */
    public final static String MONTH_DAY_PATTERN = "MMdd";

    /**
     * 每天开始时间
     */
    public final static String TIME_START = "00:00:00";

    /**
     * 每天截止时间
     */
    public final static String TIME_END = "23:59:59";

    /**
     * 一天
     */
    private static final Integer ONE_DAY = 1;

    /**
     * 周一
     */
    private static final Integer MONDAY = 1;

    /**
     * 周日
     */
    private static final Integer SUNDAY = 7;

    private static final Integer DAY_COUNT_OF_WEEK = 7;

    /**
     * 日期时间 转为 日期
     *
     * @param date 日期时间
     * @return 日期
     */
    public static LocalDate toLocalDate(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.toLocalDate();
    }

    /**
     * 日期时间 转为 时间
     *
     * @param date 日期时间
     * @return 时间
     */
    public static LocalTime toLocalTime(LocalDateTime date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.toLocalTime();
    }

    /**
     * 在yyyy-MM-dd之后拼接：空格 + 00:00:00
     *
     * @param startDate
     * @return
     */
    public static String appendTimeStart(String startDate) {
        if (startDate.isBlank()) {
            return null;
        }
        return startDate.concat(" ").concat(TIME_START);
    }

    /**
     * 毫秒时间戳转日期时间 （默认为东八区的时间戳，即北京时间）
     *
     * @param timestamp 时间戳
     * @return
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 秒时间戳转日期时间 （默认为东八区的时间戳，即北京时间）
     *
     * @param timestamp 时间戳
     * @return
     */
    public static LocalDateTime secondToLocalDateTime(Long timestamp) {
        return Instant.ofEpochSecond(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
    }

    /**
     * 在yyyy-MM-dd之后拼接：空格 + 23:59:59
     *
     * @param endDate
     * @return
     */
    public static String appendTimeEnd(String endDate) {
        if (endDate.isBlank()) {
            return null;
        }
        return endDate.concat(" ").concat(TIME_END);
    }

    /**
     * 获取给定时间当天开始时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param dateTime 日期时间
     * @return 开始时间日期格式字符串 （yyyy-MM-dd hh:mm:ss）
     */
    public static String getStartFormatDateTime(LocalDateTime dateTime) {
        String formatDate = dateToString(dateTime.toLocalDate(), DATE_PATTERN);
        return appendTimeStart(formatDate);
    }

    /**
     * 获取给定时间当天开始时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param date 日期
     * @return 开始时间日期格式字符串 （yyyy-MM-dd hh:mm:ss）
     */
    public static String getStartFormatDateTime(LocalDate date) {
        String formatDate = dateToString(date, DATE_PATTERN);
        return appendTimeStart(formatDate);
    }

    /**
     * 获取给定时间当天的开始时间
     *
     * @param dateTime 日期时间
     * @return 开始日期时间
     */
    public static LocalDateTime getStartDateTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.MIN);
    }

    /**
     * 获取给定时间当天的开始时间
     *
     * @param date 日期
     * @return 开始日期时间
     */
    public static LocalDateTime getStartDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.MIN);
    }

    /**
     * 获取给定时间当天最后的时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param dateTime 日期
     * @return 结束时间日期格式字符串 （yyyy-MM-dd hh:mm:ss）
     */
    public static String getEndFormatDateTime(LocalDateTime dateTime) {
        String formatDate = dateToString(dateTime.toLocalDate(), DATE_PATTERN);
        return appendTimeEnd(formatDate);
    }

    /**
     * 获取给定时间当天最后的时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param date 日期
     * @return 结束时间日期格式字符串 （yyyy-MM-dd hh:mm:ss）
     */
    public static String getEndFormatDateTime(LocalDate date) {
        String formatDate = dateToString(date, DATE_PATTERN);
        return appendTimeEnd(formatDate);
    }

    /**
     * 获取给定时间当天最后的时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param dateTime 日期
     * @return 结束时间
     */
    public static LocalDateTime getEndDateTime(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(23, 59, 59));
    }

    /**
     * 获取给定时间当天最后的时间字符串 格式（yyyy-MM-dd hh:mm:ss）
     *
     * @param date 日期
     * @return 结束时间
     */
    public static LocalDateTime getEndDateTime(LocalDate date) {
        return LocalDateTime.of(date, LocalTime.of(23, 59, 59));
    }

    /**
     * 获取两个日期之间的所有日期 (不包含fromDate和toDate)
     *
     * @param fromDate 开始日期
     * @param toDate   结束日期
     * @return 日期列表
     */
    public static List<String> getBetweenDates(String fromDate, String toDate) {
        if (fromDate.isBlank() || toDate.isBlank()) {
            return new ArrayList<String>();
        }
        LocalDate startLocalDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern(DATE_PATTERN));
        LocalDate endLocalDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern(DATE_PATTERN));
        List<LocalDate> betweenDates = getBetweenDates(startLocalDate, endLocalDate);
        if (CollectionUtils.isEmpty(betweenDates)) {
            return new ArrayList<String>();
        }

        return betweenDates.stream()
                .map(betweenDay -> dateToString(betweenDay, DATE_PATTERN))
                .collect(Collectors.toList());
    }

    /**
     * 获取两个日期之间的所有日期 (不包含fromDate和toDate)
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getBetweenDates(LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return new ArrayList<LocalDate>();
        }
        // 日期从小到大
        LocalDate fromDate = startDate;
        LocalDate toDate = endDate;
        if (startDate.isAfter(endDate)) {

            fromDate = endDate;
            toDate = startDate;

        }

        // 返回的日期集合
        List<LocalDate> days = new ArrayList<>();
        LocalDate tempDate = fromDate.plusDays(ONE_DAY);
        while (tempDate.isBefore(toDate)) {

            days.add(tempDate);
            tempDate = tempDate.plusDays(ONE_DAY);

        }

        return days;
    }

    /**
     * 获取两个日期之间的所有日期 (不包含fromDate和toDate)
     *
     * @param fromDate 开始日期
     * @param toDate   结束日期
     * @return 日期列表
     */
    public static List<String> getDurationDates(String fromDate, String toDate) {
        if (fromDate.isBlank() || toDate.isBlank()) {
            return new ArrayList<String>();
        }
        LocalDate startLocalDate = LocalDate.parse(fromDate, DateTimeFormatter.ofPattern(DATE_PATTERN));
        LocalDate endLocalDate = LocalDate.parse(toDate, DateTimeFormatter.ofPattern(DATE_PATTERN));
        List<LocalDate> betweenDates = getDurationDates(startLocalDate, endLocalDate);
        if (CollectionUtils.isEmpty(betweenDates)) {
            return new ArrayList<>();
        }

        return betweenDates.stream()
                .map(betweenDay -> dateToString(betweenDay, DATE_PATTERN))
                .collect(Collectors.toList());
    }

    /**
     * 获取日期段的日期列表 （包含startDate和endDate）
     *
     * @param startDate 起始日期
     * @param endDate   结束日期
     * @return 日期列表
     */
    public static List<LocalDate> getDurationDates(LocalDate startDate, LocalDate endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return new ArrayList<LocalDate>();
        }
        // 日期从小到大
        LocalDate fromDate = startDate;
        LocalDate toDate = endDate;
        if (startDate.isAfter(endDate)) {
            fromDate = endDate;
            toDate = startDate;
        }

        // 返回的日期集合
        List<LocalDate> days = new ArrayList<>();
        LocalDate tempDate = fromDate;
        while (tempDate.isBefore(addDay(toDate, ONE_DAY))) {
            days.add(tempDate);
            tempDate = tempDate.plusDays(ONE_DAY);
        }

        return days;
    }

    /**
     * 获取某日所在星期的第一天（获取星期一）
     *
     * @param date 日期
     * @return 星期一
     */
    public static LocalDate getMondayOfWeek(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(WeekFields.of(DayOfWeek.MONDAY, DayOfWeek.MONDAY.getValue()).dayOfWeek(), MONDAY);
    }

    /**
     * 获取某日所在星期的第一天
     *
     * @param date   日期
     * @param locale 地区
     * @return 星期一
     */
    public static LocalDate getFirstDayOfWeek(LocalDate date, Locale locale) {
        if (Objects.isNull(date) || Objects.isNull(locale)) {
            return null;
        }
        return date.with(WeekFields.of(locale).dayOfWeek(), MONDAY);
    }

    /**
     * 获取某日所在星期的最后一天（获取星期日）
     *
     * @param date 日期
     * @return 星期日
     */
    public static LocalDate getSundayOfWeek(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(WeekFields.of(DayOfWeek.MONDAY, DayOfWeek.MONDAY.getValue()).dayOfWeek(), SUNDAY);
    }

    /**
     * 获取某日所在星期的最后一天
     *
     * @param date   日期
     * @param locale 地区
     * @return 星期日
     */
    public static LocalDate getLastDayOfWeek(LocalDate date, Locale locale) {
        if (Objects.isNull(date) || Objects.isNull(locale)) {
            return null;
        }
        return date.with(WeekFields.of(locale).dayOfWeek(), SUNDAY);
    }

    /**
     * 获取某日所在月份的第一天
     *
     * @param date
     * @return
     */
    public static LocalDate getFirstDayOfMonth(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfMonth());
    }

    /**
     * 获取某日所在月份的最后一天
     *
     * @param date
     * @return
     */
    public static LocalDate getLastDayOfMonth(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfMonth());
    }

    /**
     * 获取某日所在年份的第一天
     *
     * @param date
     * @return
     */
    public static LocalDate getFirstDayOfYear(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(TemporalAdjusters.firstDayOfYear());
    }

    /**
     * 获取某日所在年份的最后一天
     *
     * @param date
     * @return
     */
    public static LocalDate getLastDayOfYear(LocalDate date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return date.with(TemporalAdjusters.lastDayOfYear());
    }

    /**
     * 将日期时间字符串转化为日期时间。
     *
     * @param datetime 日期时间字符串
     * @param pattern  日期时间格式
     * @return 日期时间
     */
    public static LocalDateTime stringToDateTime(String datetime, String pattern) {
        if (datetime.isBlank() || pattern.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期字符串转化为日期。
     *
     * @param date    日期字符串
     * @param pattern 日期格式
     * @return 日期
     */
    public static LocalDate stringToDate(String date, String pattern) {
        if (date.isBlank() || pattern.isBlank()) {
            return null;
        }
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期时间字符串转化为时间。
     *
     * @param datetime 日期时间字符串
     * @param pattern  日期时间格式
     * @return 时间
     */
    public static LocalTime stringToTime(String datetime, String pattern) {
        if (datetime.isBlank() || pattern.isBlank()) {
            return null;
        }
        return LocalTime.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期转化为日期字符串。
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String datetimeToString(LocalDateTime date, String pattern) {
        if (Objects.isNull(date) || pattern.isBlank()) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * 将日期转化为日期字符串。
     *
     * @param date    日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String dateToString(LocalDate date, String pattern) {
        if (Objects.isNull(date) || pattern.isBlank()) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * 将日期转化为时间字符串。
     *
     * @param time    时间
     * @param pattern 日期格式
     * @return 时间字符串
     */
    public static String timeToString(LocalTime time, String pattern) {
        if (Objects.isNull(time) || pattern.isBlank()) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(time);
    }

    /**
     * 获取当前的时分秒
     *
     * @return 当前时间
     */
    public static LocalTime nowHMS() {
        return LocalTime.now();
    }

    /**
     * 获取当前时间
     *
     * @return 当前时间
     */
    public static LocalDateTime now() {
        return LocalDateTime.now();
    }

    /**
     * 获取当前日期
     *
     * @return 当前日期
     */
    public static LocalDate today() {
        return LocalDate.now();
    }

    /**
     * 增加日期的年份。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static LocalDate addYear(LocalDate date, Integer yearAmount) {
        if (Objects.isNull(date) || Objects.isNull(yearAmount)) {
            return null;
        }
        return date.plusYears(yearAmount);
    }

    /**
     * 增加日期的月份。
     *
     * @param date        日期
     * @param monthAmount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static LocalDate addMonth(LocalDate date, Integer monthAmount) {
        if (Objects.isNull(date) || Objects.isNull(monthAmount)) {
            return null;
        }
        return date.plusMonths(monthAmount);
    }

    /**
     * 增加日期的天数。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static LocalDate addDay(LocalDate date, Integer dayAmount) {
        if (Objects.isNull(date) || Objects.isNull(dayAmount)) {
            return null;
        }
        return date.plusDays(dayAmount);
    }

    /**
     * 增加星期的周数。
     *
     * @param date       日期
     * @param weekAmount 增加数量。可为负数
     * @return 增加周数后的日期
     */
    public static LocalDate addWeek(LocalDate date, Integer weekAmount) {
        if (Objects.isNull(date) || Objects.isNull(weekAmount)) {
            return null;
        }
        return date.plusWeeks(weekAmount);
    }

    /**
     * 增加日期的年份。
     *
     * @param date       日期
     * @param yearAmount 增加数量。可为负数
     * @return 增加年份后的日期
     */
    public static LocalDateTime addYear(LocalDateTime date, Integer yearAmount) {
        if (Objects.isNull(date) || Objects.isNull(yearAmount)) {
            return null;
        }
        return date.plusYears(yearAmount);
    }

    /**
     * 增加日期的月份。
     *
     * @param date        日期
     * @param monthAmount 增加数量。可为负数
     * @return 增加月份后的日期
     */
    public static LocalDateTime addMonth(LocalDateTime date, Integer monthAmount) {
        if (Objects.isNull(date) || Objects.isNull(monthAmount)) {
            return null;
        }
        return date.plusMonths(monthAmount);
    }

    /**
     * 增加日期的天数。
     *
     * @param date      日期
     * @param dayAmount 增加数量。可为负数
     * @return 增加天数后的日期
     */
    public static LocalDateTime addDay(LocalDateTime date, Integer dayAmount) {
        if (Objects.isNull(date) || Objects.isNull(dayAmount)) {
            return null;
        }
        return date.plusDays(dayAmount);
    }

    /**
     * 增加日期的小时。
     *
     * @param datetime   时间
     * @param hourAmount 增加数量。可为负数
     * @return 增加小时后的时间
     */
    public static LocalDateTime addHour(LocalDateTime datetime, Integer hourAmount) {
        if (Objects.isNull(datetime) || Objects.isNull(hourAmount)) {
            return null;
        }
        return datetime.plusHours(hourAmount);
    }

    /**
     * 增加日期的分钟。
     *
     * @param datetime     时间
     * @param minuteAmount 增加数量。可为负数
     * @return 增加分钟后的时间
     */
    public static LocalDateTime addMinute(LocalDateTime datetime, Integer minuteAmount) {
        if (Objects.isNull(datetime) || Objects.isNull(minuteAmount)) {
            return null;
        }
        return datetime.plusMinutes(minuteAmount);
    }

    /**
     * 增加日期的分钟。
     *
     * @param datetime     时间
     * @param minuteAmount 增加数量。可为负数
     * @return 增加分钟后的时间
     */
    public static LocalTime addMinute(LocalTime datetime, Integer minuteAmount) {
        if (Objects.isNull(datetime) || Objects.isNull(minuteAmount)) {
            return null;
        }
        return datetime.plusMinutes(minuteAmount);
    }

    /**
     * 增加日期的秒钟。
     *
     * @param datetime     时间
     * @param minuteAmount 增加数量。可为负数
     * @return 增加秒钟后的时间
     */
    public static LocalTime addSecond(LocalTime datetime, Integer minuteAmount) {
        if (Objects.isNull(datetime) || Objects.isNull(minuteAmount)) {
            return null;
        }
        return datetime.plusSeconds(minuteAmount);
    }

    /**
     * 增加日期的秒钟。
     *
     * @param datetime     时间
     * @param secondAmount 增加数量。可为负数
     * @return 增加秒钟后的时间
     */
    public static LocalDateTime addSecond(LocalDateTime datetime, Integer secondAmount) {
        if (Objects.isNull(datetime) || Objects.isNull(secondAmount)) {
            return null;
        }
        return datetime.plusSeconds(secondAmount);
    }

    /**
     * 获取日期的年份。
     *
     * @param date 日期
     * @return 年份
     */
    public static Integer getYear(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.getYear();
    }

    /**
     * 获取日期的月份。
     *
     * @param date 日期
     * @return 月份
     */
    public static Integer getMonth(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.getMonthValue();
    }

    /**
     * 获取日期是星期几（周日为0，周一为1，周二为2，...周六为6）。
     *
     * @param date 日期
     * @return 星期几
     */
    public static Integer getDayOfWeek(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.getDayOfWeek().getValue() % DAY_COUNT_OF_WEEK;
    }

    /**
     * 获取日期在当月的天数。
     *
     * @param date 日期
     * @return 天
     */
    public static Integer getDayOfMonth(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.getDayOfMonth();
    }

    /**
     * 获取日期在当年的天数。
     *
     * @param date 日期
     * @return 天
     */
    public static int getDayOfYear(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.getDayOfYear();
    }

    /**
     * 获取日期在当年的第几周。
     * weekOfWeekBasedYear第一周小于7天时返回1（周一到周四）或者去年的末尾周（周五到周日） weekOfYear 第一周小于7天时返回0
     * 该方法使用weekOfWeekBasedYear
     * <p>
     * 当年第一周规则如下：
     * 如果一年的第一天是星期一，则第一周从1日开始
     * 如果一年的第二天是星期一，第一周从第二天开始，然后第一天是在上一年的最后一周
     * 如果一年的第四天是星期一，第一周从第四天开始 第1到第3天在上一年的最后一周
     * 如果一年的第五天是星期一，第二周从第五天开始第一至第四周为第一周 第1至第4天为第一周
     *
     * @param date 日期
     * @return 第几周
     */
    public static int getWeekOfYear(LocalDate date) {
        Assert.notNull(date, "参数为null");
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        return date.get(weekFields.weekOfYear());
    }

    /**
     * 获取日期的小时。
     *
     * @param datetime 日期
     * @return 小时
     */
    public static int getHour(LocalDateTime datetime) {
        Assert.notNull(datetime, "参数为null");
        return datetime.getHour();
    }

    /**
     * 获取时间的分钟。
     *
     * @param datetime 时间
     * @return 分钟
     */
    public static int getMinute(LocalDateTime datetime) {
        Assert.notNull(datetime, "参数为null");
        return datetime.getMinute();
    }

    /**
     * 获取日期的秒钟。
     *
     * @param datetime 日期
     * @return 秒钟
     */
    public static int getSecond(LocalDateTime datetime) {
        Assert.notNull(datetime, "参数为null");
        return datetime.getSecond();
    }

    /**
     * 获取当前日期的秒数时间戳。
     *
     * @return
     */
    public static long getCurrentSecondTimestamp() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取当前日期的毫秒数时间戳。
     *
     * @return
     */
    public static long getCurrentMilliTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 获取指定日期的秒数时间戳。
     *
     * @param datetime 日期
     * @return
     */
    public static long getSecondTimestamp(LocalDateTime datetime) {
        Assert.notNull(datetime, "参数为null");
        return datetime.toEpochSecond(ZoneOffset.of("+8"));
    }

    /**
     * 获取指定日期的毫秒数时间戳。
     *
     * @param datetime 日期
     * @return
     */
    public static long getMilliTimestamp(LocalDateTime datetime) {
        Assert.notNull(datetime, "参数为null");
        return datetime.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }

    /**
     * 获取指定日期的毫秒数时间戳。
     *
     * @param date 日期
     * @return 毫秒值
     */
    public static long getMilliTimestamp(LocalDate date) {
        Assert.notNull(date, "参数为null");
        return date.atStartOfDay(ZoneOffset.ofHours(8)).toInstant().toEpochMilli();
    }

    /**
     * LocalDate转为Date类型
     *
     * @param localDate LocalDate
     * @return Date类型
     */
    public static Date asDate(LocalDate localDate) {
        if (Objects.isNull(localDate)) {
            return null;
        }
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * LocalDateTime转为Date类型
     *
     * @param localDateTime localDateTime
     * @return Date类型
     */
    public static Date asDate(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Date转LocalDate
     *
     * @param date Date
     * @return LocalDate
     */
    public static LocalDate asLocalDate(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime asLocalDateTime(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * 判断开始日期 - 截止日期是否超时
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isTimeout(LocalDateTime checkTime, LocalDateTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否超时
     *
     * @param checkTime  检查的时间 yyyy-MM-dd hh:mm:ss
     * @param expireTime 过期时间 yyyy-MM-dd hh:mm:ss
     * @return boolean
     */
    public static boolean isTimeout(String checkTime, String expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return expireTime.compareTo(checkTime) <= 0;
    }

    /**
     * 判断开始日期 - 截止日期是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfterOrEquals(LocalDateTime checkTime, LocalDateTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime) || checkTime.isEqual(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfterOrEquals(LocalDate checkTime, LocalDate expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime) || checkTime.isEqual(expireTime);
    }

    /**
     * 判断开始时间 - 截止时间是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfterOrEquals(LocalTime checkTime, LocalTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime) || checkTime.equals(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfter(LocalDateTime checkTime, LocalDateTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfter(LocalDate checkTime, LocalDate expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime);
    }

    /**
     * 判断开始时间 - 截止时间是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isAfter(LocalTime checkTime, LocalTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isAfter(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBeforeOrEquals(LocalDateTime checkTime, LocalDateTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime) || checkTime.isEqual(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBeforeOrEquals(LocalDate checkTime, LocalDate expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime) || checkTime.isEqual(expireTime);
    }

    /**
     * 判断开始时间 - 截止时间是否大于或者相等
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBeforeOrEquals(LocalTime checkTime, LocalTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime) || checkTime.equals(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBefore(LocalDateTime checkTime, LocalDateTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime);
    }

    /**
     * 判断开始日期 - 截止日期是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBefore(LocalDate checkTime, LocalDate expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime);
    }

    /**
     * 判断开始时间 - 截止时间是否大于
     *
     * @param checkTime  检查的时间
     * @param expireTime 过期时间
     * @return boolean
     */
    public static boolean isBefore(LocalTime checkTime, LocalTime expireTime) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(expireTime, "参数为null");
        return checkTime.isBefore(expireTime);
    }

    /**
     * 获取开始日期 - 截止日期间隔的年
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static Integer getIntervalYear(LocalDate startDate, LocalDate endDate) {
        Assert.notNull(startDate, "参数为null");
        Assert.notNull(endDate, "参数为null");
        // 转换成相差的年份
        return startDate.until(endDate).getYears();
    }

    /**
     * 获取开始日期 - 截止日期间隔的天
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static int getIntervalDay(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Assert.notNull(startDateTime, "参数为null");
        Assert.notNull(endDateTime, "参数为null");
        // 转换成相差的分钟
        long minute = (LocalDateUtils.getMilliTimestamp(endDateTime)
                - LocalDateUtils.getMilliTimestamp(startDateTime)) / (1000 * 60 * 60 * 24);
        return (int) minute;
    }

    /**
     * 获取开始日期 - 截止日期间隔的天
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int getIntervalDay(LocalDate startDate, LocalDate endDate) {
        Assert.notNull(startDate, "参数为null");
        Assert.notNull(endDate, "参数为null");
        // 转换成相差的分钟
        long minute = (LocalDateUtils.getMilliTimestamp(endDate)
                - LocalDateUtils.getMilliTimestamp(startDate)) / (1000 * 60 * 60 * 24);
        return (int) minute;
    }

    /**
     * 获取开始日期 - 截止日期间隔的天
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static int getIntervalDay(String startDateTime, String endDateTime) {
        Assert.hasLength(startDateTime, "参数为null");
        Assert.hasLength(endDateTime, "参数为null");
        return LocalDateUtils.getIntervalDay(LocalDateUtils.stringToDateTime(startDateTime,
                LocalDateUtils.DATETIME_PATTERN),
                LocalDateUtils.stringToDateTime(endDateTime,
                        LocalDateUtils.DATETIME_PATTERN));
    }

    /**
     * 获取开始日期 - 截止日期间隔的小时
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalHour(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Assert.notNull(startDateTime, "参数为null");
        Assert.notNull(endDateTime, "参数为null");
        // 转换成相差的分钟
        return (LocalDateUtils.getMilliTimestamp(endDateTime)
                - LocalDateUtils.getMilliTimestamp(startDateTime)) / (1000 * 60 * 60);
    }

    /**
     * 获取开始日期 - 截止日期间隔的小时
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalHour(String startDateTime, String endDateTime) {
        Assert.hasLength(startDateTime, "参数为null");
        Assert.hasLength(endDateTime, "参数为null");
        return LocalDateUtils.getIntervalHour(LocalDateUtils.stringToDateTime(startDateTime,
                LocalDateUtils.DATETIME_PATTERN),
                LocalDateUtils.stringToDateTime(endDateTime,
                        LocalDateUtils.DATETIME_PATTERN));
    }

    /**
     * 获取开始日期 - 截止日期间隔的分钟
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalMinute(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Assert.notNull(startDateTime, "参数为null");
        Assert.notNull(endDateTime, "参数为null");
        // 转换成相差的分钟
        return (LocalDateUtils.getMilliTimestamp(endDateTime)
                - LocalDateUtils.getMilliTimestamp(startDateTime)) / (1000 * 60);
    }

    /**
     * 获取开始日期 - 截止日期间隔的分钟
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalMinute(String startDateTime, String endDateTime) {
        Assert.hasLength(startDateTime, "参数为null");
        Assert.hasLength(endDateTime, "参数为null");
        return LocalDateUtils.getIntervalMinute(LocalDateUtils.stringToDateTime(startDateTime,
                LocalDateUtils.DATETIME_PATTERN),
                LocalDateUtils.stringToDateTime(endDateTime,
                        LocalDateUtils.DATETIME_PATTERN));
    }

    /**
     * 获取开始日期 - 截止日期间隔的秒钟
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalSecond(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Assert.notNull(startDateTime, "参数为null");
        Assert.notNull(endDateTime, "参数为null");
        // 转换成相差的秒数
        return (LocalDateUtils.getMilliTimestamp(endDateTime) - LocalDateUtils.getMilliTimestamp(startDateTime)) / 1000;
    }

    /**
     * 获取开始日期 - 截止日期间隔的秒钟
     *
     * @param startDateTime
     * @param endDateTime
     * @return
     */
    public static long getIntervalSecond(String startDateTime, String endDateTime) {
        Assert.notNull(startDateTime, "参数为null");
        Assert.notNull(endDateTime, "参数为null");
        return LocalDateUtils.getIntervalSecond(
                LocalDateUtils.stringToDateTime(startDateTime, LocalDateUtils.DATETIME_PATTERN),
                LocalDateUtils.stringToDateTime(endDateTime, LocalDateUtils.DATETIME_PATTERN));
    }

    /**
     * 求两个日期的较大值
     *
     * @param date1
     * @param date2
     * @return
     */
    public static LocalDateTime max(LocalDateTime date1, LocalDateTime date2) {
        Assert.notNull(date1, "参数为null");
        Assert.notNull(date2, "参数为null");
        return date1.isAfter(date2) ? date1 : date2;
    }

    /**
     * 求两个日期的较大值
     *
     * @param date1
     * @param date2
     * @return
     */
    public static LocalDate max(LocalDate date1, LocalDate date2) {
        Assert.notNull(date1, "参数为null");
        Assert.notNull(date2, "参数为null");
        return date1.isAfter(date2) ? date1 : date2;
    }

    /**
     * 求两个时间的较大值
     *
     * @param time1
     * @param time2
     * @return
     */
    public static LocalTime max(LocalTime time1, LocalTime time2) {
        Assert.notNull(time1, "参数为null");
        Assert.notNull(time2, "参数为null");
        return time1.isAfter(time2) ? time1 : time2;
    }

    /**
     * 求两个日期时间的较小值
     *
     * @param date1
     * @param date2
     * @return
     */
    public static LocalDateTime min(LocalDateTime date1, LocalDateTime date2) {
        Assert.notNull(date1, "参数为null");
        Assert.notNull(date2, "参数为null");
        return date1.isBefore(date2) ? date1 : date2;
    }

    /**
     * 求两个日期的较小值
     *
     * @param date1
     * @param date2
     * @return
     */
    public static LocalDate min(LocalDate date1, LocalDate date2) {
        Assert.notNull(date1, "参数为null");
        Assert.notNull(date2, "参数为null");
        return date1.isBefore(date2) ? date1 : date2;
    }

    /**
     * 求两个时间的较小值
     *
     * @param time1
     * @param time2
     * @return
     */
    public static LocalTime min(LocalTime time1, LocalTime time2) {
        Assert.notNull(time1, "参数为null");
        Assert.notNull(time2, "参数为null");
        return time1.isBefore(time2) ? time1 : time2;
    }

    /**
     * 计算某年第几周的第几天的日期
     *
     * @param year       年份
     * @param weekOfYear 一年中的第几星期
     * @param dayOfWeek  星期几
     * @return
     */
    public static LocalDate weekDateToDate(Integer year, Integer weekOfYear, DayOfWeek dayOfWeek) {
        if (weekOfYear > 54) {
            weekOfYear = 54;
        }
        WeekFields weekFields = WeekFields.of(DayOfWeek.MONDAY, 1);
        return LocalDate.now()
                .withYear(year)
                .with(weekFields.weekOfYear(), weekOfYear)
                .with(weekFields.dayOfWeek(), dayOfWeek.getValue());
    }

    /**
     * 根据日期和时间获取日期时间
     *
     * @param localDate 日期
     * @param localTime 时间
     * @return 时期时间
     */
    public static LocalDateTime of(LocalDate localDate, LocalTime localTime) {
        return LocalDateTime.of(localDate, localTime);
    }

    /**
     * 获取当前日期所属的第几季度
     *
     * @param localDate
     * @return
     */
    public static Integer getQuarter(LocalDate localDate) {
        Integer month = LocalDateUtils.getMonth(localDate);
        if (month >= 1 && month <= 3) {
            return 1;
        } else if (month >= 4 && month <= 6) {
            return 2;
        } else if (month >= 7 && month <= 9) {
            return 3;
        }
        return 4;
    }

    /**
     * 根据具体年份周数获取日期范围
     *
     * @param week
     * @return
     */
    public static String getWeekDays(int year, int week, int targetNum) {

        if (week + targetNum > 52) {
            year++;
            week += targetNum - 52;
        } else if (week + targetNum <= 0) {
            year--;
            week += targetNum + 52;
        } else {
            week += targetNum;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar cal = Calendar.getInstance();

        // 设置每周的开始日期
        cal.setFirstDayOfWeek(Calendar.MONDAY);

        cal.set(Calendar.YEAR, year);
        cal.set(Calendar.WEEK_OF_YEAR, week);

        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        String beginDate = sdf.format(cal.getTime());

        cal.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(cal.getTime());

        return beginDate + "~" + endDate;
    }

    // 获取当前时间所在年的周数
    public static Map<String, Integer> getWeekOfYear(Date date) {
        Map<String, Integer> map = new HashMap<>();
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(1);
        c.setTime(date);
        int weekYear = c.getWeekYear();
        map.put("weekYear", weekYear);
        map.put("week", c.get(Calendar.WEEK_OF_YEAR));
        return map;
    }

}
