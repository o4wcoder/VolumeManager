package com.fourthwardcoder.android.volumemanager;

import java.util.Calendar;
import java.util.Date;


public class ControlDate {

	private String periodOfDay;
	private Date date;
	private int hour;
	private int min;

	public ControlDate(Date date) {
		this.date = date;
		this.periodOfDay = "AM";
		
		//Create a Calendar to get the time
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		this.hour = calendar.get(Calendar.HOUR);
		this.min = calendar.get(Calendar.MINUTE);
	}

	public int getHour() {
		return hour;
	}

	public void setHour(int hour) {
		this.hour = hour;
	}

	public int getMin() {
		return min;
	}

	public void setMin(int min) {
		this.min = min;
	}

	public String getPeriodOfDay() {
		return periodOfDay;
	}

	public void setPeriodOfDay(String periodOfDay) {
		this.periodOfDay = periodOfDay;
	}



	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}



}
