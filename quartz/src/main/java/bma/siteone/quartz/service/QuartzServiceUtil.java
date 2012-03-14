package bma.siteone.quartz.service;

import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.slf4j.Logger;

import bma.common.langutil.core.ValueUtil;

public class QuartzServiceUtil {

	public static final String KEY_SERVICE = "_service";

	public static void bind(Scheduler sch, QuartzServiceRealm s)
			throws SchedulerException {
		sch.getContext().put(KEY_SERVICE, s);
	}

	public static QuartzServiceRealm service(Scheduler sch)
			throws SchedulerException {
		return (QuartzServiceRealm) sch.getContext().get(KEY_SERVICE);
	}

	public static void debug(Logger log, String msg, JobExecutionContext context) {
		if (log.isDebugEnabled()) {
			log.debug("{}\n{}\n{}", new Object[] { msg, context.getJobDetail(),
					context.getTrigger() });
		}
	}

	public static void warn(Logger log, String msg, JobExecutionContext context) {
		log.warn("{}\n{}\n{}", new Object[] { msg, context.getJobDetail(),
				context.getTrigger() });
	}

	public static void error(Logger log, String msg, JobExecutionContext context) {
		log.error("{}\n{}\n{}", new Object[] { msg, context.getJobDetail(),
				context.getTrigger() });
	}

	public static Map<String, String> toProperties(JobDataMap dataMap,
			String prefix) {
		Map<String, String> r = new TreeMap<String, String>();
		String[] keys = dataMap.getKeys();
		if (keys != null) {
			for (String k : keys) {
				if (k.startsWith(prefix)) {
					String v = dataMap.getString(k);
					r.put(k.substring(prefix.length()), v);
				}
			}
		}
		return r;
	}

	public static boolean doRetry(Logger log, JobExecutionContext context,
			int retryCount, int retryTime) throws JobExecutionException {
		Trigger oldTrigger = context.getTrigger();
		JobDataMap dataMap = oldTrigger.getJobDataMap();
		int curr = ValueUtil.intValue(dataMap.getString("retry"), 0); // 当前执行的次数
		boolean canRetry = retryCount > curr;
		if (canRetry) {
			QuartzServiceUtil.debug(log, "job retry - " + (curr + 1), context);

			String baseName = ValueUtil.stringValue(oldTrigger.getJobDataMap()
					.getString("name"), "");
			if (ValueUtil.empty(baseName)) {
				baseName = oldTrigger.getKey().getName();
			}
			Trigger newTrigger = TriggerBuilder
					.newTrigger()
					.withIdentity(baseName + "__r" + (curr + 1),
							oldTrigger.getKey().getGroup())
					.forJob(oldTrigger.getJobKey())
					.startAt(new Date(System.currentTimeMillis() + retryTime))
					.build();
			newTrigger.getJobDataMap().put("name", baseName);
			newTrigger.getJobDataMap().put("retry", Integer.toString(curr + 1));
			try {
				context.getScheduler().scheduleJob(newTrigger);
				return true;
			} catch (SchedulerException e1) {
				QuartzServiceUtil.warn(log, "retry fail", context);
				throw new JobExecutionException("retry fail", e1);
			}
		} else {
			QuartzServiceUtil.debug(log, "job fail", context);
			return false;
		}
	}
}
