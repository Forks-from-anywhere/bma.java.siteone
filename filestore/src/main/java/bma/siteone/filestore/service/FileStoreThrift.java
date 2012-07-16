package bma.siteone.filestore.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;

import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.Preconditions;
import bma.common.langutil.core.StringUtil;
import bma.common.langutil.core.VPath;
import bma.common.langutil.core.ValueUtil;
import bma.common.langutil.io.IOUtil;
import bma.siteone.filestore.thrift.TFileStore;

public class FileStoreThrift implements TFileStore.Iface {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(FileStoreThrift.class);

	private Map<String, FileStoreConfig> configs;
	private File temp;
	private int timeout = 10;
	private ScheduledExecutorService timer;
	private Map<String, FileStoreSession> sessions = new ConcurrentHashMap<String, FileStoreSession>();

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public File getTemp() {
		return temp;
	}

	public void setTemp(File temp) {
		this.temp = temp;
	}

	public void setTempFile(String tmp) {
		this.temp = new File(tmp);
	}

	public ScheduledExecutorService getTimer() {
		return timer;
	}

	public void setTimer(ScheduledExecutorService timer) {
		this.timer = timer;
	}

	public void setConfigs(List<FileStoreConfig> list) {
		if (configs == null) {
			configs = new HashMap<String, FileStoreConfig>(list.size());
		}
		for (FileStoreConfig fc : list) {
			configs.put(fc.getAppId(), fc);
		}
	}

	public void init() {
		Preconditions.checkNotNull(this.temp, "temp directory");
		Preconditions.checkNotNull(this.configs, "app configs");
		if (this.timer == null) {
			this.timer = Executors.newSingleThreadScheduledExecutor();
		}
		for (FileStoreConfig fc : this.configs.values()) {
			if (fc.getTemp() == null) {
				fc.setTemp(this.getTemp());
			}
		}
	}

	public void close() {
		if (this.timer != null) {
			try {
				this.timer.awaitTermination(1, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
			}
		}
		// clearup Session
		List<FileStoreSession> tmp = new ArrayList<FileStoreSession>(
				this.sessions.values());
		for (FileStoreSession s : tmp) {
			cleanSession(s);
		}
	}

	protected void cleanSession(FileStoreSession s) {
		if (log.isDebugEnabled()) {
			log.debug("clean session - {}", s);
		}
		this.sessions.remove(s.getSessionId());
		File file = getSessionRoot(s);
		if (file != null && file.exists() && file.isDirectory()) {
			if (log.isDebugEnabled()) {
				log.debug("clear session dir - {}", file);
			}
			try {
				IOUtil.deleteDirectory(file);
			} catch (IOException e) {

			}
		}
	}

	private class Cleaner implements Runnable {
		private FileStoreSession session;

		public Cleaner(FileStoreSession session) {
			super();
			this.session = session;
		}

		@Override
		public void run() {
			long t = (session.getLastRequestTime() + timeout * 1000)
					- System.currentTimeMillis();
			if (t <= 0) {
				// clear it
				if (sessions.containsKey(session.getSessionId())) {
					if (log.isDebugEnabled()) {
						log.debug("session timeout - "
								+ DateTimeUtil.formatPeriod(-t));
					}
					cleanSession(session);
				}
			} else {
				try {
					timer.schedule(this, t, TimeUnit.MILLISECONDS);
				} catch (Exception e) {
				}
			}
		}
	}

	@Override
	public String beginSession(String appId) throws TException {
		FileStoreConfig cfg = configs.get(appId);
		if (cfg == null) {
			throw new TApplicationException("app '" + appId + "' not exists");
		}
		final FileStoreSession s = createSession(appId);
		if (log.isDebugEnabled()) {
			log.debug("begin session {} >> {}", appId, s.getSessionId());
		}
		sessions.put(s.getSessionId(), s);
		this.timer.schedule(new Cleaner(s), timeout, TimeUnit.SECONDS);
		return s.getSessionId();
	}

	protected FileStoreSession createSession(String appId) {
		FileStoreSession s = new FileStoreSession();
		s.setAppId(appId);
		s.setSessionId(UUID.randomUUID().toString());
		s.setLastRequestTime(System.currentTimeMillis());
		return s;
	}

	protected FileStoreSession getSession(String s) {
		return sessions.get(s);
	}

	protected File getSessionRoot(FileStoreSession s) {
		FileStoreConfig fsc = configs.get(s.getAppId());
		File root = this.temp;
		if (fsc != null) {
			root = fsc.getTemp();
		}
		return new File(root, s.getSessionId());
	}

	protected File getPathFile(File root, String path) {
		VPath p = VPath.create(path);
		StringBuffer newPath = new StringBuffer(path.length());
		for (String n : p.split()) {
			if (n.equals(".") || n.equals(".."))
				continue;
			if (newPath.length() > 0)
				newPath.append('/');
			newPath.append(n);
		}
		return new File(root, newPath.toString());
	}

	public static String createVCode(String token, String key) {
		return StringUtil.md5((token + key).getBytes());
	}

	protected String vcode(FileStoreSession s) {
		FileStoreConfig fsc = configs.get(s.getAppId());
		String key = fsc == null ? null : fsc.getKey();
		if (key == null)
			return null;
		return createVCode(s.getSessionId(), key);
	}

	protected boolean check(FileStoreSession s, String vcode) {
		String m = vcode(s);
		if (m != null) {
			if (!StringUtil.equalsIgnoreCase(m, vcode)) {
				if (log.isWarnEnabled()) {
					log.warn("vcode invalid - {}/{}", m, vcode);
				}
				return false;
			}
		}
		return true;
	}

	@Override
	public void sendFile(String token, String path, ByteBuffer content,
			String vcode) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("sendFile({},{},{},{})", new Object[] { token, path,
					content == null ? 0 : content.remaining(), vcode });
		}
		FileStoreSession s = getSession(token);
		if (s == null) {
			throw new TApplicationException("session('" + token + "') invalid");
		}
		if (!check(s, vcode)) {
			throw new TApplicationException("vcode('" + vcode + "') invalid");
		}
		s.setLastRequestTime(System.currentTimeMillis());

