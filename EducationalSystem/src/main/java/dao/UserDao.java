package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;


import bean.Student;
import bean.Teacher;
import bean.UserDB;
import util.C3p0Tool;
import util.PageTool;

/*
 * 用户的数据连接层，即与数据库进行交互
 */
public class UserDao {
	
	QueryRunner queryRunner = new QueryRunner(C3p0Tool.getDataSource());
	
	/*
	 * 用户登录
	 */
	public UserDB login(String account, String password)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		String sql = "select*from t_user where account = ? and password = ?";
		Object[] params = {account, password};
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			UserDB userDB = queryRunner.query(sql, new BeanHandler<UserDB>(UserDB.class),params);
			System.out.println(account.getClass());
			System.out.println(password.getClass());
			String[] sqls = {"select*from student where sno = ?",
							 "select*from teachers where tno = ?"};
			Object[] param = {account};
			if(userDB.getRole() == 0)
			{
				userDB.student = queryRunner.query(sqls[0], new BeanHandler<Student>(Student.class),param);
				userDB.student.setSkey(password);
			}
			else if(userDB.getRole() == 1)
			{
				userDB.teacher = queryRunner.query(sqls[1], new BeanHandler<Teacher>(Teacher.class),param);
				userDB.teacher.setTpassword(password);
			}
			conn.commit();
			return userDB;
			
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/*
	 * 用户列表 分页
	 */
	public PageTool<UserDB> list(String currentPage, String pageSize)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			StringBuffer listSql = new StringBuffer("select * ");
			StringBuffer countSql = new StringBuffer("select count(*) ");
			StringBuffer sql = new StringBuffer("from t_user");
			// 获取记录总数
			Long total = queryRunner.query(countSql.append(sql).toString(), new ScalarHandler<Long>());
			// 初始化分页工具
			PageTool<UserDB> pageTools = new PageTool<UserDB>(total.intValue(), currentPage, pageSize);
			sql.append(" limit ?,?");
			// 当前页的数据
			List<UserDB> list = queryRunner.query(listSql.append(sql).toString(), new BeanListHandler<UserDB>(UserDB.class),pageTools.getStartIndex(), pageTools.getPageSize());
			conn.commit();
			pageTools.setRows(list);
			System.out.println(pageTools);
			return pageTools;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return new PageTool<UserDB>(0, currentPage, pageSize);
	}
	/*
	 * 异步校验，防止创建相同账号
	 */
	public List<UserDB> getList(UserDB userDB)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		String sql = "select*from t_user where account = ?";
		Object[] params = {userDB.getAccount()};
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			List<UserDB> list = queryRunner.query(sql, new BeanListHandler<UserDB>(UserDB.class),params);
			conn.commit();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/*
	 * 管理员修改用户信息（修改姓名和密码）
	 */
	public Integer updUser(UserDB userDB)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		String[] sql = {"update students_account set skey = ?, sname = ? where sno = ?",
						"update teachers_account set tkey = ?, tname = ? where tno = ?",
						"update admin_account set akey = ?, aname = ? where ano = ?"};
		Object[] params = {userDB.getPassword(), userDB.getName(),userDB.getAccount()};
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			// 由于在前端是接收不到用户类型的数据，因此我这里还需要查询一下用户的role
			String select = "select role from t_user where account = ?";
			Object[] param = {userDB.getAccount()};
			UserDB temp = queryRunner.query(select, new BeanHandler<UserDB>(UserDB.class),param);
			Integer role = temp.getRole();
			Integer result = queryRunner.update(sql[role], params);
			conn.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	/*
	 *
	 * 删除用户
	 */
	public int delUser(String account)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		// 0学生，1教师，2管理员
		String[] sql = {"delete from students_account where ano = ?",
				"delete from teachers_account where tno = ?",
				"delete from admin_account where sno = ?"};
		Object[] params = {account};
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String select = "select role from t_user where account = ?";
			Object[] param = {account};
			UserDB temp = queryRunner.query(select, new BeanHandler<UserDB>(UserDB.class),param);
			Integer role = temp.getRole();
			Integer result = queryRunner.update(sql[role],params);
			conn.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			try {
				if(conn!=null)
				{
					conn.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return -1;	//执行不成功返回-1，防止编译错误
	}
	
	public Integer addUser(UserDB userDB)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		// 0学生，1教师，2管理员
		String[] sql = {"insert into students_account (sno,skey,sname) values (?,?,?)",
						"insert into teachers_account (tno,tkey,tname) values (?,?,?)",
						"insert into admin_account (ano,akey,aname) values (?,?,?)"};
		Object[] params = {userDB.getAccount(),userDB.getPassword(),userDB.getName()};
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			Integer result = queryRunner.update(sql[userDB.getRole()],params);
			conn.commit();
			return result;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
		}finally {
			if(conn!=null)
			{
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}
	
	
}