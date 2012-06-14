package bma.siteone.netty.thrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

import bma.common.langutil.ai.stack.AISRun;
import bma.common.langutil.ai.stack.AIStack;
import bma.common.thrift.TAIBaseFace;

public class HelloHandler extends TAIBaseFace implements Hello.Iface,
		Hello4AI.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(HelloHandler.class);

	@Override
	public void say(String word) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("hello {}", word);
		}
	}

	@Override
	public boolean say(AIStack<Boolean> stack, final String word) {
		return executeAt(stack, new AISRun<Boolean>() {
			@Override
			public void execute() {
				if (log.isDebugEnabled()) {
					log.debug("ai-hello {}", word);
				}
				success(true);
			}
		});
	}

	@Override
	public String name(String title) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("name: {}", title);
		}
		return "hi," + title + " guan";
	}

	@Override
	public boolean name(AIStack<String> stack, final String title) {
		return executeAt(stack, new AISRun<String>() {
			@Override
			public void execute() {
				if (log.isDebugEnabled()) {
					log.debug("ai-name: {}", title);
				}
				String s = "hi," + title + " guan";
				success(s);
			}
		});
	}

	@Override
	public void error(String msg) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("error: {}", msg);
		}
		throw new TApplicationException("error: " + msg);
	}

	@Override
	public boolean error(AIStack<Boolean> stack, final String msg) {
		return executeAt(stack, new AISRun<Boolean>() {
			@Override
			public void execute() {
				if (log.isDebugEnabled()) {
					log.debug("error: {}", msg);
				}
				failure(new TApplicationException("error: " + msg));
			}
		});
	}

}
