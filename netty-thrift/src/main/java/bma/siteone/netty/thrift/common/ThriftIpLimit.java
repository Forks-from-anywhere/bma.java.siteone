package bma.siteone.netty.thrift.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import bma.common.langutil.ai.executor.AIExecutor;
import bma.common.langutil.concurrent.ScheduledExecutorTM;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.FileMonitor;
import bma.common.langutil.io.FileMonitor.Event;
import bma.common.langutil.io.FileMonitorListener;
import bma.common.langutil.io.IOUtil;
import bma.common.langutil.io.InetNetwork;
import bma.common.netty.handler.ChannelHandlerIpFilter;

public class ThriftIpLimit extends ChannelHandlerIpFilter {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ThriftIpLimit.class);

	protected String file;
	protected long fileCheckTime = 5 * 1000;

	// runtime
	protected FileMonitor monitor;
	protected List<InetNetwork> orgWhiteList;
	protected List<InetNetwork> orgBlackList;

	public FileMonitor getMonitor() {
		return monitor;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public void init() {
		if (orgWhiteList == null) {
			orgWhiteList = queryWhiteList();
		}
		if (orgBlackList == null) {
			orgBlackList = queryBlackList();
		}
		if (this.monitor == null) {
			if (this.file != null) {
				loadConfigFile(this.file);

				this.monitor = new FileMonitor();
				this.monitor.setExecutor(new ScheduledExecutorTM(AIExecutor
						.getTimerManager()));
				this.monitor.setTime(fileCheckTime);
				this.monitor.setFile(file);
				this.monitor.addListener(new FileMonitorListener() {

					@Override
					public void handleEvent(String fileName, Event event) {
						loadConfigFile(fileName);
					}
				});
				this.monitor.start();
			}
		}
	}

	protected void loadConfigFile(String fileName) {
		if (log.isInfoEnabled()) {
			log.info("load IpLimit config");
		}
		File f = new File(fileName);
		if (!f.exists()) {
			replaceWhiteList(orgWhiteList);
			replaceBlackList(orgBlackList);
			return;
		}

		List<InetNetwork> wlist = new ArrayList<InetNetwork>();
		List<InetNetwork> blist = new ArrayList<InetNetwork>();
		Properties prop = new Properties();
		InputStream in = null;
		try {
			in = new FileInputStream(f);
			prop.load(in);

			int c;

			wlist.addAll(orgWhiteList);
			c = ValueUtil.intValue(prop.getProperty("whiteList.count"), 0);
			for (int i = 0; i < c; i++) {
				String s = prop.getProperty("whiteList." + i);
				List<InetNetwork> tmp = create(s);
				if (tmp != null)
					wlist.addAll(tmp);
			}

			blist.addAll(orgBlackList);
			c = ValueUtil.intValue(prop.getProperty("blackList.count"), 0);
			for (int i = 0; i < c; i++) {
				String s = prop.getProperty("blackList." + i);
				List<InetNetwork> tmp = create(s);
				if (tmp != null)
					blist.addAll(tmp);
			}

			replaceWhiteList(wlist);
			replaceBlackList(blist);

		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("load IpLimit config(" + fileName + ") fail", e);
			}
		} finally {
			IOUtil.close(in);
		}
	}

	public void close() {
		if (this.monitor != null) {
			this.monitor.stop();
			this.monitor = null;
		}
	}
}
