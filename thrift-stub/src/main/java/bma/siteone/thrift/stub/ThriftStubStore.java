package bma.siteone.thrift.stub;

import java.util.List;

public interface ThriftStubStore {

	public long lastModified();
	
	public List<ThriftStubDesc> listAllDesc();
	
	public List<ThriftStubInfo> listAllInfo();	
	
}
