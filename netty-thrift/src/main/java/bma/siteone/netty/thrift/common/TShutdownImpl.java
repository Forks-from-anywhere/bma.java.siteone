package bma.siteone.netty.thrift.common;

import org.apache.thrift.TException;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.concurrent.ProcessTimerTaskAbstract;
import bma.common.langutil.concurrent.TimerManager;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.netty.thrift.common.TShutdown.Iface;

public class TShutdownImpl implements Iface {

	private String confirmWord;
	private long delay = 2 * 1000;

	public long getDelay() {
		return delay;
	}

	public void setDelay(long delay) {
		this.delay = delay;
	}

	public String getConfirmWord() {
		return confirmWord;
	}

	public void setConfirmWord(String confirmWord) {
		this.confirmWord = confirmWord;
	}

	@Override
	public void shutdown(String confirmWord) throws TException {
		if (ValueUtil.notEmpty(this.confirmWord)
				&& !StringUtil.equals(confirmWord, this.confirmWord)) {
			throw new TException("invalid confirm word");
		}
		AIExecutor main = AIExecutor.getMainExecutor();
		if (main == null) {
			throw new TException("invalid AIMain");
		}
		TimerManager tm = AIExecutor.hasTimerManager();
		if (tm != null) {
			tm.postTimerTask(new ProcessTimerTaskAbstract(System
					.currentTimeMillis() + delay, true) {

				@Override
				public void run() {
					AIExecutor.postShutdown();
				}
			});
		} else {
			AIExecutor.postShutdown();
		}
	}
}
