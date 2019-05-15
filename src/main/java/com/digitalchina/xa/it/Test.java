package com.digitalchina.xa.it;




import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.sql.Array;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock.Block;
import org.web3j.protocol.http.HttpService;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.controller.GameController;
import com.digitalchina.xa.it.controller.PaidLotteryController;
import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.dao.Single_double_games_lottery_infoDao;
import com.digitalchina.xa.it.dao.TConfigDAO;
import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.model.SingleDoubleGamesInfoDomain;
import com.digitalchina.xa.it.model.TConfigDomain;
import com.digitalchina.xa.it.model.TPaidlotteryInfoDomain;
import com.digitalchina.xa.it.service.EthAccountService;
//import com.digitalchina.xa.it.dao.Single_double_games_detailsDao;
import com.digitalchina.xa.it.service.GameService;
import com.digitalchina.xa.it.util.GetPersonalDBPwdUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.ResultUtil;
import com.digitalchina.xa.it.util.TConfigUtils;
import com.digitalchina.xa.it.weibo.weibo4j.Friendships;
import com.digitalchina.xa.it.weibo.weibo4j.Oauth;
import com.digitalchina.xa.it.weibo.weibo4j.Timeline;
import com.digitalchina.xa.it.weibo.weibo4j.model.Status;
import com.digitalchina.xa.it.weibo.weibo4j.model.StatusWapper;
import com.digitalchina.xa.it.weibo.weibo4j.model.User;
import com.digitalchina.xa.it.weibo.weibo4j.model.UserWapper;
import com.digitalchina.xa.it.weibo.weibo4j.model.WeiboException;
import com.digitalchina.xa.it.weibo.weibo4j.util.BareBonesBrowserLaunch;

import scala.util.Random;



