package bma.siteone.netty.thrift.gate.impl;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.langutil.ai.stack.AIStackOne4Timer;
import bma.common.langutil.concurrent.TimerManager;
import bma.siteone.netty.thrift.gate.MessageContext;

public class NTGAgentTimeout implements NTGAgentProcess {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(NTGAgentTimeout.class);

	private NTGAgentProcess process;
	private TimerManager timer;
	private long timeout;

	public NTGAgentTimeout(NTGAgentProcess process, long to) {
		super();
		this.process = process;
		this.timeout = to;
	}

	public NTGAgentProcess getProcess() {
		return process;
	}

	public void setProcess(NTGAgentProcess process) {
		this.process = process;
	}

	public TimerManager getTimer() {
		return timer;
	}

	public void setTimer(TimerManager timer) {
		this.timer = timer;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	@Override
	public boolean process(MessageContext ctx) {
		AIStackAgentROOT root = new AIStackAgentROOT(log, ctx);
		return process(root, ctx);
	}

	@Override
	public boolean process(AIStack<Boolean> stack, MessageContext ctx) {
		Number tmp = ctx.getProperty("timeout", Number.class);
		long to = tmp == null ? this.timeout : tmp.longValue();
		AIStack<Boolean> callStack = stack;
		if (to > 0) {
			AIStackOne4Timer<Boolean> mystack = new AIStackOne4Timer<Boolean>(
					stack);
			callStack = mystack;
			TimerManager tm = timer;
			if (tm == null) {
				tm = AIExecutor.getTimerManager();
			}
			tm.postTimerTask(mystack.createTimeout("agent process timeout", to));
		}
		try {
			return process.process(callStack, ctx);
		} catch (Exception err) {
			return callStack.failure(err);
		}
	}

}
