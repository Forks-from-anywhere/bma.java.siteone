package bma.siteone.thrift.stub;

import java.io.File;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;
import bma.common.langutil.core.DateTimeUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.io.IOUtil;
import bma.siteone.thrift.stub.impl.ThriftStubStoreFile;

public class ThriftStubStoreFileTC extends TestCase {

	final org.slf4j.Logger log = org.slf4j.LoggerFactory
			.getLogger(ThriftStubStoreFileTC.class);

	public void testBase() throws Exception {

		File dir = IOUtil
				.getUserDirFile("src/test/java/bma/siteone/thrift/stub");
		ThriftStubStoreFile store = new ThriftStubStoreFile();
		store.setDatadir(dir);
		store.setRefreshTime(0);

		long tm = store.lastModified();
		log.info("last modify " + DateTimeUtil.formatDateTime(new Date(tm)));
		List<?> list;
		log.info("DESC:");
		list = store.listAllDesc();
		for (Object o : list) {
			log.info("{}", o);
		}

		log.info("INFO:");
		list = store.listAllInfo();
		for (Object o : list) {
			log.info("{}", o);
		}
	}

	public void testRefresh() throws Exception {

		File dir = IOUtil.getUserDirFile("docs");
		ThriftStubStoreFile store = new ThriftStubStoreFile();
		store.setDatadir(dir);
		store.start();

		ObjectUtil.waitFor(this, 60 * 1000);
	}
}
