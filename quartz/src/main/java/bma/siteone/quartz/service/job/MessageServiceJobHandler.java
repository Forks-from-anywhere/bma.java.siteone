package bma.siteone.quartz.service.job;

import java.util.HashMap;

import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import bma.common.langutil.core.ValueUtil;
import bma.common.msgservice.MessageRequest;
import bma.common.msgservice.MessageService;
import bma.siteone.quartz.service.QuartzJobForm;
import bma.siteone.quartz.service.QuartzJobHandler;
import bma.siteone.quartz.service.QuartzService;
import bma.siteone.quartz.service.QuartzServiceUtil;

/**
 * JobData:<br/>
 * url : string<br/>
 * timeout : int<br/>
 * method : "get":"post"<br/>
 * retry : 重试次数<br/>
 * retryTime : 重试时间,毫秒<br/>
 * HTTPCLIENT_* : httpclient params<br/>
 * PARAM_* : request params<br/>
 * 
 * @author guanzhong
 * 
 */
public class MessageServiceJobHandler implements QuartzJobHandler {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(MessageServiceJobHandler.class);

	private MessageService service;

	public MessageServiceJobHandler() {
		super();
	}

	public MessageServiceJobHandler(MessageService service) {
		super();
		this.service = service;
	}

	public MessageService getService() {
		return service;
	}

	public void setService(MessageService service) {
		this.service = service;
	}

	@Override
	public void execute(QuartzService service, String type,
			JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getJobDetail().getJobDataMap();

		MessageRequest msgReq = new MessageRequest(new HashMap<String, Object>(
				dataMap));

		int retryCount = ValueUtil.intValue(dataMap.getString("retry"), 0);
		int retryTime = ValueUtil.intValue(dataMap.getString("retryTime"),
				1000);

		msgReq.remove(QuartzJobForm.KEY_TYPE);
		msgReq.remove("retry");
		msgReq.remove("retryTime");

		boolean success = false;
		try {
			success = this.service.sendMessage(msgReq);
		} catch (Exception e) {
			QuartzServiceUtil.debug(log, "job execute fail " + e, context);
		}

		if (success) {
			QuartzServiceUtil.debug(log, "job success", context);
		} else {
			QuartzServiceUtil.doRetry(log, context, retryCount, retryTime);
		}
	}
}
