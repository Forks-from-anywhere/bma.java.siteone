
namespace java bma.siteone.crontabrecord.thrift
namespace php bma.siteone.crontabrecord.po

service TCrontabService {
	
	/**
	*根据服务名称调用排行榜服务
	*/
	oneway void callRankService(1:string serviceName),
	
	/**
	*根据服务名称删除排行榜服务
	*/
	oneway void deleteRankService(1:string serviceName),
}
