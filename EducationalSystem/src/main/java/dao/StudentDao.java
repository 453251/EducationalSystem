package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import bean.ClassCourse;
import bean.Semester;
import bean.Student;
import bean.StudentCourseReport;
import jakarta.security.auth.message.callback.PrivateKeyCallback.Request;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.C3p0Tool;

public class StudentDao {
	QueryRunner queryRunner = new QueryRunner(C3p0Tool.getDataSource());
	/*
	 * 获取学生信息
	 */
    public List<Student> getStudent(String sno){
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
        try{
        	conn.setAutoCommit(false);
        	conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        	String sql = "select*from Students as s where s.sno = ?";
        	Object[] param = {sno};
        	List<Student> list = queryRunner.query(sql, new BeanListHandler<Student>(Student.class),param);
        	conn.commit();
            return list;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            return null;     
        }	
    }
    /*
     * 更新密码
     */
    public void update(String sno, String skey) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
    	try {
    		conn.setAutoCommit(false);
    		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		String sql = "update Students_account as s set s.skey = ? where s.sno = ?";
    		Object[] params = {skey,sno};
    		queryRunner.update(sql, params);
    		conn.commit();
    	}
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
        }	
    }
    /*
     * 获取学期列表
     */
	public List<Semester> getTermname() {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
    	try {
    		conn.setAutoCommit(false);
    		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		String sql = "select distinct cdate from courses";
    		List<Semester> list = queryRunner.query(sql, new BeanListHandler<Semester>(Semester.class));
    		conn.commit();
            return list;
    	}
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            return null;
        }
	}
	/*
	 * 课程成绩，学期查询和课程的模糊查询
	 */
	public Map<String, Object> getScoreList(HttpServletRequest request,HttpServletResponse response,String sno,String cname, String cdate) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page-1) * rows;
			StringBuffer sql = new StringBuffer("select*from 教师课程成绩 as s where s.sno = ?");
			List<Object> params = new ArrayList<Object>();
			params.add(sno);
			/*
			 * 利用课程的模糊查询和利用学期的查询
			 */
			if(cdate!=null && !cdate.isEmpty())
			{
				sql.append(" AND s.cdate = ?");
				params.add(cdate);
			}
			if(cname!=null && !cname.isEmpty())
			{
				sql.append(" AND s.cname LIKE ?");
				params.add("%"+cname+"%");
			}
			//需要注意的是，limit限制查询行数，只能放在查询语句的最后
			sql.append(" limit ?,?");params.add(start);params.add(rows);
			List<StudentCourseReport> list = queryRunner.query(sql.toString(), new BeanListHandler<StudentCourseReport>(StudentCourseReport.class),params.toArray());
			StringBuffer countsql = new StringBuffer("select count(*) from 教师课程成绩 as s where s.sno = ?");
			List<Object> param = new ArrayList<Object>();
			param.add(sno);
			if(cdate!=null && !cdate.isEmpty())
			{
				countsql.append(" AND s.cdate = ?");
				param.add(cdate);
			}
			if(cname!=null && !cname.isEmpty())
			{
				countsql.append(" AND s.cname LIKE ?");
				param.add("%"+cname+"%");
			}
			Long total = queryRunner.query(countsql.toString(), new ScalarHandler<Long>(),param.toArray());
			Map<String, Object> result = new HashMap<String, Object>();
			result.put("total", total);
			result.put("rows", list);
			conn.commit();
            return result;
    	}
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            return null;
        }
	}
	/*
	 * 课程成绩查询，用于导出excel
	 */
	public List<StudentCourseReport> getScoreList(String sno, String cname, String cdate)
	{
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		StringBuffer sql = new StringBuffer("select*from 教师课程成绩 as s where s.sno = ?");
		List<Object> params = new ArrayList<Object>();
		params.add(sno);
		/*
		 * 利用课程的模糊查询和利用学期的查询
		 */
		if(cdate!=null && !cdate.isEmpty())
		{
			sql.append(" AND s.cdate = ?");
			params.add(cdate);
		}
		if(cname!=null && !cname.isEmpty())
		{
			sql.append(" AND s.cname LIKE ?");
			params.add("%"+cname+"%");
		}
		List<StudentCourseReport> list;
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			list = queryRunner.query(sql.toString(), new BeanListHandler<StudentCourseReport>(StudentCourseReport.class),params.toArray());
			conn.commit();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			return null;
		}
	}
	/*
	 * 课程表，学期查询，用于导出为excel
	 */
	public List<ClassCourse> getCourseList(String clno, String cdate) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			StringBuffer sql = new StringBuffer("select*from 班级课表 as c where c.clno = ?");
			List<Object> params = new ArrayList<Object>();
			params.add(clno);
			if(cdate!=null && !cdate.isEmpty())
			{
				sql.append(" AND c.cdate = ?");
				params.add(cdate);
			}
			List<ClassCourse> list = queryRunner.query(sql.toString(), new BeanListHandler<ClassCourse>(ClassCourse.class),params.toArray());
            conn.commit();
			return list;
    	}
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
            return null;
        }
	}
	
}
