package com.lostleon.qdrealestate.util;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Util {

	/**
	 * 把 "12-03" 格式的日期转换为Date对象
	 * 
	 * @param str
	 * @return
	 */
	public static Date str2date(String str) {
		DateFormat year = new SimpleDateFormat("yyyy");        
		Date date=new Date();
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		try {
			date = format.parse(year.format(date) + "-" + str + "-23:59:59");
		} catch (ParseException e) {
			return null;
		}
		return date;
	}
	
	/**
	 * 把 "yyyy-MM-dd HH:mm:ss" 格式的日期转换为Date对象
	 * 
	 * @param str
	 * @return
	 */
	public static Date str2dateComplex(String str) {
		Date d = null;
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			d = format.parse(str);
		} catch (ParseException e) {
			return null;
		}
		return d;
	}
	
	/**
	 * 把Date转换为"yyyy-MM-dd HH:mm:ss" 格式的字符串
	 * @param d
	 * @return
	 */
	public static String date2str(Date d) {
		DateFormat format = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
		return format.format(d);
	}
	
}
