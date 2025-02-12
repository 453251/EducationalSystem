package service;

import util.PageTool;
import dao.UserDao;

import java.util.List;

import bean.UserDB;

/*
 * 用户的业务层，即调用后端层
 */
public class UserService {
	
	private UserDao userDao = new UserDao();
	
	/*
	 * 登录
	 */
	public UserDB login(String account, String password)
	{
		return userDao.login(account, password);
	}
	
	public PageTool<UserDB> list(String currentPage, String pageSize)
	{
		return userDao.list(currentPage,pageSize);
	}
	
	/*
	 * 异步校验
	 */
	public List<UserDB> getList(UserDB userDB)
	{
		return userDao.getList(userDB);
	}
	/*
	 * 添加用户
	 */
	public Integer addUser(UserDB userDB)
	{
		return userDao.addUser(userDB);
	}
	/*
	 * 管理员修改用户密码
	 */
	public Integer updUser(UserDB userDB)
	{
		return userDao.updUser(userDB);
	}
	
	/*
	 * 用户删除
	 * 
	 */
	public Integer delUser(String account)
	{
		return userDao.delUser(account);
	}
}
