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
import java.util.concurrent.TimeUnit;

import org.junit.Test;

public class JDK8LocalDateTime {
    // Instant：瞬时时间。
    // LocalDate：本地日期，不包含具体时间, 格式 yyyy-MM-dd。
    // LocalTime：本地时间，不包含日期. 格式 HH:mm:ss.SSS 。
    // LocalDateTime：组合了日期和时间，但不包含时差和时区信息。
    // ZonedDateTime：最完整的日期时间，包含时区和相对UTC或格林威治的时差。

    @Test
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

    @Test
    public void 获取年月日时分秒(){
        //获取当前的时间，包括毫秒
        LocalDateTime now = LocalDateTime.now();
        System.out.println("当前年:"+now.getYear());//当前年:2018
        System.out.println("本年当中第:"+now.getDayOfYear()+"天");//本年当中第:353 天
        System.out.println("当前月:"+now.getMonthValue());//当前月:12
        System.out.println("当前时:"+now.getHour());//当前时:15
        System.out.println("当前分:"+now.getMinute());//当前分:24
        System.out.println("当前秒："+now.getSecond());//当前秒：33
        System.out.println("当前时间:"+now.toString());//当前时间:2018-12-19T15:24:35.833
        
        int dayOfYear = now.getDayOfYear();
        int dayOfMonth = now.getDayOfMonth();
        DayOfWeek dayOfWeek = now.getDayOfWeek();
        System.out.println("今天是" + now + "\n"
                + "本年当中第" + dayOfYear + "天" + "\n"
                + "本月当中第" + dayOfMonth + "天" + "\n"
                + "本周中星期" + dayOfWeek.getValue() + "-即" + dayOfWeek + "\n");                
        //今天是2018-03-28T17:11:15.180
        //本年当中第87天
        //本月当中第28天
        //本周中星期3-即WEDNESDAY
        
    }

    @Test
    public void 格式化时间(){
        LocalDateTime now = LocalDateTime.now();

        //使用jdk自身配置好的日期格式
        DateTimeFormatter formatter1 = DateTimeFormatter.ISO_DATE_TIME;
        //反过来调用也可以:now.format(formatter1);
        String date1Str = formatter1.format(now);
        System.out.println(date1Str);//2018-01-14T16:50:43:27.2
        //自定义日期格式
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm:ss");
        String date2Str = formatter2.format(now);
        System.out.println(date2Str);//2018年01月14日 16:50:43:27

        DateTimeFormatter formatter3 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        System.out.println(formatter3.format(now));//2018-03-28
        
        DateTimeFormatter formatter4 = DateTimeFormatter.ofPattern("HH:mm:ss");
        System.out.println(formatter4.format(now));//18:59:36

        //将时间字符串转换为日期对象
        String datetime =  "2018-01-14 16:50:43";  
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime ldt1 = LocalDateTime.parse(datetime, dtf);  
        System.out.println(ldt1);  //2018-01-14T16:50:43
       
    }

    @Test
    public void 时间戳转换(){
        //Date Instant转换
        Instant instant = Instant.now();
        System.out.println(instant);//2019-06-08T16:50:19.174Z
        Date date = Date.from(instant);
        Instant instant2 = date.toInstant();
        System.out.println(date);//Sun Jun 09 00:50:19 CST 2019
        System.out.println(instant2);//2019-06-08T16:50:19.174Z

        //Instant转时间戳
        Instant now = Instant.now().plusMillis(TimeUnit.HOURS.toMillis(8));//TimeUnit.HOURS.toMillis():将小时转化为毫秒
        System.out.println("秒数:"+now.getEpochSecond());//秒数:1539170157
        System.out.println("毫秒数:"+now.toEpochMilli());//毫秒数:1539170157886

        //LocalDateTime转时间戳，比Instant多一步转换
        LocalDateTime localDateTime = LocalDateTime.now();
        //LocalDateTime转Instant
        Instant localDateTime2Instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        System.out.println("LocalDateTime 毫秒数:"+localDateTime2Instant.toEpochMilli());//LocalDateTime 毫秒数:1539141733010
        
        //时间戳转LocalDateTime
         DateTimeFormatter df= DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
         LocalDateTime local = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()),ZoneId.of("Asia/Shanghai"));
         String longToDateTime = df.format(local);
         System.out.println(longToDateTime);

        //替代System.currentTimeMillis()获取程序执行时间      
        Instant ins1 = Instant.now();
        for (int i = 0; i < 10000000; i++) {
            //循环
        }
        Instant ins2 = Instant.now();
        Duration duration1 = Duration.between(ins1, ins2);
        System.out.println("程序运行耗时为 ： " + duration1.toMillis() + "毫秒");
    }

    @Test
    public void 对时间进行增加_减少年月日时分秒操作(){
        LocalDateTime now = LocalDateTime.now();
        System.out.println("后5天时间:"+now.plusDays(5));
        System.out.println("前5天时间并格式化:"+now.minusDays(5).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        System.out.println("前一个月的时间:"+now.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyyMM")));
        System.out.println("后一个月的时间:"+now.plusMonths(1));
        System.out.println("指定2099年的当前时间:"+now.withYear(2099));
        // 后5天时间:2018-12-24T15:50:37.508
        // 前5天时间并格式化:2018-12-14
        // 前一个月的时间:201712
        // 后一个月的时间:2018-02-04T09:19:29.499
        // 指定2099年的当前时间:2099-12-19T15:50:37.508

        //以下方法的参数都是long型，返回值都是LocalDateTime
        LocalDateTime plusYearsResult = now.plusYears(2L);
        LocalDateTime plusMonthsResult = now.plusMonths(3L);
        LocalDateTime plusDaysResult = now.plusDays(7L);
        LocalDateTime plusHoursResult = now.plusHours(2L);
        LocalDateTime plusMinutesResult = now.plusMinutes(10L);
        LocalDateTime plusSecondsResult = now.plusSeconds(10L);
                
        System.out.println("当前时间是 : " + now + "\n"
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
        LocalDateTime nextMonth = now.plus(1, ChronoUnit.MONTHS);
        LocalDateTime nextYear = now.plus(1, ChronoUnit.YEARS);
        LocalDateTime nextWeek = now.plus(1, ChronoUnit.WEEKS);
                
        System.out.println("now : " + now + "\n"
                + "nextYear : " + nextYear + "\n"
                + "nextMonth : " + nextMonth + "\n"
                + "nextWeek :" + nextWeek + "\n"
                );
        
        // now :2018-03-28T17:04:18.623
        // nextYear :2019-03-28T17:04:18:623
        // nextMonth :2018-04-28T17:04:18.623
        // nextweek ：2018-04-04T17:04:18.623
        
    }

    @Test
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

    @Test
    public void 将年月日修改为指定值(){
        LocalDate now = LocalDate.now();
        //当前时间基础上，指定本年当中的第几天，取值范围为1-365,366
        LocalDate withDayOfYearResult = now.withDayOfYear(200);
        //当前时间基础上，指定本月当中的第几天，取值范围为1-29,30,31
        LocalDate withDayOfMonthResult = now.withDayOfMonth(5);
        //当前时间基础上，直接指定年份
        LocalDate withYearResult = now.withYear(2017);
        //当前时间基础上，直接指定月份
        LocalDate withMonthResult = now.withMonth(5);
        System.out.println("当前时间是 : " + now + "\n"
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

    @Test
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
    }

    @Test
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
}