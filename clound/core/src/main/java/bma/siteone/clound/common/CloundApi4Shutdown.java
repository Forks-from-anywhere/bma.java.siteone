package bma.siteone.clound.common;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.concurrent.ProcessTimerTask;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.clound.local.SimpleLocalCloundApi;

public class CloundApi4Shutdown extends SimpleLocalCloundApi<String, Boolean> {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(CloundApi4Shutdown.class);

	private String confirmWord;

	public String getConfirmWord() {
		return confirmWord;
	}

	public void setConfirmWord(String confirmWord) {
		this.confirmWord = confirmWord;
	}

	public CloundApi4Shutdown() {
		super();
		setTitle("shutdown server");
	}

	@Override
	public Class<String> getParamClass() {
		return String.class;
	}

	@Override
	public Boolean execute(String req) {
		if (ValueUtil.notEmpty(confirmWord)) {
			if (!StringUtil.equals(req, confirmWord)) {
				if (log.isWarnEnabled()) {
					log.warn("shutdown error confirmWord({})", req);
				}
				return false;
			}
		}
		if (log.isInfoEnabled()) {
			log.info("SHUTDOWN!!!!!!!!");
		}
		ProcessTimerTask task = TimerManager.delay(new Runnable() {

			@Override
			public void run() {
				if(AIExecutor.postShutdown()) {
					log.info("POST shutdown done");					
				} else {
					log.warn("POST shutdown fail");
				}
			}
		}, 1000);
		AIExecutor.getTimerManager().postTimerTask(task);
		return true;
	}

}
