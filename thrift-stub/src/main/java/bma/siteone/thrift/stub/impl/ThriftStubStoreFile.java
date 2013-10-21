package bma.siteone.thrift.stub.impl;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.io.IOUtil;
import bma.siteone.thrift.stub.ThriftStubDesc;
import bma.siteone.thrift.stub.ThriftStubInfo;
import bma.siteone.thrift.stub.ThriftStubStore;

public class ThriftStubStoreFile implements ThriftStubStore {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ThriftStubStoreFile.class);

	private File datadir;
	private long refreshTime = 5000;

	private long lastTime = 0;
	private List<File> files = new ArrayList<File>();
	private ScheduledExecutorService executor = Executors
			.newSingleThreadScheduledExecutor();

	public File getDatadir() {
		return datadir;
	}

	public void setDatadir(File datadir) {
		this.datadir = datadir;
	}

	public void setDir(String dir) {
		this.datadir = new File(dir);
	}

	public long getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(long refreshTime) {
		this.refreshTime = refreshTime;
	}

	public void start() {
		if (this.datadir == null) {
			this.datadir = IOUtil.getUserDirFile("data");
		}
		if (!this.datadir.exists()) {
			throw new IllegalArgumentException("invalid dir '"
					+ this.datadir.getAbsolutePath() + "'");
		}
		if (refreshTime > 0) {
			executor.scheduleAtFixedRate(new Runnable() {

				@Override
				public void run() {
					refresh();
				}
			}, 0, refreshTime, TimeUnit.MILLISECONDS);
		}
	}

	protected void refresh() {
		File[] fs = this.datadir.listFiles(new FileFilter() {

			@Override
			public boolean accept(File file) {
				if (file.isDirectory())
					return false;
				String n = file.getName();
				return n.endsWith(".json") || n.endsWith(".dat")
						|| n.endsWith(".js");
			}
		});
		boolean mod = false;
		if (fs.length != files.size()) {
			lastTime = System.currentTimeMillis();
			mod = true;
		} else {
			for (File f : fs) {
				if (f.lastModified() > lastTime) {
					mod = true;
					lastTime = f.lastModified();
				}
			}
		}

		if (mod) {
			if (log.isInfoEnabled()) {
				log.info("refresh files, count = {}", fs.length);
			}
			synchronized (this) {
				files.clear();
				files.addAll(Arrays.asList(fs));
			}
		}
	}

	@Override
	public long lastModified() {
		if (refreshTime == 0) {
			refresh();
		}
		return lastTime;
	}

	@Override
	public List<ThriftStubDesc> listAllDesc() {
		List<File> fs = new ArrayList<File>();
		synchronized (this) {
			fs.addAll(files);
		}

		try {
			List<ThriftStubDesc> r = new ArrayList<ThriftStubDesc>();
			for (File file : fs) {
				String name = file.getName();
				if (name.endsWith(".json")) {
					String json = IOUtil.readFileToString(file, "UTF-8");
					ThriftStubDesc o = new ThriftStubDesc();
					o.setContent(json);
					o.setName(name);
					r.add(o);
				}
			}
			return r;
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	@Override
	public List<ThriftStubInfo> listAllInfo() {
		List<File> fs = new ArrayList<File>();
		synchronized (this) {
			fs.addAll(files);
		}
		try {
			List<ThriftStubInfo> r = new ArrayList<ThriftStubInfo>();
			for (File file : fs) {
				String name = file.getName();
				if (name.endsWith(".dat") || name.endsWith(".js")) {
					String json = IOUtil.readFileToString(file, "UTF-8");
					ThriftStubInfo o = new ThriftStubInfo();
					o.setContent(json);
					o.setName(name);
					if (name.endsWith(".dat")) {
						o.setMethod(name.substring(0, name.length() - 4));
						o.setType("data");
					} else if (name.endsWith(".js")) {
						o.setMethod(name.substring(0, name.length() - 3));
						o.setType("js");
					}
					r.add(o);
				}
			}
			return r;
		} catch (Exception e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

}
