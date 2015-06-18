package cn.com.gps169.common.tool;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
    private static transient final Logger LOGGER = LoggerFactory.getLogger(DateUtil.class);

    /**
     * 格式：yyyy
     */
    public static SimpleDateFormat YEARFORMATER() {
        return new SimpleDateFormat("yyyy");
    }

    /**
     * 格式：yyyyMM
     */
    public static SimpleDateFormat MONTHFORMATER() {
        return new SimpleDateFormat("yyyyMM");
    }

    /**
     * 格式：yyyy年MM月
     */
    public static SimpleDateFormat MONTHFORMATERSTR() {
        return new SimpleDateFormat("yyyy年MM月");
    }

    /**
     * 格式：yyyyMMdd
     */
    public static SimpleDateFormat DATEFORMATER() {
        return new SimpleDateFormat("yyyyMMdd");
    }

    /**
     * 格式：yyyy年MM月dd日
     */
    public static SimpleDateFormat DATEFORMATER1() {
        return new SimpleDateFormat("yyyy年MM月dd日");
    }

    /**
     * 格式：yyyy-MM-dd
     */
    public static SimpleDateFormat DATEFORMATER2() {
        return new SimpleDateFormat("yyyy-MM-dd");
    }

    /**
     * 格式：MM-dd HH:mm
     */
    public static SimpleDateFormat DATEFORMATER3() {
        return new SimpleDateFormat("yyMMddHHmmss");
    }

    /**
     * 格式：yyyyMMdd-HH:mm:ss
     */
    public static SimpleDateFormat TIMEFORMATER() {
        return new SimpleDateFormat("yyyyMMdd-HH:mm:ss");
    }

    /**
     * 格式：yyyy-MM-dd HH:mm:ss
     */
    public static SimpleDateFormat TIMEFORMATER1() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    }

    /**
     * 格式：yyMMddHHmmss
     */
    public static SimpleDateFormat TIMEFORMATER3() {
        return new SimpleDateFormat("yyMMddHHmmss");
    }

    /**
     * 格式：MM-dd HH:mm
     */
    public static SimpleDateFormat TIMEFORMATER4() {
        return new SimpleDateFormat("MM-dd HH:mm");
    }

    /**
     * 格式：yyyyMMddHHmmss
     */
    public static SimpleDateFormat TIMEFORMATER_yyyyMMddHHmmss() {
        return new SimpleDateFormat("yyyyMMddHHmmss");
    }

    public static Date formatTime(String timeStr) {

        Date date = null;
        try {
            date = TIMEFORMATER().parse(timeStr);
        } catch (Exception e) {
            String errorMsg = String.format("解析日期失败，非法的日期格式[%s], 正确格式为[yyyyMMdd-HH:mm:ss]", timeStr);
            LOGGER.trace(errorMsg);
        }

        return date;
    }
    public static Date stringToDatetime(String timeStr){
    	
    	Date date = null;
    	try {
    		date = TIMEFORMATER1().parse(timeStr);
    	} catch (Exception e) {
    		String errorMsg = String.format("解析日期失败，非法的日期格式[%s], 正确格式为[yyyy-MM-dd HH:mm:ss]", timeStr);
    		LOGGER.trace(errorMsg);
    	}
    	
    	return date;
    }

    public static Date formatDate(String dateStr) {

        Date date = null;
        try {
            date = DATEFORMATER().parse(dateStr);
        } catch (Exception e) {
            String errorMsg = String.format("解析日期失败，非法的日期格式[%s]", dateStr);
            LOGGER.trace(errorMsg);
        }

        return date;
    }

    public static Date formatDate(Date date) {

        String dateString = DATEFORMATER().format(date);
        Date formatedDate = null;
        try {
            formatedDate = DATEFORMATER().parse(dateString);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage());
            e.printStackTrace();
        }

        return formatedDate;
    }

    public static Date addHour(Date date, int hour) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR, hour);

        return c.getTime();
    }

    public static Date addDate(Date date, int day) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);

        return c.getTime();
    }

    public static Date formatMonth(Date date) {

        String dateString = MONTHFORMATER().format(date);
        Date formatedDate = null;
        try {
            formatedDate = MONTHFORMATER().parse(dateString);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage());
            e.printStackTrace();
        }

        return formatedDate;
    }

    public static Date addMonth(Date date, int month) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, month);

        return c.getTime();
    }

    public static Date formatYear(Date date) {

        String dateString = YEARFORMATER().format(date);
        Date formatedDate = null;
        try {
            formatedDate = YEARFORMATER().parse(dateString);
        } catch (Exception e) {
            LOGGER.trace(e.getMessage());
            e.printStackTrace();
        }

        return formatedDate;
    }

    public static Date addYear(Date date, int year) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);

        return c.getTime();
    }

    public static String stratTimeToday(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(java.util.Calendar.HOUR_OF_DAY, 0);
        c.set(java.util.Calendar.MINUTE, 0);
        c.set(java.util.Calendar.SECOND, 0);

        return TIMEFORMATER1().format(c.getTime());
    }

    public static String endTimeToday(Date date) {

        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.set(java.util.Calendar.HOUR_OF_DAY, 23);
        c.set(java.util.Calendar.MINUTE, 59);
        c.set(java.util.Calendar.SECOND, 59);

        return TIMEFORMATER1().format(c.getTime());
    }

    public static boolean isToday(Date date) {

        Date today = formatDate(new Date());
        date = formatDate(date);

        return (today.getTime() == date.getTime());
    }

    public static boolean isCurMonth(Date date) {

        Date today = formatMonth(new Date());
        date = formatMonth(date);

        return (today.getTime() == date.getTime());
    }

    public static boolean isCurYear(Date date) {

        Date curYear = formatYear(new Date());
        date = formatMonth(date);

        return (curYear.getTime() == date.getTime());
    }

    public static Date getCalendarStart(Date date) {

        Date firstDay = formatMonth(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDay);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);

        return addDate(firstDay, 1 - dayOfWeek);
    }

    public static Date getCalendarEnd(Date date) {

        Date firstDay = formatMonth(date);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(firstDay);
        calendar.add(Calendar.MONTH, 1);
        calendar.add(Calendar.DATE, -1);

        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        calendar.add(Calendar.DATE, Calendar.DAY_OF_WEEK - dayOfWeek);

        return calendar.getTime();
    }

    public static Date getStarDateByYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    public static Date getEndDateByYear(int year) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year + 1);
        c.set(Calendar.MONTH, 0);
        c.set(Calendar.DAY_OF_MONTH, 1);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c.getTime();
    }

    /**
     * 求两个日期差 endDate - beginDate
     * 
     * @param beginDate
     *            开始日期
     * @param endDate
     *            结束日期
     * @return 两个日期相差天数
     */
    public static long getDateMargin(Date beginDate, Date endDate) {
        long margin = 0;
        margin = endDate.getTime() - beginDate.getTime();
        margin = margin / (1000 * 60 * 60 * 24);
        return margin;
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(TIMEFORMATER1().format(getStarDateByYear(2013)));
        
        Date d = stringToDatetime("2015-04-07 14:00:00");
        Date d1 = stringToDatetime("2015-04-07 14:01:00");
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        System.out.println(c.get(Calendar.YEAR));
        System.out.println(getSeconds(d,d1));
    }
    
    public static Date getDate(Date date, int field, int i) {

		if (date == null)
			return null;

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, i);

		return calendar.getTime();

	}
    
    public static double getSeconds(Date start, Date end)
    {
        double diffInSeconds = 0.001 * (end.getTime() - start.getTime());
        return diffInSeconds;
    }
    
}
