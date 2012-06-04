package bma.siteone.netty.thrift;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

public class HelloHandler implements Hello.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(HelloHandler.class);

	@Override
	public void say(String word) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("hello {}", word);
		}
	}

	@Override
	public String name(String title) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("name: {}", title);
		}
		return "hi," + title + " guan";
	}

	@Override
	public void error(String msg) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("error: {}", msg);
		}
		throw new TApplicationException("error: " + msg);
	}

}
