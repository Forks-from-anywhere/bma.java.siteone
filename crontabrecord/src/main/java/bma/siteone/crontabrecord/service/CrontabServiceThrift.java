package bma.siteone.crontabrecord.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import bma.common.langutil.log.filelog.AsyncFileLog;
import bma.common.langutil.log.filelog.PerformanceFileLogTask;
import bma.siteone.crontabrecord.thrift.*;

public class CrontabServiceThrift implements TCrontabService.Iface {	

	private static final Logger logger = LoggerFactory.getLogger(CrontabServiceThrift.class);

	private AsyncFileLog fileLog;
	private int corePoolSize = 1;
	private int maximumPoolSize = 10;
	private long keepAliveTime = 10;
	
	private Map<String,CrontabInterface> serviceMap =  new HashMap<String, CrontabInterface>();

	public int getCorePoolSize() {
		return corePoolSize;
	}

	public void setCorePoolSize(int corePoolSize) {
		this.corePoolSize = corePoolSize;
	}

	public int getMaximumPoolSize() {
		return maximumPoolSize;
	}

	public void setMaximumPoolSize(int maximumPoolSize) {
		this.maximumPoolSize = maximumPoolSize;
	}

	public long getKeepAliveTime() {
		return keepAliveTime;
	}

	public void setKeepAliveTime(long keepAliveTime) {
		this.keepAliveTime = keepAliveTime;
	}

	// 线程池
	private ExecutorService executorService;

	public void initExecutorService() {
		if (executorService == null) {
			executorService = new ThreadPoolExecutor(getCorePoolSize(), getMaximumPoolSize(), getKeepAliveTime(),
					TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		}

	}

	/**
	 * 服务关闭
	 * 
	 * @throws InterruptedException
	 */
	public void close() throws InterruptedException {
		if (logger.isInfoEnabled()) {
			logger.info("------ close ------");
		}

		// shutdown线程池
		executorService.shutdown();
	}

	public AsyncFileLog getFileLog() {
		return fileLog;
	}

	public void setFileLog(AsyncFileLog fileLog) {
		this.fileLog = fileLog;
	}

	
	@Override
	public void callRankService(final String serviceName) throws TException {
		initExecutorService();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				PerformanceFileLogTask task = new PerformanceFileLogTask("crontab");
				try {
					CrontabInterface service = getServiceMap().get(serviceName);
					if(service == null){
						logger.warn("can not find the service instance.service name={}",serviceName);
						return;
					}
					task.setLogId(service.getService());
					task.begin();
					service.execute();
				} finally {
					task.addNow().addMessage("op", "callRankService.serviceName=" + serviceName);
					fileLog.write(task.end());
				}
			}
		});
	}

	public void setServiceMap(Map<String,CrontabInterface> serviceMap) {
		this.serviceMap = serviceMap;
	}

	public Map<String,CrontabInterface> getServiceMap() {
		return serviceMap;
	}

	@Override
	public void deleteRankService(final String serviceName) throws TException {
		initExecutorService();
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				PerformanceFileLogTask task = new PerformanceFileLogTask("crontab");
				task.begin();
				try {
					CrontabInterface service = getServiceMap().get(serviceName);
					if(service == null){
						logger.warn("can not find the service instance.service name={}",serviceName);
						return;
					}
					service.stopRankService();
				} finally {
					task.addNow().addMessage("op", "deleteRankService.serviceName=" + serviceName);
					fileLog.write(task.end());
				}
			}
		});
	}
	

}
