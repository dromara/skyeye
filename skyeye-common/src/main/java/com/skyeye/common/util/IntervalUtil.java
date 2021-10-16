package com.skyeye.common.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 *
 * @ClassName: IntervalUtil
 * @author: 卫志强
 * @date: 2021年2月15日
 */
public class IntervalUtil {

    /**
     * 判断data_value是否在interval区间范围内
     * @author: 卫志强
     * @date: 2021年2月15日
     * @param data_value 数值类型的
     * @param interval 正常的数学区间，包括无穷大等，如：(1,3)、>5%、(-∞,6]、(125%,135%)U(70%,80%)
     * @return true：表示data_value在区间interval范围内，false：表示data_value不在区间interval范围内
     */
    public static boolean isInTheInterval(String data_value, String interval) {
        // 将区间和data_value转化为可计算的表达式
        String formula = getFormulaByAllInterval(data_value,interval,"||");
        ScriptEngine jse = new ScriptEngineManager().getEngineByName("JavaScript");
        try {
            // 计算表达式
            return (Boolean) jse.eval(formula);
        } catch (Exception t) {
            return false;
        }
    }

    /**
     * 将所有阀值区间转化为公式：如
     * [75,80)   =》        date_value < 80 && date_value >= 75
     * (125%,135%)U(70%,80%)   =》        (date_value < 1.35 && date_value > 1.25) || (date_value < 0.8 && date_value > 0.7)
     * @param date_value
     * @param interval  形式如：(125%,135%)U(70%,80%)
     * @param connector 连接符 如：") || ("
     */
    private static String getFormulaByAllInterval(String date_value, String interval, String connector) {
        StringBuffer buff = new StringBuffer();
        for(String limit:interval.split("U")){//如：（125%,135%）U (70%,80%)
            buff.append("(").append(getFormulaByInterval(date_value, limit," && ")).append(")").append(connector);
        }
        String allLimitInvel = buff.toString();
        int index = allLimitInvel.lastIndexOf(connector);
        allLimitInvel = allLimitInvel.substring(0,index);
        return allLimitInvel;
    }

    /**
     * 将整个阀值区间转化为公式：如
     * 145)      =》         date_value < 145
     * [75,80)   =》        date_value < 80 && date_value >= 75
     * @param date_value
     * @param interval  形式如：145)、[75,80)
     * @param connector 连接符 如：&&
     */
    private static String getFormulaByInterval(String date_value, String interval, String connector) {
        StringBuffer buff = new StringBuffer();
        for(String halfInterval:interval.split(",")){//如：[75,80)、≥80
            buff.append(getFormulaByHalfInterval(halfInterval, date_value)).append(connector);
        }
        String limitInvel = buff.toString();
        int index = limitInvel.lastIndexOf(connector);
        limitInvel = limitInvel.substring(0,index);
        return limitInvel;
    }

    /**
     * 将半个阀值区间转化为公式：如
     * 145)      =》         date_value < 145
     * ≥80%      =》         date_value >= 0.8
     * [130      =》         date_value >= 130
     * <80%     =》         date_value < 0.8
     * @param halfInterval  形式如：145)、≥80%、[130、<80%
     * @param date_value
     * @return date_value < 145
     */
    private static String getFormulaByHalfInterval(String halfInterval, String date_value) {
        halfInterval = halfInterval.trim();
        if(halfInterval.contains("∞")){//包含无穷大则不需要公式
            return "1 == 1";
        }
        StringBuffer formula = new StringBuffer();
        String data = "";
        String opera = "";
        if(halfInterval.matches("^([<>≤≥\\[\\(]{1}(-?\\d+.?\\d*\\%?))$")){//表示判断方向（如>）在前面 如：≥80%
            opera = halfInterval.substring(0,1);
            data = halfInterval.substring(1);
        }else{//[130、145)
            opera = halfInterval.substring(halfInterval.length()-1);
            data = halfInterval.substring(0,halfInterval.length()-1);
        }
        double value = dealPercent(data);
        formula.append(date_value).append(" ").append(opera).append(" ").append(value);
        String a = formula.toString();
        // 转化特定字符
        return a.replace("[", ">=").replace("(", ">").replace("]", "<=").replace(")", "<").replace("≤", "<=").replace("≥", ">=");
    }

    /**
     * 去除百分号，转为小数
     * @param str 可能含百分号的数字
     * @return
     */
    private static double dealPercent(String str){
        double d = 0.0;
        if(str.contains("%")){
            str = str.substring(0,str.length()-1);
            d = Double.parseDouble(str)/100;
        }else{
            d = Double.parseDouble(str);
        }
        return d;
    }

    public static void main(String[] args) {
        IntervalUtil a = new IntervalUtil();
        System.out.println(a.isInTheInterval("7", "(-∞,6]"));
    }
}

