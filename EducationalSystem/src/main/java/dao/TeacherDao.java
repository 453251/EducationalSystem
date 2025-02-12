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
import bean.StudentCourseReport;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.C3p0Tool;



public class TeacherDao {
	QueryRunner queryRunner = new QueryRunner(C3p0Tool.getDataSource());
	/*
	 * 更新密码
	 */
    public void update(String account, String password) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
    	try {
    		conn.setAutoCommit(false);
    		conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
    		String sql = "update teachers_account as t set t.tkey = ? where t.tno = ?";
    		Object[] params = {password, account};
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
	 * 教师课表，学期查询，同时用于导出为excel
	 */
	public List<ClassCourse> getCourseList(String tname, String cdate) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			StringBuffer sql = new StringBuffer("select*from 班级课表 as c where c.tname = ?");
			List<Object> params = new ArrayList<Object>();
			params.add(tname);
			if(cdate!=null && !cdate.isEmpty())
			{
				sql.append(" AND c.cdate = ?");
				params.add(cdate);
			}
			sql.append(" order by c.cno asc");
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
	
	/*
	 * 教师教的某门课的课程成绩，同时用于导出为excel
	 */
	public List<StudentCourseReport> getScoreList(String cno) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "select* from 教师课程成绩 as s where s.cno = ? order by s.sno asc";
			Object[] param = {cno};
			List<StudentCourseReport> list = queryRunner.query(sql, new BeanListHandler<StudentCourseReport>(StudentCourseReport.class),param);
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
	 * 教师登分表，分页
	 */
	public Map<String, Object> getStudentList(HttpServletRequest request,HttpServletResponse response,String tno,String cname, String cdate) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page-1) * rows;
			StringBuffer sql = new StringBuffer("select*from 教师课程成绩 as s where s.tno = ? and s.score is null");
			List<Object> params = new ArrayList<Object>();
			params.add(tno);
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
			StringBuffer countsql = new StringBuffer("select count(*) from 教师课程成绩 as s where s.tno = ? and s.score is null");
			List<Object> param = new ArrayList<Object>();
			param.add(tno);
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
	 * 更新学生成绩
	 */
	public void setStudentScore(String sno, String cno, float score) {
    	Connection conn = null;
    	conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update reports as r set r.score = ? where r.sno = ? and r.cno = ?";
			Object[] params = {score,sno,cno};
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
	 * 自动计算学分，已在数据库端使用触发器实现
	 */
//	public void setStudentCredit(String sno, double credit) {
//		try {
//    		session=HibernateUtil.getSession();
//            transaction=session.beginTransaction();
//            String hql="UPDATE  Student as s SET s.scredit= s.scredit+? WHERE s.sno=?";
//    		Query query=session.createQuery(hql);
//    		query.setParameter(0, credit);
//    		query.setParameter(1, sno);
//    		query.executeUpdate();
//            transaction.commit();
//            session.close();
//    	}
//        catch(Exception e)
//        {
//            e.printStackTrace(); 
//        }
//		
//	}
}
