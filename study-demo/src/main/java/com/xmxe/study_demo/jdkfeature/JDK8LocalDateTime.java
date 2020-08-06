package com.xmxe.study_demo.jdkfeature;

import java.time.Clock;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Month;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class JDK8LocalDateTime {
    // Instant：瞬时时间。
    // LocalDate：本地日期，不包含具体时间, 格式 yyyy-MM-dd。
    // LocalTime：本地时间，不包含日期. 格式 HH:mm:ss.SSS 。
    // LocalDateTime：组合了日期和时间，但不包含时差和时区信息。
    // ZonedDateTime：最完整的日期时间，包含时区和相对UTC或格林威治的时差。

    public void 获取当前时间(){
        //本地日期,不包括时分秒
        LocalDate nowDate = LocalDate.now();
        LocalTime localTime = LocalTime.now();
        //本地日期,包括时分秒
        LocalDateTime nowDateTime = LocalDateTime.now();
        System.out.println("当前时间:"+nowDate);//当前时间:2018-12-19
        System.out.println(localTime);//16:45:23.778
        System.out.println("当前时间:"+nowDateTime); //当前时间:2018-12-19T15:24:35.822
       
    }


    public void 获取年月日时分秒(){
        //获取当前的时间，包括毫秒
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("当前年:"+ldt.getYear());//当前年:2018
        System.out.println("当前年份天数:"+ldt.getDayOfYear());//当前年份天数:353
        System.out.println("当前月:"+ldt.getMonthValue());//当前月:12
        System.out.println("当前时:"+ldt.getHour());//当前时:15
        System.out.println("当前分:"+ldt.getMinute());//当前分:24
        System.out.println("当前时间:"+ldt.toString());//当前时间:2018-12-19T15:24:35.833
        
        LocalDateTime localDateTime = LocalDateTime.now();
        int dayOfYear = localDateTime.getDayOfYear();
        int dayOfMonth = localDateTime.getDayOfMonth();
        DayOfWeek dayOfWeek = localDateTime.getDayOfWeek();
        System.out.println("今天是" + localDateTime + "\n"
                + "本年当中第" + dayOfYear + "天" + "\n"
                + "本月当中第" + dayOfMonth + "天" + "\n"
                + "本周中星期" + dayOfWeek.getValue() + "-即" + dayOfWeek + "\n");                
        //今天是2018-03-28T17:11:15.180
        //本年当中第87天
        //本月当中第28天
        //本周中星期3-即WEDNESDAY

        //获取当天时间的年月日时分秒
        int year = localDateTime.getYear();
        Month month = localDateTime.getMonth();
        int day = localDateTime.getDayOfMonth();
        int hour = localDateTime.getHour();
        int minute = localDateTime.getMinute();
        int second = localDateTime.getSecond();
        System.out.println("今天是" + localDateTime + "\n"
                + "年 ： " + year + "\n"
                + "月 ： " + month.getValue() + "-即 "+ month + "\n"
                + "日 ： " + day + "\n"
                + "时 ： " + hour + "\n"
                + "分 ： " + minute + "\n"
                + "秒 ： " + second + "\n"
                );
        //今天是2018-03-28T17:11:15.180
        //年：2018
        //月：3-即MARCH
        //日：28
        //时：17
        //分：11
        //秒：35
        
    }

    public void 格式化时间(){
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("格式化时间: "+ ldt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        //格式化时间:2018-12-19 15:37:47.119

        //使用jdk自身配置好的日期格式
        DateTimeFormatter formatter1 = DateTimeFormatter.ISO_DATE_TIME;
        LocalDateTime date1 = LocalDateTime.now();
        //反过来调用也可以 : date1.format(formatter1);
        String date1Str = formatter1.format(date1);
        System.out.println(date1Str);//2018-01-14T16:50:43:27.2

        LocalDateTime date = LocalDateTime.now();
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String date2Str = formatter2.format(date);
        System.out.println(date2Str);//2018年01月14日 16:50:43:27

        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(formatter3.format(LocalDate.now()));//2018-03-28
        
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println(formatter4.format(LocalTime.now()));//18:59:36

        //将时间字符串转换为日期对象
        String datetime =  "2018-01-14 16:50:43";  
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt1 = LocalDateTime.parse(datetime, dtf);  
        System.out.println(ldt1);  //2018-01-14T16:50:43

        //将时间日期对象转为格式化后的时间日期对象
        //新的格式化API中，格式化后的结果都默认是String，有时我们也需要返回经过格式化的同类型对象
        LocalDateTime ldt2 = LocalDateTime.now();
        DateTimeFormatter dtf1 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String temp = dtf1.format(ldt2);
        System.out.println(temp);//2018-01-14 16:50:43
        LocalDateTime formatedDateTime = LocalDateTime.parse(temp, dtf1);
        System.out.println(formatedDateTime);//2018-01-14T16:50:43:27

        //long毫秒值转对象
        DateTimeFormatter df= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String longToDateTime = df.format(LocalDateTime.ofInstant(
        Instant.ofEpochMilli(System.currentTimeMillis()),ZoneId.of("Asia/Shanghai")));
        System.out.println(longToDateTime);
    }

    public void 对时间进行增加_减少年月日时分秒操作(){
        LocalDateTime ldt = LocalDateTime.now();
        System.out.println("后5天时间:"+ldt.plusDays(5));
        System.out.println("前5天时间并格式化:"+ldt.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("前一个月的时间:"+ldt.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMM")));
        System.out.println("后一个月的时间:"+ldt.plusMonths(1));
        System.out.println("指定2099年的当前时间:"+ldt.withYear(2099));
       //  后5天时间:2018-12-24T15:50:37.508
       //  前5天时间并格式化:2018-12-14
       //  前一个月的时间:201712
       //  后一个月的时间:2018-02-04T09:19:29.499
       //  指定2099年的当前时间:2099-12-19T15:50:37.508

        LocalDateTime localDateTime = LocalDateTime.now();
        //以下方法的参数都是long型，返回值都是LocalDateTime
        LocalDateTime plusYearsResult = localDateTime.plusYears(2L);
        LocalDateTime plusMonthsResult = localDateTime.plusMonths(3L);
        LocalDateTime plusDaysResult = localDateTime.plusDays(7L);
        LocalDateTime plusHoursResult = localDateTime.plusHours(2L);
        LocalDateTime plusMinutesResult = localDateTime.plusMinutes(10L);
        LocalDateTime plusSecondsResult = localDateTime.plusSeconds(10L);
                
        System.out.println("当前时间是 : " + localDateTime + "\n"
                + "当前时间加2年后为 : " + plusYearsResult + "\n"
                + "当前时间加3个月后为 : " + plusMonthsResult + "\n"
                + "当前时间加7日后为 : " + plusDaysResult + "\n"
                + "当前时间加2小时后为 : " + plusHoursResult + "\n"
                + "当前时间加10分钟后为 : " + plusMinutesResult + "\n"
                + "当前时间加10秒后为 : " + plusSecondsResult + "\n"
                );
        // 当前时间是：2018-03-28T17:04:18.623
        // 当前时间加2年后为：2020-03-28T17:04:18623
        // 当前时间加3个月后为：2018-06-28T17:04:18.623
        // 当前时间加7日后为：2018-04-04T17:04:18.623
        // 当前时间加2小时后为：
        // 当前时间加10分钟后为：2018-03-28T17:14:18.623
        // 当前时间加10秒后为：2018-03-28T17:04:28.623

        //也可以以另一种方式来相加减日期，即plus(long amountToAdd, TemporalUnit unit)
        // 参数1 ： 相加的数量， 参数2 ： 相加的单位
        LocalDateTime nextMonth = localDateTime.plus(1, ChronoUnit.MONTHS);
        LocalDateTime nextYear = localDateTime.plus(1, ChronoUnit.YEARS);
        LocalDateTime nextWeek = localDateTime.plus(1, ChronoUnit.WEEKS);
                
        System.out.println("now : " + localDateTime + "\n"
                + "nextYear : " + nextYear + "\n"
                + "nextMonth : " + nextMonth + "\n"
                + "nextWeek :" + nextWeek + "\n"
                );
        
        // now :2018-03-28T17:04:18.623
        // nextYear :2019-03-28T17:04:18:623
        // nextMonth :2018-04-28T17:04:18.623
        // nextweek ：2018-04-04T17:04:18.623
        
    }
    public void 创建指定时间(){
        LocalDate ld3=LocalDate.of(2017, Month.NOVEMBER, 17);
        LocalDate ld4=LocalDate.of(2018, 02, 11);
        LocalDate localDate = LocalDate.of(2018, 1, 13);
        LocalTime localTime = LocalTime.of(9, 43, 20);
        LocalDateTime localDateTime = LocalDateTime.of(2018, 1, 13, 9, 43, 20);
        System.out.println(ld3);
        System.out.println(ld4);
        System.out.println(localDate);//2018-01-13
        System.out.println(localTime);//09:43:20
        System.out.println(localDateTime);//2018-01-13T09:43:20
    }

    public void 将年月日修改为指定值(){
        LocalDate localDate = LocalDate.now();
        //当前时间基础上，指定本年当中的第几天，取值范围为1-365,366
        LocalDate withDayOfYearResult = localDate.withDayOfYear(200);
        //当前时间基础上，指定本月当中的第几天，取值范围为1-29,30,31
        LocalDate withDayOfMonthResult = localDate.withDayOfMonth(5);
        //当前时间基础上，直接指定年份
        LocalDate withYearResult = localDate.withYear(2017);
        //当前时间基础上，直接指定月份
        LocalDate withMonthResult = localDate.withMonth(5);
        System.out.println("当前时间是 : " + localDate + "\n"
                + "指定本年当中的第200天 : " + withDayOfYearResult + "\n"
                + "指定本月当中的第5天 : " + withDayOfMonthResult + "\n"
                + "直接指定年份为2017 : " + withYearResult + "\n"
                + "直接指定月份为5月 : " + withMonthResult + "\n"
                );
        //当前时间是 : 2018-03-28
        //指定本年当中的第200天 : 2018-07-19
        //指定本月当中的第5天 : 2018-03-05
        //直接指定年份为2017 :2017-03-28
        //直接指定月份为5月 : 2018-05-28

    }

    public void 时间相差比较(){
        //示例一: 具体相差的年月日
        LocalDate ld=LocalDate.parse("2017-11-17");
        LocalDate ld2=LocalDate.parse("2018-01-05");
        Period p=Period.between(ld, ld2);
        System.out.println("相差年: "+p.getYears()+" 相差月 :"+p.getMonths() +" 相差天:"+p.getDays());
        // 相差年: 0 相差月 :1 相差天:19
        //注:这里的月份是不满足一年，天数是不满足一个月的。这里实际相差的是1月19天，也就是49天。

        //示例二：相差总数的时间
        //ChronoUnit 日期周期单位的标准集合。

        LocalDate startDate = LocalDate.of(2017, 11, 17);
        LocalDate endDate = LocalDate.of(2018, 01, 05);
        System.out.println("相差月份:"+ChronoUnit.MONTHS.between(startDate, endDate));
        System.out.println("两月之间的相差的天数   : " + ChronoUnit.DAYS.between(startDate, endDate));
        // 相差月份:1
        // 两天之间的差在天数   : 49
        //注:ChronoUnit也可以计算相差时分秒。

        //示例三：精度时间相差
        //Duration 这个类以秒和纳秒为单位建模时间的数量或数量。

        Instant inst1 = Instant.now();
        System.out.println("当前时间戳 : " + inst1);
        Instant inst2 = inst1.plus(Duration.ofSeconds(10));
        System.out.println("增加之后的时间 : " + inst2);
        System.out.println("相差毫秒 : " + Duration.between(inst1, inst2).toMillis());
        System.out.println("相毫秒 : " + Duration.between(inst1, inst2).getSeconds());
        // 当前时间戳 : 2018-12-19T08:14:21.675Z
        // 增加之后的时间 : 2018-12-19T08:14:31.675Z
        // 相差毫秒 : 10000
        // 相毫秒 : 10

        //示例四：时间大小比较
        LocalDateTime ldt4 = LocalDateTime.now();
        LocalDateTime ldt5 = ldt4.plusMinutes(10);
        System.out.println("当前时间是否大于:"+ldt4.isAfter(ldt5));
        System.out.println("当前时间是否小于"+ldt4.isBefore(ldt5));
        // false
        // true

        //判断两个时间点的前后
        LocalDate localDate1 = LocalDate.of(2017, 8, 8);
        LocalDate localDate2 = LocalDate.of(2018, 8, 8);
        boolean date1IsBeforeDate2 = localDate1.isBefore(localDate2);
        System.out.println("date1IsBeforeDate2 : " + date1IsBeforeDate2);
        // date1IsBeforeDate2 == true

        //计算两个日期的日期间隔-年月日
        LocalDate date1 = LocalDate.of(2018, 2, 13);
        LocalDate date2 = LocalDate.of(2017, 3, 12);
        //内部是用date2-date1，所以得到的结果是负数
        Period period = Period.between(date1, date2);
        System.out.println("相差年数 ： " + period.getYears());
        System.out.println("相差月数 ： " + period.getMonths());
        System.out.println("相差日数 ： " + period.getDays());
        // 差年数:0
        // 相差月数:-11
        // 相差日数:-1
        long years = period.get(ChronoUnit.YEARS);
        long months = period.get(ChronoUnit.MONTHS);
        long days = period.get(ChronoUnit.DAYS);
        System.out.println("相差的年月日分别为 ： " + years + "," + months + "," + days);
        //注意，当获取两个日期的间隔时，并不是单纯的年月日对应的数字相加减，而是会先算出具体差多少天，在折算成相差几年几月几日的     
        // 相差的年月日分别为:0,-11, -1

        //计算两个时间的间隔
        LocalDateTime date3 = LocalDateTime.now();
        LocalDateTime date4 = LocalDateTime.of(2018, 1, 13, 22, 30, 10);
        Duration duration = Duration.between(date3, date4);
        System.out.println(date3 + " 与 " + date4 + " 间隔  " + "\n"
                + " 天 :" + duration.toDays() + "\n"
                + " 时 :" + duration.toHours() + "\n"
                + " 分 :" + duration.toMinutes() + "\n"
                + " 毫秒 :" + duration.toMillis() + "\n"
                + " 纳秒 :" + duration.toNanos() + "\n"
                );
        //注意，并没有获得秒差的，但既然可以获得毫秒，秒就可以自行获取了
       
        // 2018-03-28T18:56:12.427 与2018-01-13T22:30:10 间隔
        // 天:-73
        // 时:-1772
        // 分:-106346
        // 毫秒:-6380762427
        // 纳秒:-6380762427000000

        //替代System.currentTimeMillis()获取程序执行时间      
        Instant ins1 = Instant.now();
        for (int i = 0; i < 10000000; i++) {
        
        }
        Instant ins2 = Instant.now();
        Duration duration1 = Duration.between(ins1, ins2);
        System.out.println("程序运行耗时为 ： " + duration1.toMillis() + "毫秒");
    }

    public void 时区时间计算(){
        //得到其他时区的时间。

        //示例一:通过Clock时钟类获取计算
        //Clock时钟类用于获取当时的时间戳，或当前时区下的日期时间信息。

        Clock clock = Clock.systemUTC();
        System.out.println("当前时间戳 : " + clock.millis());
        Clock clock2 = Clock.system(ZoneId.of("Asia/Shanghai"));
        System.out.println("亚洲上海此时的时间戳:"+clock2.millis());
        Clock clock3 = Clock.system(ZoneId.of("America/New_York"));
        System.out.println("美国纽约此时的时间戳:"+clock3.millis());
        //  当前时间戳 : 1545209277657
        //  亚洲上海此时的时间戳:1545209277657
        //  美国纽约此时的时间戳:1545209277658

        //示例二:通过ZonedDateTime类和ZoneId
        ZoneId zoneId= ZoneId.of("America/New_York");
        ZonedDateTime dateTime=ZonedDateTime.now(zoneId);
        System.out.println("美国纽约此时的时间 : " + dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS")));
        System.out.println("美国纽约此时的时间 和时区: " + dateTime);
        // 美国纽约此时的时间 : 2018-12-19 03:52:22.494
        // 美国纽约此时的时间 和时区: 2018-12-19T03:52:22.494-05:00[America/New_York]
    }

    public void 时间戳(){
        Instant instant = Instant.now();
        //2019-06-08T16:50:19.174Z
        System.out.println(instant);
        Date date = Date.from(instant);
        Instant instant2 = date.toInstant();
        //Sun Jun 09 00:50:19 CST 2019
        System.out.println(date);
        //2019-06-08T16:50:19.174Z
        System.out.println(instant2);
    }
}