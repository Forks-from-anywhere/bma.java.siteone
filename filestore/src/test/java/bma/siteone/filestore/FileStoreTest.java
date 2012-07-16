package bma.siteone.filestore;

import java.nio.ByteBuffer;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.siteone.filestore.service.FileStoreThrift;

public class FileStoreTest {

	FileSystemXmlApplicationContext context;

	@Before
	public void setUp() throws Exception {
		SpringTestcaseUtil.disableDebug();
		context = new SpringTestcaseUtil.ApplicationContextBuilder().resource(
				getClass(), "filestore.xml").build();
	}

	@After
	public void tearDown() throws Exception {
		if (context != null)
			context.close();
	}

	@Test
	public void testService_Boot() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);
		System.out.println(s);

		ObjectUtil.waitFor(this, 5000);
	}

	@Test
	public void testService_Base() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		String token = s.beginSession("test");
		String vcode = null;
		s.sendFile(token, "/d1/f1.txt",
				ByteBuffer.wrap("hello world".getBytes()), vcode);
		s.sendFile(token, "/f2.txt",
				ByteBuffer.wrap("hello world2".getBytes()), vcode);
		s.removeFile(token, "/f3.txt", vcode);
		s.sendFile(token, "/f4.txt", null, vcode);
		s.commitSession(token, vcode);
	}

	@Test
	public void testService_Path() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		String token = s.beginSession("test");
		String vcode = null;
		s.sendFile(token, "./../../f5.txt",
				ByteBuffer.wrap("hello world5".getBytes()), vcode);
		s.commitSession(token, vcode);
	}

	@Test
	public void testService_Vcode() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		String token = s.beginSession("test2");
		String vcode = FileStoreThrift.createVCode(token, "123456");
		s.removeFile(token, "/f3.txt", vcode);
		s.commitSession(token, vcode);
	}

	@Test
	public void testService_Timeout() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		s.beginSession("test");
		ObjectUtil.waitFor(this, 12 * 1000);
	}

	@Test
	public void testService_Chunked() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		String token = s.beginSession("test");
		String vcode = null;
		s.sendFile(token, "/f6.txt", ByteBuffer.wrap("first part".getBytes()),
				vcode);
		s.sendFile(token, "/f6.txt",
				ByteBuffer.wrap("- second part".getBytes()), vcode);
		s.commitSession(token, vcode);
	}

	@Test
	public void testService_Read() throws Exception {
		FileStoreThrift s = context.getBean("filestore", FileStoreThrift.class);

		String app = "test2";
		String vcode = null;

		String file = "/f6.txt";
		boolean bin = true;
		file = "/Thunder5.9.10.1144.exe";
		for (int i = 0;; i++) {
			int sz = 1000 * 1000;

			if (app.equals("test2")) {
				StringBuilder sb = new StringBuilder();
				sb.append("test2/Thunder5.9.10.1144.exe").append(i * sz)
						.append(sz);
				vcode = FileStoreThrift.createVCode(sb.toString(), "123456");
			}

			ByteBuffer bb = s.readFile(app, file, i * sz, sz, vcode);
			System.out.println(i + ":" + bb.remaining());
			if (bb.remaining() != sz)
				break;
			if (!bin) {
				System.out.println(new String(bb.array()));
			}
		}
	}
}
