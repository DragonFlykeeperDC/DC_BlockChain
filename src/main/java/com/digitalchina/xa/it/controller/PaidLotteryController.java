package com.digitalchina.xa.it.controller;

import java.io.IOException;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.dao.TPaidlotteryDetailsDAO;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.TPaidlotteryDetailsDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
import com.digitalchina.xa.it.service.TPaidlotteryService;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.TConfigUtils;

@Controller
@RequestMapping(value = "/paidLottery")
public class PaidLotteryController {
	
	@Autowired
	private TPaidlotteryService tPaidlotteryService;
	@Autowired
	private EthAccountService ethAccountService;
	@Autowired
	private TPaidlotteryDetailsDAO tPaidlotteryDetailsDAO;
	
	@ResponseBody
	@PostMapping("/insertLotteryInfo")
	public Map<String, Object> insertLotteryInfo(
	        @RequestParam(name = "param", required = true) String jsonValue){
		return null;
	}
	@Transactional
	@ResponseBody
	@GetMapping("/selectLotteryInfo")
	public Map<String, Object> selectLotteryInfo(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 1.插入detail信息
		 * 2.调用kafka进行合约交易
		 */
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
		if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
			modelMap.put("data", "LotteryOver");
			return modelMap;
		}
		modelMap.put("data", "success");
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/getCurrentRMBId")
	public Map<String, Object> getCurrentRMBId(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		Integer id = tPaidlotteryService.selectLastRMBLottery();
		modelMap.put("data", id);
		
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/updateReward")
	public Map<String, Object> updateReward(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer id = Integer.valueOf(jsonObj.getString("id"));
		String reward = jsonObj.getString("reward");
		tPaidlotteryService.updateLotteryReward(id, reward);
		
		modelMap.put("data", "true");
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/runOptionLottery")
	public Map<String, Object> runOptionLottery(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer id = Integer.valueOf(jsonObj.getString("lotteryId"));
		Integer option = Integer.valueOf(jsonObj.getString("option"));
		tPaidlotteryService.runOptionLottery(id, option);
		
		modelMap.put("data", "true");
		return modelMap;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/insertLotteryDetails")
	public Map<String, Object> insertLotterydetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 1.插入detail信息
		 * 2.调用kafka进行合约交易
		 */
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		Integer backup4 = Integer.valueOf(jsonObj.getString("option"));
		String itcode = jsonObj.getString("itcode");
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("unitPrice")) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(jsonObj.getString("unitPrice")) > Double.valueOf(balance.toString())-10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新 表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), "", "", 0, backup4);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		//向kafka发送请求，参数为itcode, transactionId,  金额？， lotteryId？; 产生hashcode，更新account字段，并返回hashcode与transactionId。
//		String url = TConfigUtils.selectValueByKey("kafka_address_test") + "/lottery/buyTicket";
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		
		modelMap.put("data", "success");
		return modelMap;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/inviteLotteryDetails")
	public Map<String, Object> inviteLotteryDetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer lotteryId = Integer.valueOf(jsonObj.getString("lotteryId"));
		//此处默认为5，代表未激活的夺宝码
		Integer backup4 = Integer.valueOf(jsonObj.getString("option"));
		String itcode = jsonObj.getString("itcode");
		String inviteItcode = jsonObj.getString("inviteItcode");
		//不允许邀请自身
		if(itcode == inviteItcode){
			modelMap.put("data", "feifa");
			return modelMap;
		}
		//itcode合法性
		EthAccountDomain ead = ethAccountService.selectDefaultEthAccount(inviteItcode);
		if(ead == null){
			modelMap.put("data", "InviteItcodeIsIllegaly");
			return modelMap;
		}
		//用户邀请已达上限
		List<TPaidlotteryDetailsDomain> tempz1z = tPaidlotteryService.selectHaveInvitedByItcodeAndLotteryId(itcode, lotteryId);
		if(tempz1z.size() - 20 >= 0){
			modelMap.put("data", "InviteCountMoreThanLimit");
			return modelMap;
		}
		
		//该用户已邀请
		List<TPaidlotteryDetailsDomain> tempz2z = tPaidlotteryService.selectIfInvitedByItcodeAndLotteryId(itcode, inviteItcode, lotteryId);
		if(!tempz2z.isEmpty()){
			modelMap.put("data", "ThisItcodeHasBeenInvited");
			return modelMap;
		}
				