		File file = getPathFile(getSessionRoot(s), path);
		try {
			if (log.isDebugEnabled()) {
				log.debug("{} file {}", file.exists() ? "append" : "create",
						file);
			}
			file.getParentFile().mkdirs();
			if (content != null) {
				byte[] buf = new byte[1024 * 4];
				OutputStream out = new FileOutputStream(file, file.exists());
				try {
					while (content.hasRemaining()) {
						int len = Math.min(content.remaining(), buf.length);
						content.get(buf, 0, len);
						out.write(buf, 0, len);
					}
					out.flush();
				} finally {
					IOUtil.close(out);
				}
			} else {
				if (!file.exists()) {
					IOUtil.touch(file);
				}
			}
		} catch (Exception e) {
			if (log.isWarnEnabled()) {
				log.warn("save file(" + file + ") fail", e);
			}
			throw new TApplicationException("save file(" + path + ") fail - "
					+ e.getMessage());
		}

		for (FileStoreRequest old : s.getRequests()) {
			if (!old.isDelete() && StringUtil.equals(old.getPath(), path)) {
				return;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("new file request[{}]", path);
		}
		FileStoreRequest req = new FileStoreRequest();
		req.setDelete(false);
		req.setPath(path);
		req.setTempFile(file);
		s.getRequests().add(req);
	}

	@Override
	public void removeFile(String token, String path, String vcode)
			throws TException {
		if (log.isDebugEnabled()) {
			log.debug("removeFile({},{},{})",
					new Object[] { token, path, vcode });
		}
		FileStoreSession s = getSession(token);
		if (s == null) {
			throw new TApplicationException("session('" + token + "') invalid");
		}
		if (!check(s, vcode)) {
			throw new TApplicationException("vcode('" + vcode + "') invalid");
		}
		s.setLastRequestTime(System.currentTimeMillis());

		for (FileStoreRequest old : s.getRequests()) {
			if (old.isDelete() && StringUtil.equals(old.getPath(), path)) {
				return;
			}
		}

		if (log.isDebugEnabled()) {
			log.debug("new delete request[{}]", path);
		}
		FileStoreRequest req = new FileStoreRequest();
		req.setDelete(true);
		req.setPath(path);
		s.getRequests().add(req);
	}

