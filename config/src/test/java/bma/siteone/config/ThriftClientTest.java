package bma.siteone.config;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;

import bma.siteone.config.thrift.TConfigAdminService;
import bma.siteone.config.thrift.TConfigInfo;

public class ThriftClientTest {
	
	
	  public static void main(String [] args) {

		    try {	
		      TTransport transport;
//	          transport = new TSocket("183.61.6.61", 9192);
//	          transport = new TSocket("183.61.6.60", 9092);
	          transport = new TSocket("127.0.0.1", 9098);
	          transport.open();
	          transport = new TFramedTransport(transport,2*1024*1024);
		      TProtocol protocol = new  TBinaryProtocol(transport);
		      TConfigAdminService.Client client = new TConfigAdminService.Client(protocol);

		      try {

		    	  String app = "mine";
		    	  String group = "bao";
		    	  String name = "test";
	  
		    	  TConfigInfo info = new TConfigInfo();
		    	  info.setGroup(group);
		    	  info.setName(name);
		    	  info.setValue("88");
		    	  System.out.println(client.setConfig(app, info));

		    	  System.out.println(client.refreshConfig(app,group));
		    	  
		    	  System.out.println(client.deleteConfig(app, group, name));
		    	  
		    	  System.out.println("done");
		    	  
			   } catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
			 }

		      transport.close();
		    } catch (TException x) {
		      x.printStackTrace();
		    } 
	 }
	  
	
}
