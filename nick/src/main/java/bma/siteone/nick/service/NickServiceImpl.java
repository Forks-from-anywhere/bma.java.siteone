package bma.siteone.nick.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.service.webdb.impl.WebdbServiceImpl;
import com.duowan.service.webdb.po.WebdbUserInfo;

import bma.siteone.nick.dao.NickUserSiteOneDAOService;
import bma.siteone.nick.po.NickUser;

public class NickServiceImpl implements NickService {
	
	private static final Logger logger = LoggerFactory.getLogger(NickServiceImpl.class);
	
	private NickUserSiteOneDAOService nickUserSiteOneDAOService;
	
	private WebdbServiceImpl webdbService;

	@Override
	public List<NickUser> getNicks(List<Long> uids, int overdueTime) {
		if(uids == null || uids.isEmpty()){
			logger.info("uids is null");
			return null;
		}
		logger.debug("[NickServiceImpl] uids=>[" + uids + " ], overdueTime = " + overdueTime);
		List<NickUser> nickUsers = nickUserSiteOneDAOService.getNickUserList(uids);
		nickUserSiteOneDAOService.updateNickUserOverdueTime(uids,overdueTime);
		List<NickUser> others =  getOthers(nickUsers, uids,overdueTime);
		nickUserSiteOneDAOService.updateNickUserList(others);
		return nickUserSiteOneDAOService.getNickUserList(uids);
	}
	
	private Map<Long,NickUser> getNickUserMap(List<NickUser> nickUsers){
		Map<Long,NickUser> userMap = new HashMap<Long,NickUser>();
		for(NickUser c : nickUsers){
			userMap.put(c.getUid(), c);
		}
		return userMap;
	}

	private List<NickUser> getOthers(List<NickUser> nickUsers, List<Long> uids , int overdueTime) {
		List<Long> uidOthers = new ArrayList<Long>();
		Map<Long,NickUser> userMap = getNickUserMap(nickUsers);
		for(Long uid : uids){
			if(!userMap.containsKey(uid) || userMap.get(uid).getNick() == null){
				uidOthers.add(uid);
			}
		}
		Map<Long, WebdbUserInfo> uidMap = webdbService.getUserInfoMap(uidOthers);
		List<NickUser> listTmp = new ArrayList<NickUser>();
		for(Long uid : uidMap.keySet()){
			NickUser c = new NickUser();
			c.setUid(uid);
			c.setNick(uidMap.get(uid).getNick());
			c.setOverdue_time(overdueTime);
			listTmp.add(c);
		}
		return listTmp;
	}

	public void setWebdbService(WebdbServiceImpl webdbService) {
		this.webdbService = webdbService;
	}

	public WebdbServiceImpl getWebdbService() {
		return webdbService;
	}

	public void setNickUserSiteOneDAOService(NickUserSiteOneDAOService nickUserSiteOneDAOService) {
		this.nickUserSiteOneDAOService = nickUserSiteOneDAOService;
	}

	public NickUserSiteOneDAOService getNickUserSiteOneDAOService() {
		return nickUserSiteOneDAOService;
	}
}
