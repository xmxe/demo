package com.xmxe.util;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

public class LocalDateUtils {
    /**
     * 标准日期格式
     */
    public final static String DATE_PATTERN = "yyyy-MM-dd";

    /**
     * 标准时间格式
     */
    public final static String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    /*
     * Instant:瞬时时间。
     * LocalDate:本地日期,不包含具体时间,格式yyyy-MM-dd。
     * LocalTime:本地时间,不包含日期.格式HH:mm:ss.SSS。
     * LocalDateTime:组合了日期和时间,但不包含时差和时区信息。
     * ZonedDateTime:最完整的日期时间,包含时区和相对UTC或格林威治的时差。
     */

    /**
     * LocalDateTime转Date
     * 
     * @param localDateTime 日期时间
     * @return Date
     */
    public static Date localDateTime2Date(LocalDateTime localDateTime) {
        if (Objects.isNull(localDateTime)) {
            return null;
        }
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneId.systemDefault());
        // 将时区时间工具类，转换为时间戳
        Instant instant = zonedDateTime.toInstant();
        return Date.from(instant);
    }

    /**
     * Date转LocalDateTime
     *
     * @param date Date
     * @return LocalDateTime
     */
    public static LocalDateTime date2LocalDateTime(Date date) {
        if (Objects.isNull(date)) {
            return null;
        }
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * LocalDate转换LocalDateTime/Date
     * 
     * @param localDate 日期时间
     * @param type 1:转LocalDateTime(当天的开始时间) 2:转LocalDateTime(当前时刻) 3:转Date(当天的开始时间) 4:转Date(当前时刻)
     * @return 日期
     */
    public static Object localDateConvert(LocalDate localDate, int type) {
        if (Objects.isNull(localDate)) {
            return null;
        }
        switch (type) {
            case 1:
                // ZoneId.of("Asia/Shanghai")
                return localDate.atStartOfDay(ZoneId.systemDefault()).toLocalDateTime();
            case 2:
                return localDate.atTime(LocalTime.now());// atTime有许多重载方法
            case 3:
                return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            case 4:
                return Date.from(localDate.atTime(LocalTime.now()).atZone(ZoneId.systemDefault()).toInstant());
            default:
                return null;
        }
    }

    /**
     * 时间戳转LocalDateTime(默认为东八区的时间戳即北京时间)
     *
     * @param timestamp 时间戳
     * @return 日期
     */
    public static LocalDateTime toLocalDateTime(Long timestamp) {
        return Instant.ofEpochMilli(timestamp).atZone(ZoneOffset.ofHours(8)).toLocalDateTime();
        // return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp),ZoneId.of("Asia/Shanghai"));
    }

    /**
     * LocalDateTime转时间戳(默认为系统时区)
     *
     * @param localDateTime 时间
     * @return 时间戳
     */
    public static Long toTimeStamp(LocalDateTime localDateTime) {
        return localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    /**
     * 获取LocalDateTime之间的差值
     * 
     * @param start 开始时间
     * @param end 结束时间
     * @param type 1:返回毫秒数 2:返回秒数 3:返回分钟数 4:返回小时数 5:返回天数
     * 
     * @return
     */
    public static Long getMillisDif(LocalDateTime start, LocalDateTime end, int type) {
        Assert.notNull(start, "参数为null");
        Assert.notNull(end, "参数为null");
        Assert.isTrue(compareTime(start,end,2), "传入的开始时间比结束时间要大,请检查参数");
        // 计算LocalDate差值使用`Period period = Period.between(LocalDate, LocalDate);`
        // Duration.between(Instant, Instant)
        // ChronoUnit.MONTHS(DAYS...).between
        Duration duration = Duration.between(start, end);
        switch (type) {
            case 1:
                return duration.toMillis();
            case 2:
                return duration.toSeconds();
            case 3:
                return duration.toMinutes();
            case 4:
                return duration.toHours();
            case 5:
                return duration.toDays();
            default:
                throw new RuntimeException("请检查参数!");
        }
    }

    /**
     * 根据cycleType和dateType获取该周期单位上的开始或结束时间
     * 
     * @param localDateTime 时间
     * @param cycleType 1:年 2:月 3:日 4:周 5:季 6:小时
     * @param dateType 1:开始时间 2:结束时间
     * @see TemporalAdjusters
     * @return
     */
    public static LocalDateTime getTimeByCycle(LocalDateTime localDateTime, int cycleType, int dateType) {
        Assert.notNull(localDateTime, "参数为null");
        switch (cycleType) {
            case 1:
                return dateType == 1
                        ? localDateTime.with(TemporalAdjusters.firstDayOfYear()).toLocalDate().atStartOfDay()
                        : LocalDateTime.of(localDateTime.with(TemporalAdjusters.lastDayOfYear()).toLocalDate(), LocalTime.MAX);
            case 2:
                return dateType == 1
                        ? localDateTime.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay()
                        : LocalDateTime.of(localDateTime.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate(), LocalTime.MAX);
            case 3:
                return dateType == 1 
                        ? localDateTime.toLocalDate().atStartOfDay()
                        : LocalDateTime.of(localDateTime.toLocalDate(), LocalTime.MAX);
            case 4:
                return dateType == 1
                        ? localDateTime.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY)).toLocalDate().atStartOfDay()
                        : LocalDateTime.of(localDateTime.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY)).toLocalDate(), LocalTime.MAX);
            // return type == 1 ? date.with(WeekFields.of(DayOfWeek.MONDAY,DayOfWeek.MONDAY.getValue()).dayOfWeek(), 1) : date.with(WeekFields.of(DayOfWeek.MONDAY,DayOfWeek.MONDAY.getValue()).dayOfWeek(), 7);
            case 5:
                Month month = localDateTime.getMonth();
                Month firstMonthOfQuarter = month.firstMonthOfQuarter();
                Month endMonthOfQuarter = Month.of(firstMonthOfQuarter.getValue() + 2);
                return dateType == 1 
                        ? LocalDate.of(localDateTime.getYear(), firstMonthOfQuarter, 1).atStartOfDay()
                        : LocalDateTime.of(LocalDate.of(localDateTime.getYear(), endMonthOfQuarter, endMonthOfQuarter.length(localDateTime.toLocalDate().isLeapYear())), LocalTime.MAX);
            case 6:
                return dateType == 1
                        ? LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), localDateTime.getHour(), 0, 0)
                        : LocalDateTime.of(localDateTime.getYear(), localDateTime.getMonth(), localDateTime.getDayOfMonth(), localDateTime.getHour(), 59, 59);

            default:
                return null;
        }
    }

    /**
     * 将日期时间字符串转化为日期时间(String转LocalDateTime)
     *
     * @param datetime 日期时间字符串
     * @param pattern 日期时间格式
     * @return 日期时间
     */
    public static LocalDateTime string2LocalDateTime(String datetime, String pattern) {
        if (datetime.isBlank() || pattern.isBlank()) {
            return null;
        }
        return LocalDateTime.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期时间字符串转化为日期时间(String转LocalDateTime)
     *
     * @param datetime 日期时间字符串
     * @return 日期时间
     */
    public static LocalDateTime string2LocalDateTime(String datetime) {
        return string2LocalDateTime(datetime, DATETIME_PATTERN);
    }

    /**
     * 将日期时间字符串转化为日期时间(String转LocalDate)
     *
     * @param datetime 日期时间字符串
     * @param pattern 日期时间格式
     * @return 日期时间
     */
    public static LocalDate string2LocalDate(String datetime, String pattern) {
        if (datetime.isBlank() || pattern.isBlank()) {
            return null;
        }
        return LocalDate.parse(datetime, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 将日期时间字符串转化为日期时间(String转LocalDate)
     *
     * @param datetime 日期时间字符串
     * @return 日期时间
     */
    public static LocalDate string2LocalDate(String datetime) {
        return string2LocalDate(datetime, DATE_PATTERN);
    }

    /**
     * 将日期转化为日期字符串(LocalDateTime转String)
     *
     * @param date 日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String localDateTime2String(LocalDateTime date, String pattern) {
        if (Objects.isNull(date) || pattern.isBlank()) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * 将日期转化为日期字符串(LocalDateTime转String)
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String localDateTime2String(LocalDateTime date) {
        return localDateTime2String(date, DATETIME_PATTERN);
    }

    /**
     * 将日期转化为日期字符串(LocalDate转String)
     *
     * @param date 日期
     * @param pattern 日期格式
     * @return 日期字符串
     */
    public static String localDate2String(LocalDate date, String pattern) {
        if (Objects.isNull(date) || pattern.isBlank()) {
            return null;
        }
        return DateTimeFormatter.ofPattern(pattern).format(date);
    }

    /**
     * 将日期转化为日期字符串(LocalDate转String)
     *
     * @param date 日期
     * @return 日期字符串
     */
    public static String localDate2String(LocalDate date) {
        return localDate2String(date, DATE_PATTERN);
    }

    /**
     * 获取两个日期之间的所有日期(不包含fromDate和toDate)
     *
     * @param fromDate 开始日期 yyyy-MM-dd
     * @param toDate 结束日期 yyyy-MM-dd
     * @return 日期列表
     */
    public static List<String> getBetweenDates(String fromDate, String toDate) {
        if (fromDate.isBlank() || toDate.isBlank()) {
            return new ArrayList<String>();
        }
        LocalDate startLocalDate = string2LocalDate(fromDate);
        LocalDate endLocalDate = string2LocalDate(toDate);
        List<LocalDate> betweenDates = getBetweenDates(startLocalDate, endLocalDate);
        if (CollectionUtils.isEmpty(betweenDates)) {
            return new ArrayList<String>();
        }

        return betweenDates.stream()
                .map(betweenDay -> localDate2String(betweenDay))
                .collect(Collectors.toList());
    }

    /**
     * 获取两个日期之间的所有日期(不包含fromDate和toDate)
     *
     * @param startDate 开始日期
     * @param endDate 结束日期
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
        LocalDate tempDate = fromDate.plusDays(1);
        while (tempDate.isBefore(toDate)) {
            days.add(tempDate);
            tempDate = tempDate.plusDays(1);
        }
        return days;
    }

    /**
     * 获取两个日期之间的所有日期(包含fromDate和toDate)
     *
     * @param fromDate 开始日期 yyyy-MM-dd
     * @param toDate 结束日期 yyyy-MM-dd
     * @return 日期列表
     */
    public static List<String> getDurationDates(String fromDate, String toDate) {
        if (fromDate.isBlank() || toDate.isBlank()) {
            return new ArrayList<String>();
        }
        LocalDate startLocalDate = string2LocalDate(fromDate);
        LocalDate endLocalDate = string2LocalDate(toDate);
        List<LocalDate> betweenDates = getDurationDates(startLocalDate, endLocalDate);
        if (CollectionUtils.isEmpty(betweenDates)) {
            return new ArrayList<>();
        }

        return betweenDates.stream()
                .map(betweenDay -> localDate2String(betweenDay))
                .collect(Collectors.toList());
    }

    /**
     * 获取日期段的日期列表(包含startDate和endDate)
     *
     * @param startDate 起始日期
     * @param endDate 结束日期
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
        while (tempDate.isBefore(toDate.plusDays(1))) {
            days.add(tempDate);
            tempDate = tempDate.plusDays(1);
        }
        return days;
    }

    /**
     * LocalDate偏移日期
     *
     * @param date 日期
     * @param yearAmount 正数往未来偏移,负数往过去偏移
     * @param type 1:年 2:月 3:日 4:周
     * @return 增加后的日期
     */
    public static LocalDate offsetLocalDate(LocalDate date, int type, long amount) {
        if (Objects.isNull(date)) {
            return null;
        }
        switch (type) {
            case 1:
                return date.plusYears(amount);
            case 2:
                return date.plusMonths(amount);
            case 3:
                return date.plusDays(amount);
            case 4:
                return date.plusWeeks(amount);
            default:
                return null;
        }
    }

    /**
     * LocalDateTime偏移日期。
     *
     * @param date 日期
     * @param yearAmount 增加数量。可为负数
     * @param type 1:年 2:月 3:日 4:小时 5:分钟 6:秒
     * @return 增加年份后的日期
     */
    public static LocalDateTime offsetLocalDateTime(LocalDateTime date, int type, long amount) {
        if (Objects.isNull(date)) {
            return null;
        }
        switch (type) {
            case 1:
                return date.plusYears(amount);
            case 2:
                return date.plusMonths(amount);
            case 3:
                return date.plusDays(amount);
            case 4:
                return date.plusHours(amount);
            case 5:
                return date.plusMinutes(amount);
            case 6:
                return date.plusSeconds(amount);
            default:
                return null;
        }
    }

    /**
     * LocalDateTime比较日期
     *
     * @param checkTime 检查的时间
     * @param compareTime 时间
     * @param type 1:检查时间是否在指定时间后面 2:检查时间是否在指定时间前面 3:检查时间是与指定时间相同
     * @return boolean
     */
    public static boolean compareTime(LocalDateTime checkTime, LocalDateTime compareTime, int type) {
        Assert.notNull(checkTime, "参数为null");
        Assert.notNull(compareTime, "参数为null");
        switch (type) {
            case 1:
                return checkTime.isAfter(compareTime);
            case 2:
                return checkTime.isBefore(compareTime);
            case 3:
                return checkTime.isEqual(compareTime);
            default:
                return false;
        }
    }

    /**
     * 获取LocalDateTime间隔时间 只对比单位 例如2023-01-01 00 2023-01-11 09点 type传入4只返回`9`
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param type 1:年 2:月 3:日 4:时 5:分 6:秒
     * @return
     */
    public static Long until(LocalDateTime startDate, LocalDateTime endDate, int type) {
        Assert.notNull(startDate, "参数为null");
        Assert.notNull(endDate, "参数为null");
        // 转换成相差的年份
        switch (type) {
            case 1:
                return startDate.toLocalDate().until(endDate.toLocalDate(), ChronoUnit.YEARS);
            case 2:
                return startDate.toLocalDate().until(endDate.toLocalDate(), ChronoUnit.MONTHS);
            case 3:
                return startDate.toLocalDate().until(endDate.toLocalDate(), ChronoUnit.DAYS);
            case 4:
                return startDate.toLocalTime().until(endDate.toLocalTime(), ChronoUnit.HOURS);
            case 5:
                return startDate.toLocalTime().until(endDate.toLocalTime(), ChronoUnit.MINUTES);
            case 6:
                return startDate.toLocalTime().until(endDate.toLocalTime(), ChronoUnit.SECONDS);
            default:
                return null;
        }

    }

    /**
     * 计算某年第几周的第几天的日期
     *
     * @param year 年份
     * @param weekOfYear 一年中的第几星期
     * @param dayOfWeek 星期几
     * @return LocalDate
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
     * 获取当前日期所属的第几季度
     *
     * @param localDate 时间
     * @return Integer
     */
    public static Integer getQuarter(LocalDate localDate) {
        Integer month = localDate.getMonthValue();
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
     * 获取当前时间所在年的周数
     * @param date 时间
     */
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

    /**
     * 判断时间是否属于当月
     * 
     * @param date 日期
     */
    public static boolean isCurrentMonth(Date date) {
        ZoneId timeZone = ZoneId.systemDefault();
        LocalDateTime givenLocalDateTime = LocalDateTime.ofInstant(date.toInstant(), timeZone);
        YearMonth currentMonth = YearMonth.now(timeZone);
        return currentMonth.equals(YearMonth.from(givenLocalDateTime));
    }

    /**
     * 判断时间是否是当天
     * 
     * @param targetDate 时间
     */
    public static boolean isCurrentDay(Date targetDate) {
        // 获取当前日期
        LocalDate currentDate = LocalDate.now();
        // 将Date转换为LocalDate
        LocalDate targetLocalDate = targetDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return currentDate.equals(targetLocalDate);
    }

}
