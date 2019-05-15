package com.digitalchina.xa.it.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.digitalchina.xa.it.util.DecryptAndDecodeUtils;
import com.digitalchina.xa.it.util.Encrypt;
import com.digitalchina.xa.it.util.EncryptImpl;
import com.digitalchina.xa.it.util.GetPersonalDBPwdUtils;
import com.digitalchina.xa.it.util.HttpRequest;
import com.digitalchina.xa.it.util.JDBCUtils;
import com.digitalchina.xa.it.util.TConfigUtils;
import com.digitalchina.xa.it.weibo.weibo4j.Friendships;
import com.digitalchina.xa.it.weibo.weibo4j.Oauth;
import com.digitalchina.xa.it.weibo.weibo4j.Timeline;
import com.digitalchina.xa.it.weibo.weibo4j.http.AccessToken;
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
	private JdbcTemplate jdbc;
	
	
	@ResponseBody
	@RequestMapping("/code")
	@Transactional
	public Map<String, Object> code(@RequestParam String code,@RequestParam String screen_name){
		System.out.println(code);
		System.out.println(screen_name);
		Oauth oauth = new Oauth();
			AccessToken accessToken = null;
			try {
				accessToken = oauth.getAccessTokenByCode(code);
			} catch (WeiboException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String token = accessToken.getAccessToken();
			Friendships fm = new Friendships(token);
			try {
				UserWapper users = fm.getFollowersByName(screen_name);
				for(User u : users.getUsers()){
					System.out.println(u.getScreenName());
					System.out.println(u.toString());
				}
				System.out.println(users.getNextCursor());
				System.out.println(users.getPreviousCursor());
				System.out.println(users.getTotalNumber());
			} catch (WeiboException e) {
				e.printStackTrace();
			}
			Encrypt encrypt = new EncryptImpl();
		return null;
		
	}
		
		
	/**
	 * @api {get} /table/uploadFile 将上传的excel表格中数据保存到数据库中
	 * @apiVersion 0.0.1
	 * 
	 * @apiName uploadFile
	 * @apiGroup TiDBGroupCreate
	 *
	 * @apiParam {String} itcode 用户的itcode.
	 * @apiParam {MultipartFile} file 上传的Excel文件.文件名不能以数字开头。上传文件内容格式必须为第一行为表字段，其他行是相应字段数据
	 * @apiParam file文件例 文件名：学生.xls    第一行：姓名 年龄 班级 。。。  第二行：杜伟 21 应数151 。。。 第三行。。。

	 *
	 * @apiSuccess {Boolean} success  是否上传成功，false未成功，可能原因：文件名以数字开头（数据库不支持表名为数字开头）；所传文件格式不正确（非Excel表形式）.
	 * @apiSuccess {String} msg  查询结果信息提示.
	 * 
	 * @apiSuccessExample Success-Response: 返回结果示例1
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "上传成功",
	 *         "success": true
	 *     }
	 *     
	 * @apiSuccessExample Success-Response: 返回结果示例2
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "文件名不能以数字开头哦",
	 *         "success": false
	 *     }
	 * @apiSuccessExample Success-Response: 返回结果示例3
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "请检查文件格式是否为Excel格式",
	 *         "success": false
	 *     }
	 * @apiSuccessExample Success-Response: 返回结果示例3
	 *     HTTP/1.1 200 OK
	 *     {
	 *         "msg": "插入数据失败，请检查数据格式",
	 *         "success": false
	 *     }
	 */
	@ResponseBody
	@RequestMapping("/lll")
	@Transactional
	public Map<String, Object> uploadFile(@RequestParam MultipartFile file,@RequestParam String itcode) throws IllegalStateException, IOException, ClassNotFoundException, SQLException {
		System.out.println(itcode);
		HashMap<String, Object> map = new HashMap<>();
		//获取文件名
		String filename = file.getOriginalFilename();
		HSSFWorkbook workbook;
		try {
			workbook = new HSSFWorkbook(file.getInputStream());			
		} catch (Exception e) {
			e.printStackTrace();
			map.put("success", false);
			map.put("msg", "请检查文件格式是否为Excel格式");
			return map;
		}
		int index = filename.indexOf(".");//首先获取字符的位置
		//去除文件后缀名做表名
		filename = filename.substring(0,index);
		if (filename.startsWith("[0-9]")) {
			map.put("success", false);
			map.put("msg", "文件名不能以数字开头哦");
			return map;
		}
		System.out.println(filename);
		String sql = "";
		String fieldNames = "";
		String fieldValues = "";
		//获取表中第一个sheet
		HSSFSheet sheetAt = workbook.getSheetAt(0);
		//获取总行数
		int lastRowNum = sheetAt.getLastRowNum();
		for (int i = 0; i <= lastRowNum; i++) {
			HSSFRow row = sheetAt.getRow(i);
			//获取总列数
			short lastCellNum = row.getLastCellNum();
			if (i == 0) {
				for (int j = 0; j <= lastCellNum; j++) {
					if (row.getCell(j) == null) {
					} else {
						fieldNames += row.getCell(j) + " varchar(255),";
					}
				}
				String lString = "select table_name from information_schema.tables where table_name = '" + filename
						+ "'";
				System.out.println(fieldNames);
				//判断表名是否存在，如果存在不用创建新表，直接插入值
				int querySQL = JDBCUtils.executeQuerySQL(lString, itcode, GetPersonalDBPwdUtils.findPersonalDBPwd(itcode));
				if (querySQL == 1) {
					System.out.println("表已存在");
				} else {
					//创建表
					sql = "CREATE TABLE " + filename + "(" + fieldNames.substring(0, fieldNames.length() - 1) + ")";
					System.out.println(sql);
					JDBCUtils.executeSQL(sql, itcode, GetPersonalDBPwdUtils.findPersonalDBPwd(itcode));
					System.out.println("将操作记录记录至建表信息表中"+"INSERT INTO table_info (itcode,table_name,table_status,fields,create_time)"
							+ "VALUES('"+itcode+"','"+filename+"',"+0+",'"+fieldNames.substring(0, fieldNames.length()-1)+new Timestamp(new Date().getTime())+"')");
					jdbc.execute("INSERT INTO table_info (itcode,table_name,table_status,fields)"
							+ "VALUES('"+itcode+"','"+filename+"',"+0+",'"+fieldNames.substring(0, fieldNames.length()-1)+"')");
				}
			} else {
				fieldValues = "";
				for (int j = 0; j <= lastCellNum; j++) {
					if (!(row.getCell(j) == null)) {
						fieldValues += "'"+row.getCell(j) + "',";
					}
				}
				//每次插入一行值
				sql = "INSERT INTO " + filename + " VALUES(" + fieldValues.substring(0, fieldValues.length() - 1) + ")";
				System.out.println(sql);
				try {
					JDBCUtils.executeSQL(sql, itcode, GetPersonalDBPwdUtils.findPersonalDBPwd(itcode));					
				} catch (Exception e) {
					e.printStackTrace();
					map.put("success", false);
					map.put("msg", "插入数据失败，请检查数据格式");
					return map;
				}
				DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Timestamp time = new Timestamp(new Date().getTime());
				String time1 = sdf.format(time);
				System.out.println("INSERT INTO add_data_detail (tableName,itcode,data,time) VALUES ('"+filename+"','"+itcode+"',\""+sql+"\",'"+time1+"')");
				jdbc.execute("INSERT INTO add_data_detail (tableName,itcode,data,time) VALUES ('"+filename+"','"+itcode+"',\""+sql+"\",'"+time1+"')");
			}
		}
		map.put("success", true);
		map.put("msg", "导入成功");
		return map;
	}

	
}
