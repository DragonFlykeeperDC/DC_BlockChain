																										package com.digitalchina.xa.it.util;


import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.digitalchina.xa.it.model.EthAccountDomain;
import com.digitalchina.xa.it.service.EthAccountService;

/**
 * 
 * @ClassName: GetPersonalDBPwdUtils 
 * @Description: 根据itcode查询个人数据库连接密码
 * @author fannl
 * @date 2019-05-07 17:37:14 
 * @version 1.0 
 *
 */
@Component
public class GetPersonalDBPwdUtils {
	@Autowired
	private EthAccountService ethAccountService;
	private static EthAccountService ethAccountService1;
	@PostConstruct
	public void init() {
		ethAccountService1 = ethAccountService;
	}
	public static String findPersonalDBPwd(String itcode){
		EthAccountDomain ethAccountDomain = ethAccountService1.selectDefaultEthAccount(itcode);
		String account = ethAccountDomain.getAccount();
		String password = account.substring(0, 6);
//		System.out.println(password);
		return password;
	}
	
}
