package com.example;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by whatisjava on 17-2-9.
 */

public class TimeUtil {
    public static final String TAG = "TimeUtil";

    public static String timeFormat(String timestr) {
        SimpleDateFormat sdfTemp = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
        long timestamp = 0;
        try {
            timestamp = sdfTemp.parse(timestr).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        long time = (System.currentTimeMillis() - timestamp) / 1000;
        long monthSecond = 86400 * 30;
        long daySecond = 86400;
        long hourSecond = 3600;
        long minuteSecond = 60;
        if (time / monthSecond > 0) {
            return timestr.substring(0, 10);
        } else if (time / daySecond > 0) {
            return time / daySecond + "天前";
        } else if (time / hourSecond > 0) {
            return time / hourSecond + "小时前";
        } else if (time / minuteSecond > 0) {
            return time / minuteSecond + "分钟前";
        } else {
            return "刚刚";
        }
    }

    public static boolean compare(String time1, String time2) {
        //如果想比较日期则写成"yyyy-MM-dd"就可以了
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS", Locale.getDefault());
        //将字符串形式的时间转化为Date类型的时间
        long timestamp1 = 0;
        long timestamp2 = 0;
        try {
            timestamp1 = sdf.parse(time1).getTime();
            timestamp2 = sdf.parse(time2).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (timestamp1 - timestamp2 < 0)
            return true;
        else
            return false;
    }

    public static boolean isEnd(String time1) {
        {
//            LogUtils.d(TAG,"time1 "+time1);
            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS" ,Locale.getDefault());
            //将字符串形式的时间转化为Date类型的时间
            long timestamp1 = 0;
            long timestamp2 = System.currentTimeMillis();

            try {
                timestamp1 = sdf.parse(time1).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
//            LogUtils.d(TAG,"timestamp1 "+timestamp1);
//            LogUtils.d(TAG,"timestamp2 "+timestamp2);

            if (timestamp1  < timestamp2)
                return true;
            else
                return false;
        }

    }

    public static boolean isEnd(String time1,String time2) {
        {
//            LogUtils.d(TAG,"time1 "+time1);
            //如果想比较日期则写成"yyyy-MM-dd"就可以了
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:SS" ,Locale.getDefault());
            //将字符串形式的时间转化为Date类型的时间
            long timestamp1 = 0;
            long timestamp2 = 0;

            try {
                timestamp1 = sdf.parse(time1).getTime();
                timestamp2 =  sdf.parse(time2).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }
            LogUtils.d(TAG,"timestamp1 "+timestamp1);
            LogUtils.d(TAG,"timestamp2 "+timestamp2);

            if (timestamp1  < timestamp2)
                return true;
            else
                return false;
        }

    }

    /**
     * 返回精确到天数时间 2017-04-01
     *
     * @param time
     * @return
     */
    public static String getDayTime(String time) {
        LogUtils.d(TAG,"time "+time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd" ,Locale.getDefault());
        try {
            Date date=sdf.parse(time);
            time =sdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        LogUtils.d(TAG,"time day "+time);
        return time;
    }


    public static int diffDays(String time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentStr = sdf.format(new Date());
        int days=1;
        try {
            Date currentDate = sdf.parse(currentStr); //把时间1和时间2的时分秒毫秒全置为0
            Date diffDate = sdf.parse(time);
            days = differentDaysByMillisecond(diffDate, currentDate);
        } catch (ParseException e) {
            e.printStackTrace();
            LogUtils.e(TAG,"diffDays "+e.getMessage());
        }
        LogUtils.d(TAG,"time "+time+" days "+days);
        return days;
    }

    public static String getDayStr(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

     /**
     * 通过时间秒毫秒数判断两个时间的间隔,单纯需要天数,需要传入yyyy-MM-dd格式
     * @param date1
     * @param date2
     * @return
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        int days = (int) ((date2.getTime() - date1.getTime()) / (1000 * 3600 * 24));
        return days;
    }

    public static boolean isToday(String time){
        return  diffDays(time) == 0;
    }

//    public static String getMonth(String time){
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
//        // 创建 Calendar 对象
//        Calendar calendar = Calendar.getInstance();
//        try {
//            Date date = sdf.parse(time);
//            calendar.setTime(date);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//    }


    public static String getMonth(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getMonthDayHM(String time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
        SimpleDateFormat sdf2 = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        String str="";
        try {
            Date date = sdf.parse(time);
            str = sdf2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

}