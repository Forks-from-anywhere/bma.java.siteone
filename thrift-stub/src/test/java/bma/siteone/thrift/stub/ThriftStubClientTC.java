package bma.siteone.thrift.stub;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import bma.common.langutil.core.ToStringUtil;
import bma.common.thrift.sample.TMedal;
import bma.common.thrift.sample.Test;

public class ThriftStubClientTC extends TestCase {

	public void testCall() throws Exception {

		TTransport transport;
		transport = new TSocket("127.0.0.1", 9180);
		transport.open();
		transport = new TFramedTransport(transport, 20 * 1024 * 1024);
		TProtocol protocol = new TBinaryProtocol(transport);
		Test.Client client = new Test.Client(protocol);

		int TC = 1;

		if (TC == 1) {
			Set<String> r = client.medalNames();
			for (String n : r) {
				System.out.print(n);
				System.out.print(",");
			}
		}
		if (TC == 2) {
			List<String> ids = new ArrayList<String>();
			ids.add("123");
			ids.add("234");
			Map<String, List<TMedal>> r = client.getMedal(ids);
			for (String n : r.keySet()) {
				List<TMedal> m = r.get(n);
				System.out.print(n + " = ");
				for (TMedal tMedal : m) {
					System.out.print(tMedal.toString());
					System.out.print(",");
				}
				System.out.println();
			}
		}
		if (TC == 3) {
			TMedal m = client.medal("123");
			System.out.println(m.toString());
		}
		if (TC == 4) {
			client.sleep(1000);
		}
		System.out.println("END");
		transport.close();
	}

}
