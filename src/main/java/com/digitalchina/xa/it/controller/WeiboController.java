package com.digitalchina.xa.it.controller;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.digitalchina.xa.it.weibo.weibo4j.Friendships;
import com.digitalchina.xa.it.weibo.weibo4j.Oauth;
import com.digitalchina.xa.it.weibo.weibo4j.Timeline;
import com.digitalchina.xa.it.weibo.weibo4j.http.AccessToken;
import com.digitalchina.xa.it.weibo.weibo4j.model.Source;
import com.digitalchina.xa.it.weibo.weibo4j.model.Status;
import com.digitalchina.xa.it.weibo.weibo4j.model.StatusWapper;
import com.digitalchina.xa.it.weibo.weibo4j.model.User;
import com.digitalchina.xa.it.weibo.weibo4j.model.UserWapper;
import com.digitalchina.xa.it.weibo.weibo4j.model.WeiboException;
import com.digitalchina.xa.it.weibo.weibo4j.util.BareBonesBrowserLaunch;



@Controller
@RequestMapping(value = "/weibo")
public class WeiboController {
	
	@Autowired
	private TableController tableController;
	
	@ResponseBody
	@RequestMapping("/openURL")
	@Transactional
	public void openURL() throws WeiboException {
		Oauth oauth = new Oauth();
		BareBonesBrowserLaunch.openURL(oauth.authorize("code"));
		System.out.println(oauth.authorize("code"));
	}
	
	@ResponseBody
	@RequestMapping("/code")
	@Transactional
	public Map<String, Object> code(@RequestParam(name = "itcode", required = true)String itcode ,
			@RequestParam(name = "code", required = true)  String code,
			@RequestParam(name = "screen_name", required = true) String screen_name) throws ClassNotFoundException, SQLException{
		Map<String, Object> map1 = new HashMap<>();
		System.out.println(code);
		System.out.println(screen_name);
		Oauth oauth = new Oauth();
		try {
			//建粉丝表
			String param = "{\"field1\":\"itcode\",\"type1\":\"varchar(20)\",\"field2\":\"screen_name\",\"type2\":\"varchar(20)\",\"field3\":\"follower_uid\",\"type3\":\"varchar(20)\",\"field4\":\"follow_screen_name\",\"type4\":\"varchar(20)\",\"field5\":\"location\",\"type5\":\"varchar(20)\",\"tableName\":\"weibo_followers\",\"itcode\":\""+itcode+"\"}";
			Map<String, Object> map = tableController.createTable(param);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			//建关注表
			String param = "{\"field1\":\"itcode\",\"type1\":\"varchar(20)\",\"field2\":\"screen_name\",\"type2\":\"varchar(20)\",\"field3\":\"friend_uid\",\"type3\":\"varchar(20)\",\"field4\":\"friend_screen_name\",\"type4\":\"varchar(20)\",\"field5\":\"location\",\"type5\":\"varchar(20)\",\"tableName\":\"weibo_friends\",\"itcode\":\""+itcode+"\"}";
			Map<String, Object> map = tableController.createTable(param);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
		//建微博表
		String param = "{\"field1\":\"id\",\"type1\":\"varchar(255)\",\"field2\":\"screen_name\",\"type2\":\"varchar(255)\",\"field3\":\"text\",\"type3\":\"varchar(255)\",\"field4\":\"created_at\",\"type4\":\"varchar(255)\",\"field5\":\"source\",\"type5\":\"varchar(255)\",\"tableName\":\"weibo_timeline\",\"itcode\":\""+itcode+"\"}";
		Map<String, Object> map2 = tableController.createTable(param);
		System.out.println(map2.get("success"));
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
			AccessToken accessToken = null;
			try {
				//使用code为用户生成accessToken
				accessToken = oauth.getAccessTokenByCode(code);
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			String token = accessToken.getAccessToken();
			Friendships fm = new Friendships(token);
			try {
				//获取用户粉丝列表
				UserWapper users = fm.getFollowersByName(screen_name);
				for(User u : users.getUsers()){
					String follow_uid = u.getId();
					String follow_screen_ame = u.getScreenName();
					String location = u.getLocation();
					System.out.println(u.getScreenName());
					System.out.println(u.toString());
					String fieldValues = "'"+itcode+"','"+screen_name + "','"+follow_uid+"','"+follow_screen_ame+"','"+location+"'";
					String fieldNames = "itcode,screen_name,follower_uid,follow_screen_name,location";
					Map<String, Object> map = tableController.addDataToTable("weibo_followers", itcode, fieldNames, fieldValues);
					System.out.println(map.get("success"));
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			//获取用户发布的最新微博
			Timeline tm = new Timeline(token);
			try {
				StatusWapper status = tm.getUserTimeline();
				List<Status> statuses = status.getStatuses();
				for (Status status2 : statuses) {
					String id = status2.getId();
					String text = status2.getText();
					Date createdAt = status2.getCreatedAt();
					DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					String date = sdf.format(createdAt);
					String url = status2.getSource().getUrl();
					String name = status2.getSource().getName();
					String fieldValues = "'"+id+"','"+screen_name + "','"+text+"','"+date+"','"+name+":"+url+"'";
					String fieldNames = "id,screen_name,text,created_at,source";
					tableController.addDataToTable("weibo_timeline", itcode, fieldNames, fieldValues);
					
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			//获取用户关注列表
			try {
				UserWapper users = fm.getFriendsByScreenName(screen_name);
				for(User u : users.getUsers()){
					String friend_uid = u.getId();
					String friend_screen_ame = u.getScreenName();
					String location = u.getLocation();
					System.out.println(u.getScreenName());
					System.out.println(u.toString());
					String fieldValues = "'"+itcode+"','"+screen_name + "','"+friend_uid+"','"+friend_screen_ame+"','"+location+"'";
					String fieldNames = "itcode,screen_name,friend_uid,friend_screen_name,location";
					Map<String, Object> map = tableController.addDataToTable("weibo_friends", itcode, fieldNames, fieldValues);
					System.out.println(map.get("success"));
				}
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			map1.put("success", true);
			return map1;
		
	}
		
	
	
}