@RunWith(SpringRunner.class)
@SpringBootTest
public class Test {
//	@Autowired
//	private JdbcTemplate jdbc;
//	@Autowired
//	private GameService gameService;
//	@Autowired
//	private Single_double_games_detailsDao single_double_games_detailsDao;
	@Autowired
	private EthAccountService ethAccountService;
//	@Autowired
//	private GameController gameController;
//	@Autowired
//	private TConfigDAO tconfigDAO;
//	@Autowired
//	private PaidLotteryController p;
//	private static String[] ip = {"http://10.7.10.124:8545","http://10.7.10.125:8545","http://10.0.5.217:8545","http://10.0.5.218:8545","http://10.0.5.219:8545"};
//	@Autowired
//	private Single_double_games_lottery_infoDao single_double_games_lottery_infoDao;
//	@org.junit.Test
//	public void insertNewBlock1() throws IOException{
//		EthAccountDomain ethAccountDomain = ethAccountService.selectDefaultEthAccount("duwei");
//		System.out.println(ethAccountDomain.getAccount());
//		
//	}
////		String param = "5F0169EFE1C71DAE1915E9FFAF1BC9A8DC735381E0C57BBAC7C0DFF2D84998341F810DAA0B13738041E32ED5994AC176689E6333D36634C0DF22892073B268930B3F5DD1A91533C6E3A6041C05F58E0D477BA72DFFD83A62D6E7940CA8DA8E6DF0418DE6EF3BF0F542B968FC6CB57E51AD0C3E732995CCEF5B8736F5C889F1DE651D3F93FB0518F6";
////		gameController.insertGameDetails(param);
////		Web3j web3j = Web3j.build(new HttpService(ip[new Random().nextInt(5)]));
////		Block winBlock = web3j.ethGetBlockByNumber(DefaultBlockParameterName.LATEST, true).send().getResult();
////		BigInteger blockNumber = winBlock.getNumber();
////		System.out.println(blockNumber);
////		int mod = blockNumber.mod(new BigInteger("500")).intValue();
////		System.out.println(mod);
////		gameService.generateWinTicketNew(5,10, 0);
//		//查询未结束的抽奖
////		List<SingleDoubleGamesInfoDomain> tpidList = single_double_games_lottery_infoDao.selectUnfinishedLottery();
////		if(tpidList.size() == 0) {
////			return;
////		}
////		for(int index = 0; index < tpidList.size(); index++) {
////			SingleDoubleGamesInfoDomain tpid = tpidList.get(index);
////			//查询抽奖details中，区块链交易已确认的个数
////			int count1 = single_double_games_detailsDao.selectCountByBackup3(tpid.getId(), 1);
////			if(count1 >= (tpid.getWinSumAmount() / tpid.getUnitPrice())) {
////				single_double_games_lottery_infoDao.updateBackup4To0(tpid.getId());
////			}
////		}
////		List<SingleDoubleGamesInfoDomain> tpidList0 = single_double_games_lottery_infoDao.selectRunLottery();
////		System.out.println(tpidList0.size()+"123");
////		if(tpidList0.size() == 0) {
////			return;
////		}
////		SingleDoubleGamesInfoDomain tpid0 = tpidList0.get(0);
////		if(tpid0.getTypeCode() == 0 || tpid0.getTypeCode() == 1){
////			gameService.runALottery(tpid0);
////		
////	}
////		TPaidlotteryInfoDomain tpid = new TPaidlotteryInfoDomain();
////		List<TConfigDomain> tconfigList = tconfigDAO.selectConfigByExtra("GameSzbInfo");
////		String lotteryInfo = tconfigList.get((int) (Math.random() * tconfigList.size())).getCfgValue();
////		System.out.println(lotteryInfo);
////		String[] infoList = lotteryInfo.split("##");
////		
////		tpid.setName(infoList[0]);
////		tpid.setDescription(infoList[1]);
////		tpid.setWinSumAmount(Integer.valueOf(infoList[2]));
////		tpid.setWinSumPerson(Integer.valueOf(infoList[3]));
////		tpid.setReward(infoList[4]);
////		tpid.setUnitPrice(Integer.valueOf(infoList[5]));
////		tpid.setLimitEveryday(Integer.valueOf(infoList[6]));
////		tpid.setWinCount(Integer.valueOf(infoList[7]));
////		
////		tpid.setFlag(0);
////		//1为神州币抽奖
////		tpid.setTypeCode(1);
////		tpid.setNowSumAmount(0);
////		tpid.setBackup4(0);
////		tpid.setBackup5(0);
////		tpid.setLotteryTime(new Timestamp(new Date().getTime()));
////		
////		tpid.setNowSumPerson(0);
////		tpid.setWinDate("");
////		tpid.setBackup1("");
////		tpid.setBackup2("");
////		tpid.setBackup3("");
////		single_double_games_lottery_infoDao.insertLotteryInfo(tpid);
////				   		  "{\"field1\":\"1\",\"type1\":\"1\",\"field2\":\"2\",\"type2\":\"2\"}"
////		System.out.println("查找数据库中有无重名表");
////		String tableName = "test1";
////		List<Map<String,Object>> list = jdbc.queryForList("select table_name from information_schema.tables where table_schema='dc_blockchain' and table_name = '"+tableName+"'");
////		System.out.println(list.size());
////		List<Map<String,Object>> list = jdbc.queryForList("select table_name from table_info where itcode = 'dede'");
////		if (list.size()>0) {
////			for (Map<String, Object> map : list) {
////				System.out.println(map.get("table_name"));
////			}
////		}
//		String data = "INSERT INTO "+1+"("+1+") VALUES ("+111+")";
//
//		System.out.println("INSERT INTO add_data_detail (tableName,itcode,data) VALUES ('"+1+"','"+2+"',\""+data+"\")");
//		
////	
//	}
//	@org.junit.Test
//	public void insertNewBlock() throws IOException, ClassNotFoundException, SQLException {
//		String token = "2.00D5amKDKJkQfE8a57353facqDv5UD";
//		Timeline tm = new Timeline(token);
//		try {
//			StatusWapper status = tm.getUserTimeline();
//			List<Status> statuses = status.getStatuses();
//			for (Status status2 : statuses) {
//				System.out.println(status2.getId()+"1111111");
//			}
//		} catch (WeiboException e) {
//			e.printStackTrace();
//		}
//	}
	@org.junit.Test
	public void insertNewBlock() throws IOException, ClassNotFoundException, SQLException, WeiboException {
		Oauth oauth = new Oauth();
		BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
		System.out.println(oauth.authorize("code"));
		System.out.print("Hit enter when it's done.[Enter]:");
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		String code = br.readLine();
	}
	
}