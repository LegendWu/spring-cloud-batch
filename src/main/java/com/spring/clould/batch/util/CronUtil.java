package com.spring.clould.batch.util;

import java.util.Date;

import com.spring.clould.batch.entity.TaskSchedule;

public class CronUtil {
	/** 
     *  
     *方法摘要：构建Cron表达式 
     *@param  TaskSchedule 
     *@return String 
     */  
    public static String createCronExpression(TaskSchedule TaskSchedule){  
        StringBuffer cronExp = new StringBuffer("");  
        
        if(null == TaskSchedule.getJobType()) {
        	System.out.println("执行周期未配置" );//执行周期未配置
        }
        
		if (null != TaskSchedule.getSecond()
				&& null != TaskSchedule.getMinute()
				&& null != TaskSchedule.getHour()) {  
            //秒  
            cronExp.append(TaskSchedule.getSecond()).append(" ");  
            //分  
            cronExp.append(TaskSchedule.getMinute()).append(" ");  
            //小时  
            cronExp.append(TaskSchedule.getHour()).append(" ");  
              
            //每天  
            if(TaskSchedule.getJobType().intValue() == 1){  
            	cronExp.append("* ");//日
            	cronExp.append("* ");//月
            	cronExp.append("?");//周
            }
              
            //按每周  
            else if(TaskSchedule.getJobType().intValue() == 3){  
                //一个月中第几天  
                cronExp.append("? ");  
                //月份  
                cronExp.append("* ");  
                //周  
                Integer[] weeks = TaskSchedule.getDayOfWeeks();  
                for(int i = 0; i < weeks.length; i++){  
                    if(i == 0){  
                        cronExp.append(weeks[i]);  
                    } else{  
                        cronExp.append(",").append(weeks[i]);  
                    }  
                }  
                  
            }  
              
            //按每月  
            else if(TaskSchedule.getJobType().intValue() == 2){  
            	//一个月中的哪几天  
            	Integer[] days = TaskSchedule.getDayOfMonths();  
            	for(int i = 0; i < days.length; i++){  
            		if(i == 0){  
            			cronExp.append(days[i]);  
            		} else{  
            			cronExp.append(",").append(days[i]);  
            		}  
            	}  
                //月份  
                cronExp.append(" * ");  
                //周  
                cronExp.append("?");  
            }  
              
        }  
		else {
			System.out.println("时或分或秒参数未配置" );//时或分或秒参数未配置
		}
        return cronExp.toString();  
    }  
      
    /** 
     *  
     *方法摘要：生成计划的详细描述 
     *@param  TaskSchedule 
     *@return String 
     */  
    public static String createDescription(TaskSchedule TaskSchedule){  
        StringBuffer description = new StringBuffer("");  
        //计划执行开始时间  
//      Date startTime = TaskSchedule.getScheduleStartTime();  
          
        if (null != TaskSchedule.getSecond()
				&& null != TaskSchedule.getMinute()
				&& null != TaskSchedule.getHour()) { 
            //按每天  
            if(TaskSchedule.getJobType().intValue() == 1){  
            	description.append("每天");  
            	description.append(TaskSchedule.getHour()).append("时");  
            	description.append(TaskSchedule.getMinute()).append("分");  
            	description.append(TaskSchedule.getSecond()).append("秒");  
            	description.append("执行");  
            }  
              
            //按每周  
            else if(TaskSchedule.getJobType().intValue() == 3){  
                if(TaskSchedule.getDayOfWeeks() != null && TaskSchedule.getDayOfWeeks().length > 0) {  
                	String days = "";
                	for(int i : TaskSchedule.getDayOfWeeks()) {
                		days += "周" + i;
                	}
                    description.append("每周的").append(days).append(" ");  
                }  
                if (null != TaskSchedule.getSecond()
        				&& null != TaskSchedule.getMinute()
        				&& null != TaskSchedule.getHour()) {   
                    description.append(",");   
                    description.append(TaskSchedule.getHour()).append("时");  
                	description.append(TaskSchedule.getMinute()).append("分");  
                	description.append(TaskSchedule.getSecond()).append("秒"); 
                }  
                description.append("执行");  
            }  
              
            //按每月  
            else if(TaskSchedule.getJobType().intValue() == 2){  
                //选择月份  
            	if(TaskSchedule.getDayOfMonths() != null && TaskSchedule.getDayOfMonths().length > 0) {  
                	String days = "";
                	for(int i : TaskSchedule.getDayOfMonths()) {
                		days += i + "号";
                	}
                    description.append("每月的").append(days).append(" ");  
                }    
            	description.append(TaskSchedule.getHour()).append("时");  
            	description.append(TaskSchedule.getMinute()).append("分");  
            	description.append(TaskSchedule.getSecond()).append("秒"); 
                description.append("执行");  
            }  
              
        }  
        return description.toString();  
    }
    
    /**
     * 根据当前时间加指定分钟
     * @param addMillis
     * @return
     */
    @SuppressWarnings("deprecation")
	public static String createCronByCurrentTimeAddMillis(int addMillis) {
    	TaskSchedule taskSchedule = new TaskSchedule();
    	Date nextRunTime = DateUtil.currentAddMillis(addMillis);
    	taskSchedule.setJobType(1);//按每天
    	Integer hour = nextRunTime.getHours(); //时
    	Integer minute = nextRunTime.getMinutes(); //分
    	Integer second = nextRunTime.getSeconds(); //秒
    	taskSchedule.setHour(hour);
    	taskSchedule.setMinute(minute);
    	taskSchedule.setSecond(second);
    	String cropExp = createCronExpression(taskSchedule);
    	return cropExp;
    }
    
    //参考例子
//    public static void main(String[] args) {
//    	//执行时间：每天的12时12分12秒 start
//    	TaskSchedule TaskSchedule = new TaskSchedule();
//    	TaskSchedule.setJobType(1);//按每天
//    	Integer hour = 12; //时
//    	Integer minute = 12; //分
//    	Integer second = 12; //秒
//    	TaskSchedule.setHour(hour);
//    	TaskSchedule.setMinute(minute);
//    	TaskSchedule.setSecond(second);
//    	String cropExp = createCronExpression(TaskSchedule);
//    	System.out.println(cropExp + ":" + createDescription(TaskSchedule));
//    	//执行时间：每天的12时12分12秒 end
//    	
//    	TaskSchedule.setJobType(3);//每周的哪几天执行
//    	Integer[] dayOfWeeks = new Integer[3];
//    	dayOfWeeks[0] = 1;
//    	dayOfWeeks[1] = 2;
//    	dayOfWeeks[2] = 3;
//    	TaskSchedule.setDayOfWeeks(dayOfWeeks);
//    	cropExp = createCronExpression(TaskSchedule);
//    	System.out.println(cropExp + ":" + createDescription(TaskSchedule));
//    	
//    	TaskSchedule.setJobType(2);//每月的哪几天执行
//    	Integer[] dayOfMonths = new Integer[3];
//    	dayOfMonths[0] = 1;
//    	dayOfMonths[1] = 21;
//    	dayOfMonths[2] = 13;
//    	TaskSchedule.setDayOfMonths(dayOfMonths);
//    	cropExp = createCronExpression(TaskSchedule);
//    	System.out.println(cropExp + ":" + createDescription(TaskSchedule));
//	}
}