		//查询inviteItcode是否已被邀请
//		List<TPaidlotteryDetailsDomain> tempzz = tPaidlotteryService.selectUninviteLotteryDetailsByItcodeAndLotteryId(inviteItcode, lotteryId);
//		if(!tempzz.isEmpty()){
//			modelMap.put("data", "UserHasBeenInvited");
//			return modelMap;
//		}
		//未被邀请则进行下述步骤
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("unitPrice")) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(jsonObj.getString("unitPrice")) > Double.valueOf(balance.toString()) - 10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
		
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新Info表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option（backup4）选项
		//20180118 添加inviteItcode（backup1）选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), inviteItcode, itcode, 0, backup4);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		//向kafka发送请求，参数为itcode, transactionId,  金额？， lotteryId？; 产生hashcode，更新account字段，并返回hashcode与transactionId。
//		String url = TConfigUtils.selectValueByKey("kafka_address_test") + "/lottery/buyTicket";
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		//kafka那边更新account和hashcode
		//定时任务，查询到
		
		modelMap.put("data", "success");
		return modelMap;
	}
	
	@ResponseBody
	@PostMapping("/getResult")
	public Map<String, Object> kafkaUpdateDetails(
			@RequestParam(name = "param", required = true) String jsonValue){
		return null;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getOne")
	public Map<String, Object> selectLotteryInfoById(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		int id = Integer.valueOf(jsonObj.getString("id"));
		
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/getData")
	public Map<String, Object> getData(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
//		String itcode = jsonObj.getString("itcode");
//		int id = Integer.valueOf(jsonObj.getString("id"));
		//查询当前的未开奖SMB抽奖
		TPaidlotteryInfoDomain smbTpid = tPaidlotteryService.selectOneSmbTpid();
		//查询当前的未开奖RMB红包抽奖
		List<TPaidlotteryInfoDomain> hbTpidList = tPaidlotteryService.selectHbTpids();
		//查询多选项的抽奖
		List<TPaidlotteryInfoDomain> otherTpidList = tPaidlotteryService.selectOtherTpids();
		List<TPaidlotteryInfoDomain> newOpenList = tPaidlotteryService.selectNewOpen(Integer.valueOf(TConfigUtils.selectValueByKey("lottery_show_finish_size")));
		
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for(TPaidlotteryInfoDomain tpid : newOpenList){
	        tpid.setBackup1(sdf.format(tpid.getLotteryTime()));
		}
		
		modelMap.put("smbData", JSONObject.toJSON(smbTpid));
		modelMap.put("hbData", JSONObject.toJSON(hbTpidList));
		modelMap.put("otherData", JSONObject.toJSON(otherTpidList));
		modelMap.put("newOpen", JSONObject.toJSON(newOpenList));
		
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/unfinished")
	public Map<String, Object> selectLotteryInfoUnfinished(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<TPaidlotteryInfoDomain> tpidList = tPaidlotteryService.selectLotteryInfoByFlag(0);
		modelMap.put("success", true);
		modelMap.put("data", JSONObject.toJSON(tpidList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/finished")
	public Map<String, Object> selectLotteryInfofinished(){
		Map<String, Object> modelMap = new HashMap<String, Object>();
		List<TPaidlotteryInfoDomain> tpidList = tPaidlotteryService.selectLotteryInfoByFlag(1);
		modelMap.put("success", true);
		modelMap.put("data", JSONObject.toJSON(tpidList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryInfo/all")
	public void selectLotteryInfoAll(){
		/*
		 * 排序逻辑
		 * 1.按时间，当前时间到开奖时间（XX%）
		 * 2.按奖池，当前奖池到开奖金额（XX%）
		 * 3.按参与人数，当前人数到开奖人数（XX%）
		 */
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/{lotteryDetailId}")
	public void selectlotteryDetailById(
			@RequestParam(name = "param", required = true) String jsonValue){
		
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/myJoin")
	public Map<String, Object> selectlotteryDetailByItcode(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcode(itcode);
		
		modelMap.put("data", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/myWin")
	public Map<String, Object> selectMyWin(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectLotteryDetailsByItcodeAndResult(itcode,2);
		
		modelMap.put("data", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@ResponseBody
	@GetMapping("/lotteryDetail/{lotteryInfoId}")
	public void selectlotteryDetailByLotteryInfoId(
			@RequestParam(name = "param", required = true) String jsonValue){
		/*
		 * 某抽奖参与详情。
		 */
		//tPaidlotteryService.selectLotteryDetailsByLotteryId(lotteryId);
	}
	//查询邀请我的及界面展示
	@Transactional
	@ResponseBody
	@GetMapping("/lotteryInfo/getInvite")
	public Map<String, Object> selectInviteLotteryInfoById(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		String itcode = jsonObj.getString("itcode");
		int id = Integer.valueOf(jsonObj.getString("id"));
		
		TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(id);
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectInviteLotteryDetailsByItcodeAndLotteryId(itcode, id);
		
		for(TPaidlotteryDetailsDomain tpldd : tpddList){
	        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			tpldd.setHashcode(sdf.format(tpldd.getBuyTime()));
		}
		modelMap.put("infoData", JSONObject.toJSON(tpid));
		modelMap.put("detailData", JSONObject.toJSON(tpddList));
		return modelMap;
	}
	
	@Transactional
	@ResponseBody
	@GetMapping("/acceptInvite")
	public Map<String, Object> acceptInvite(
			@RequestParam(name = "param", required = true) String jsonValue){
		Map<String, Object> modelMap = DecryptAndDecodeUtils.decryptAndDecode(jsonValue);
		if(!(boolean) modelMap.get("success")){
			return modelMap;
		}
		JSONObject jsonObj = JSONObject.parseObject((String) modelMap.get("data"));
		Integer idKey = Integer.valueOf(jsonObj.getString("id"));
		
		TPaidlotteryDetailsDomain tpldd = tPaidlotteryService.selectLotteryDetailsById(idKey);
		String itcode = tpldd.getBackup1();
		String inviteItcode = tpldd.getBackup2();
		Integer lotteryId = tpldd.getLotteryId();
		//1.查出该条记录，将backup4置为0,其余都置为7；(添加受邀请上限)
		List<TPaidlotteryDetailsDomain> tpddList = tPaidlotteryService.selectInviteLotteryDetailsByItcodeAndLotteryId(itcode, lotteryId);
		List<TPaidlotteryDetailsDomain> tpddList1 = tPaidlotteryService.selectAcceptInviteLotteryDetailsByItcodeAndLotteryId(itcode, lotteryId);
		Integer limit = Integer.valueOf(TConfigUtils.selectValueByKey("accept_invite_limit"));
		if(tpddList1.size() + 1 < limit){
			tPaidlotteryDetailsDAO.updateBackup4From5To0(idKey);
		}else if(tpddList1.size() + 1 == limit){
			tPaidlotteryDetailsDAO.updateBackup4From5To0(idKey);
			for(TPaidlotteryDetailsDomain tplddTemp : tpddList){
				if((tplddTemp.getBackup4() != 0) && (tplddTemp.getId() != idKey) ){
					tPaidlotteryDetailsDAO.updateBackup4From5To7(tplddTemp.getId());
				}
			}
		}else {
			modelMap.put("data", "acceptInviteLimit");
			return modelMap;
		}
		
		//2.为自己再购买一张夺宝券，backup1=admin，backup2=邀请人		
		BigInteger turnBalance = BigInteger.valueOf( Long.valueOf(jsonObj.getString("unitPrice")) * 10000000000000000L);
		
		//余额判断
		try {
			Web3j web3j =Web3j.build(new HttpService(TConfigUtils.selectIp()));
			BigInteger balance = web3j.ethGetBalance(ethAccountService.selectDefaultEthAccount(itcode).getAccount(),DefaultBlockParameterName.LATEST).send().getBalance().divide(BigInteger.valueOf(10000000000000000L));
			if(Double.valueOf(jsonObj.getString("unitPrice")) > Double.valueOf(balance.toString()) - 10) {
				modelMap.put("data", "balanceNotEnough");
				return modelMap;
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("查询余额失败");
		}
			
		//判断是否达到所需金额
		synchronized(this){
			TPaidlotteryInfoDomain tpid = tPaidlotteryService.selectLotteryInfoById(lotteryId);
			if(tpid.getNowSumAmount() >= tpid.getWinSumAmount()) {
				modelMap.put("data", "LotteryOver");
				return modelMap;
			}
			//直接更新Info表nowSumAmount、backup4（待确认交易笔数）
			tPaidlotteryService.updateNowSumAmountAndBackup4(lotteryId);
		}
		
		//向t_paidlottery_details表中插入信息， 参数为lotteryId, itcode, result(0), buyTime
		//20180114 添加option（backup4）选项
		//20180118 添加inviteItcode（backup1）选项
		TPaidlotteryDetailsDomain tpdd = new TPaidlotteryDetailsDomain(lotteryId, itcode, "", "", "", 0, "", "", new Timestamp(new Date().getTime()), "admin", inviteItcode, 0, 0);
		int transactionId = tPaidlotteryService.insertLotteryBaseInfo(tpdd);
		System.out.println("transactionId" + transactionId);
		
		String url = TConfigUtils.selectValueByKey("kafka_address") + "/lottery/buyTicket";
		System.err.println(url);
		String postParam = "itcode=" + itcode + "&turnBalance=" + turnBalance.toString() + "&transactionDetailId=" + transactionId;
		HttpRequest.sendPost(url, postParam);
		modelMap.put("data", "success");
		return modelMap;
	}
}
