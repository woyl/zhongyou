package com.example;

public class MyClass {

    public static void main(String[] args){
        String str = "2017-08-03 17:58:08.0";
//        System.out.print(TimeUtil.isToday(str));
//        System.out.print(TimeUtil.getMonth(str));
        System.out.print(TimeUtil.getMonthDayHM(str));
    }

}