	@Override
	public void commitSession(String token, String vcode) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("commitSession({},{})", new Object[] { token, vcode });
		}
		FileStoreSession s = getSession(token);
		if (s == null) {
			throw new TApplicationException("session('" + token + "') invalid");
		}
		if (!check(s, vcode)) {
			throw new TApplicationException("vcode('" + vcode + "') invalid");
		}
		sessions.remove(s.getSessionId());

		StringBuffer err = new StringBuffer(256);
		try {
			FileStoreConfig fc = configs.get(s.getAppId());
			if (fc != null) {
				for (FileStoreRequest req : s.getRequests()) {
					File file = getPathFile(fc.getRoot(), req.getPath());
					try {
						if (req.isDelete()) {
							if (file.exists()) {
								if (file.isDirectory()) {
									if (log.isDebugEnabled()) {
										log.debug("delete dir - {}", file);
									}
									IOUtil.deleteDirectory(file);
								} else {
									if (log.isDebugEnabled()) {
										log.debug("delete file - {}", file);
									}
									file.delete();
								}
							} else {
								if (log.isDebugEnabled()) {
									log.debug("skip delete file - {}", file);
								}
							}
						} else {
							File tmp = req.getTempFile();
							if (log.isDebugEnabled()) {
								log.debug("copy file - {} >> {}", tmp, file);
							}
							IOUtil.copyFile(tmp, file);
						}
					} catch (Exception e) {
						if (log.isWarnEnabled()) {
							log.warn("handle file(" + file + ") fail", e);
						}
						if (err.length() > 0)
							err.append(",");
						err.append(req.getPath());
					}
				}
			}
		} finally {
			cleanSession(s);
		}

		if (err.length() > 0) {
			throw new TApplicationException(err + " fail");
		}
	}

	@Override
	public ByteBuffer readFile(String appId, String path, int pos,
			int readSize, String vcode) throws TException {
		if (log.isDebugEnabled()) {
			log.debug("readFile({}, {},{},{},{})", new Object[] { appId, path,
					pos, readSize, vcode });
		}

		FileStoreConfig cfg = configs.get(appId);
		if (cfg == null) {
			throw new TApplicationException("app '" + appId + "' not exists");
		}

		String key = cfg.getKey();
		if (ValueUtil.notEmpty(key)) {
			StringBuilder buf = new StringBuilder();
			buf.append(appId).append(path).append(pos).append(readSize);
			String m = createVCode(buf.toString(), key);

			if (!StringUtil.equalsIgnoreCase(m, vcode)) {
				if (log.isWarnEnabled()) {
					log.warn("vcode invalid - {}/{}", m, vcode);
				}
				throw new TApplicationException("vcode('" + vcode
						+ "') invalid");
			}
		}

		File file = getPathFile(cfg.getRoot(), path);
		try {
			if (file.exists()) {
				if (file.isDirectory()) {
					if (log.isDebugEnabled()) {
						log.debug("skip read dir - {}", file);
					}
					return ByteBuffer.wrap(new byte[0]);
				} else {
					long len = file.length();
					int sz = Math.min(pos + readSize, (int) len) - pos;
					if (log.isDebugEnabled()) {
						log.debug(
								"reading file ({},{}) : {}/{} ",
								new Object[] { file, len, pos, sz > 0 ? sz : 0 });
					}
					if (sz <= 0)
						return ByteBuffer.wrap(new byte[0]);
					InputStream in = null;
					try {
						in = new FileInputStream(file);
						if (pos > 0) {
							in.skip(pos);
						}
						byte[] buffer = new byte[sz];
						in.read(buffer);
						return ByteBuffer.wrap(buffer);
					} finally {
						IOUtil.close(in);
					}
				}
			} else {
				if (log.isDebugEnabled()) {
					log.debug("file - {} not exists", file);
				}
				return ByteBuffer.wrap(new byte[0]);
			}

		} catch (Exception e) {
			String msg = "read file {} fail - " + e.getMessage();
			if (log.isWarnEnabled()) {
				log.warn(msg, e);
			}
			throw new TApplicationException(msg);
		}
	}
}
