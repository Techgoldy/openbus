package com.produban.openbus.console.util;

import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

public class ScheduledJob
  implements Job
{
  private static Logger LOG = Logger.getLogger(ScheduledJob.class);
  
  public void execute(JobExecutionContext arg0)
    throws JobExecutionException
  {
    LOG.info("Job A is runing");
  }
}