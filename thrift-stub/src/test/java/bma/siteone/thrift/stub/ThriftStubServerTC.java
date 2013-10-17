package bma.siteone.thrift.stub;

import java.io.File;
import java.io.InputStream;

import junit.framework.TestCase;

import org.codehaus.jackson.JsonNode;

import bma.common.json.JsonUtil;
import bma.common.json.xdom.XdomJson;
import bma.common.langutil.core.ExceptionUtil;
import bma.common.langutil.core.ObjectUtil;
import bma.common.langutil.core.ResourceUtil;
import bma.common.langutil.io.IOUtil;
import bma.common.langutil.testcase.SpringTestcaseUtil;
import bma.common.langutil.xdom.Xdom;
import bma.common.langutil.xdom.XdomUtil;
import bma.common.netty.thrift.core.NettyThriftServer;
import bma.common.thrift.xdom.TProcessorXdom;
import bma.common.thrift.xdom.impl.DynamicThriftManagerHub;
import bma.common.thrift.xdom.impl.DynamicThriftManagerSimple;

/**
 * ThriftStub服务测试用例
 * 
 * @author 关中
 * 
 */
public class ThriftStubServerTC extends TestCase {

	/**
	 * 测试服务端启动
	 * 
	 * @throws Exception
	 */
	public void testSpringServer() throws Exception {
		File xmlFile = IOUtil
				.getUserDirFile("src/test/java/bma/siteone/thrift/stub/thrift-stub.xml");
		System.setProperty("spring_server_xml", xmlFile.getAbsolutePath());
		SpringTestcaseUtil.disableDebug();
		bma.common.langutil.ai.boot.AIServerBoot.main(new String[] {});
	}

	protected String read(String fileName) throws Exception {
		return IOUtil.readStreamToString((InputStream) ResourceUtil
				.getResourceFromClass(ThriftStubServerTC.class, fileName)
				.getContent(), "UTF-8");
	}

	public void testServer() throws Exception {

		String json = read("test.json");
		JsonNode node = JsonUtil.parse(json);
		Xdom desc = new XdomJson().bind(node);

		DynamicThriftManagerSimple m = new DynamicThriftManagerSimple();
		m.init(desc);

		DynamicThriftManagerHub hub = new DynamicThriftManagerHub();
		hub.addManager(m);

		TProcessorXdom processor = new TProcessorXdom() {

			@Override
			public Xdom handle(String name, Xdom param) {
				try {
					System.out.println("REQUEST:");
					System.out.println(XdomUtil.toString(param));
					System.out.println("RESPONSE:" + name);
					String json = read(name + ".dat");
					System.out.println(json);
					JsonNode node = JsonUtil.parse(json);
					return new XdomJson().bind(node);
				} catch (Exception e) {
					throw ExceptionUtil.throwRuntime(e);
				}
			}
		};
		processor.setDescManager(hub);

		NettyThriftServer server = new NettyThriftServer();
		server.setName("ThriftStub");
		server.setFrameSize("10m");
		server.setPort(9180);
		server.setProcessor(processor);

		server.start();
		ObjectUtil.waitFor(this, 30 * 60 * 1000 * 100);
		server.close();
	}
}
