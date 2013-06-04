package bma.siteone.nick.service;

import java.util.List;

import bma.siteone.nick.po.NickUser;

public interface NickService {

	/**
	 * 传入uid列表和过期时间 返回uid为key昵称为value的map
	 */
	public List<NickUser> getNicks(List<Long> uids , int overdueTime);
	
}
