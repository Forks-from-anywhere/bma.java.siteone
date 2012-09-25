package bma.siteone.cloud;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;

import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ValueUtil;
import bma.siteone.cloud.impl.BaseCloudEntry;

public class CloudUtil {

	public static String entry2string(CloudEntry e) {
		StringBuilder sb = new StringBuilder();
		sb.append(ValueUtil.stringValue(e.getNodeId(), "")).append("/");
		sb.append(ValueUtil.stringValue(e.getAppId(), "")).append("/");
		sb.append(ValueUtil.stringValue(e.getServiceId(), "")).append("/");
		sb.append(ValueUtil.stringValue(e.getApiId(), ""));
		return sb.toString();
	}

	public static CloudEntry string2entry(String s) {
		if (s == null)
			return null;
		String[] slist = s.split("/");
		BaseCloudEntry e = new BaseCloudEntry();
		e.setNodeId(slist.length > 0 ? slist[0] : null);
		e.setAppId(slist.length > 1 ? slist[1] : null);
		e.setServiceId(slist.length > 2 ? slist[2] : null);
		e.setApiId(slist.length > 3 ? slist[3] : null);
		return e;
	}

	public static String[] explodeContentType(String s) {
		if (s == null)
			return null;
		return s.split(";");
	}

	public static String implodeContentType(List<String> list) {
		if (list == null)
			return null;
		StringBuilder sb = new StringBuilder();
		Iterator<String> it = list.iterator();
		while (it.hasNext()) {
			sb.append(it.next());
			if (it.hasNext())
				sb.append(";");
		}
		return sb.toString();
	}

	public static String content2string(CloudRequest req) {
		byte[] c = req.getContent();
		if (c == null) {
			return null;
		}
		String encode = getEncodingFromContentType(req.getContentType());
		if (encode == null)
			return null;
		try {
			return new String(c, encode);
		} catch (UnsupportedEncodingException e) {
			throw ExceptionUtil.throwRuntime(e);
		}
	}

	public static String getEncodingFromContentType(String contentType) {
		String[] list = explodeContentType(contentType);
		if (list != null) {
			for (String s : list) {
				s = s.trim();
				if (s.equalsIgnoreCase("text")) {
					return "UTF-8";
				}
				if (s.toLowerCase().startsWith("text/")) {
					return s.substring(5).trim();
				}
			}
		}
		return null;
	}

	public static void setTextContent(CloudResponse rep, String str) {
		setTextContent(rep, str, null);
	}

	public static void setTextContent(CloudResponse rep, String str,
			String encoding) {
		if (encoding == null)
			encoding = "UTF-8";
		rep.setContentType("text/" + encoding);
		if (str != null) {
			try {
				rep.setContent(str.getBytes(encoding));
			} catch (UnsupportedEncodingException e) {
				throw ExceptionUtil.throwRuntime(e);
			}
		}
	}

	public static void setJsonContent(CloudResponse rep, String str) {
		String encoding = "UTF-8";
		rep.setContentType("text/" + encoding + ";json");
		if (str != null) {
			try {
				rep.setContent(str.getBytes(encoding));
			} catch (UnsupportedEncodingException e) {
				throw ExceptionUtil.throwRuntime(e);
			}
		}
	}

	public static void setRedirect(CloudResponse rep, BaseCloudEntry e) {
		String encoding = "UTF-8";
		rep.setType(CloudResponse.TYPE_REDIRECT);
		rep.setContentType("text/" + encoding + ";entry");
		String s = entry2string(e);
		if (s != null) {
			try {
				rep.setContent(s.getBytes(encoding));
			} catch (UnsupportedEncodingException err) {
				throw ExceptionUtil.throwRuntime(err);
			}
		}
	}
}
