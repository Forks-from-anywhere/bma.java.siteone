package bma.siteone.nick.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.duowan.service.webdb.impl.WebdbServiceImpl;
import com.duowan.service.webdb.po.WebdbUserInfo;

import bma.siteone.nick.dao.NickUserDAOService;
import bma.siteone.nick.po.NickUser;

public class NickServiceImpl implements NickService {
	
	private static final Logger logger = LoggerFactory.getLogger(NickServiceImpl.class);
	
	private NickUserDAOService nickUserDAOService;
	
	private WebdbServiceImpl webdbService;

	@Override
	public List<NickUser> getNicks(List<Long> uids, int overdueTime) {
		logger.info("[NickServiceImpl - getNicks start]");
		List<NickUser> nickUsers = getNickUserDAOService().getNickUserList(uids);
		getNickUserDAOService().updateNickUserOverdueTime(uids,overdueTime);
		List<NickUser> others =  getOthers(nickUsers, uids,overdueTime);
		getNickUserDAOService().updateNickUserList(others);
		logger.info("[NickServiceImpl - getNicks end]");
		return getNickUserDAOService().getNickUserList(uids);
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
			if(!userMap.containsKey(uid)){
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

	public void setNickUserDAOService(NickUserDAOService nickUserDAOService) {
		this.nickUserDAOService = nickUserDAOService;
	}

	public NickUserDAOService getNickUserDAOService() {
		return nickUserDAOService;
	}
}
