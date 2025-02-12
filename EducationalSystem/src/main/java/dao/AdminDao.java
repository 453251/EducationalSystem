package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import com.mysql.cj.Query;
import com.oracle.wls.shaded.org.apache.bcel.generic.NEW;

import bean.Course;
import bean.Stu;
import bean.Student;
import bean.StudentCourseReport;
import bean.StudentGPA;
import bean.Teacher;
import bean.UserDB;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import util.C3p0Tool;
import util.PageTool;

public class AdminDao {
	QueryRunner queryRunner = new QueryRunner(C3p0Tool.getDataSource());
	/*
	 * 绩点表查询，分页
	 */
	public Map<String, Object> getScoreList(HttpServletRequest request,HttpServletResponse response,String cdate) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			/*
			 * 成绩查询，分页
			 */
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page-1) * rows;
			StringBuffer sql = new StringBuffer("select s.sno, s.sname, SUM(s.score * s.ccredit) as totalScore, SUM(s.ccredit) as totalCredits from 教师课程成绩 as s");
			List<Object> params = new ArrayList<Object>();
			/*
			 * 可以进行学期查询，课程的模糊查询
			 */
			if(cdate!=null && !cdate.isEmpty())
			{
				sql.append(" where s.cdate = ?");
				params.add(cdate);
			}
			sql.append(" group by s.sno,s.sname");
			sql.append(" order by s.sno");
			//limit要放在最后
			sql.append(" limit ?,?");params.add(start);params.add(rows);
			List<StudentGPA> list = queryRunner.query(sql.toString(), new BeanListHandler<StudentGPA>(StudentGPA.class),params.toArray());
			//查询记录总数
			String countsql = "select count(*) from students";
			Long total = queryRunner.query(countsql, new ScalarHandler<Long>());
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
					e1.printStackTrace();
				}
            }
            return null;
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
	}
	/*
	 * 绩点表，用于导出excel
	 */
	public List<StudentGPA> getScoreList(String cdate)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		StringBuffer sql = new StringBuffer("select s.sno, s.sname, SUM(s.score * s.ccredit) as totalScore, SUM(s.ccredit) as totalCredits from 教师课程成绩 as s");
		List<Object> params = new ArrayList<Object>();
		/*
		 * 可以进行学期查询，课程的模糊查询
		 */
		if(cdate!=null && !cdate.isEmpty())
		{
			sql.append(" where s.cdate = ?");
			params.add(cdate);
		}
		sql.append(" group by s.sno,s.sname");
		sql.append(" order by s.sno");
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			List<StudentGPA> list = queryRunner.query(sql.toString(), new BeanListHandler<StudentGPA>(StudentGPA.class),params.toArray());
			conn.commit();
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			if(conn!=null) {
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
				
			return null;
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
	}
	/*
	 * 学生表，分页（使用前端的pagination分页工具）
	 */
	public Map<String, Object> getStudentList(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page - 1) * rows;
            String sql="select*from Student order by sno asc limit ?,?";
            Object[] params = {start,rows};
            List<Student> list = queryRunner.query(sql, new BeanListHandler<Student>(Student.class),params);
            String countsql = "select count(*) from Student";
            Long total = queryRunner.query(countsql, new ScalarHandler<Long>());
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total", total);
            result.put("rows", list);
			conn.commit();
            return result;
        }
        catch(Exception e)
        {
        	System.out.println("查询异常");
        	if(conn!=null)
        	{
        		try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
        	}
            e.printStackTrace();
            return null;     
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
	}
	
	/*
	 * 学生表，分页（使用后端的pagination分页工具）
	 */
	public PageTool<Student> getStudentList(String currentPage, String pageSize)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			StringBuffer listSql = new StringBuffer("select * ");
			StringBuffer countSql = new StringBuffer("select count(*) ");
			StringBuffer sql = new StringBuffer("from Student");
			// 获取记录总数
			Long total = queryRunner.query(countSql.append(sql).toString(), new ScalarHandler<Long>());
			// 初始化分页工具
			PageTool<Student> pageTools = new PageTool<Student>(total.intValue(), currentPage, pageSize);
			sql.append(" limit ?,?");
			// 当前页的数据
			List<Student> list = queryRunner.query(listSql.append(sql).toString(), new BeanListHandler<Student>(Student.class),pageTools.getStartIndex(), pageTools.getPageSize());
			pageTools.setRows(list);
			conn.commit();
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
		return new PageTool<Student>(0, currentPage, pageSize);
	}
	/*
	 * 学生表，用于导出excel
	 */
	public List<Student> getStudentList()
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="select*from Student order by sno asc";
			List<Student> list = queryRunner.query(sql, new BeanListHandler<Student>(Student.class));
			conn.commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return null;
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
	}
	/*
	 * 添加学生
	 */
	public String addStudent(Stu student) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "insert into students(Sno,Sname,Ssex,Sage,Saddress,Scredit,Clno,Syear) values (?,?,?,?,?,?,?,?)";
			Object[] params = {student.getSno(),student.getSname(),student.getSsex(),student.getSage(),student.getSaddress(),student.getScredit(),student.getClno(),student.getSyear()};
			queryRunner.update(sql, params);
			conn.commit();
            return "success";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
            return "fail";     
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
	}
	/*
	 * 删除学生
	 */
	public String deleteStudent(Stu student) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "delete from students where sno = ?";
			Object[] param = {student.getSno()};
			queryRunner.update(sql, param);
			conn.commit();
			return "success";
		}
		catch(Exception e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return "fail";
		}
	}
	/*
	 * 修改学生姓名
	 */
	public void setStudentName(String sno, String sname) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  students as s set s.sname=? WHERE s.sno=?";
            Object[] params = {sname, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
	}
	/*
	 * 修改学生密码
	 */
	public void setStudentPassword(String sno, String spassword) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  students_account as s set s.skey=? WHERE s.sno=?";
            Object[] params = {spassword, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
		
	}
	/*
	 * 修改学生地址
	 */
	public void setStudentAddress(String sno, String saddress) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  students as s set s.saddress=? WHERE s.sno=?";
            Object[] params = {saddress, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
		
	}
	/*
	 * 修改学生性别
	 */
	public void setStudentSex(String sno, String ssex) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Students as s set s.ssex=? WHERE s.sno=?";
            Object[] params = {ssex, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
		
	}
	/*
	 * 修改学生年龄
	 */
	public void setStudentAge(String sno, int sage) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Students as s set s.sage=? WHERE s.sno=?";
            Object[] params = {sage,sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
	}
	/*
	 * 修改入学年份
	 */
	public void setStudentYear(String sno, int syear) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Students as s set s.syear=? WHERE s.sno=?";
            Object[] params = {syear, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
	}
	/*
	 * 修改学分
	 */
	public void setStudentCredit(String sno, double scredit) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Students as s set s.scredit=? WHERE s.sno=?";
            Object[] params = {scredit, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
	}
	/*
	 * 修改班级
	 */
	public void setStudentClno(String sno, String clno) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Students as s set s.clno=? WHERE s.sno=?";
            Object[] params = {clno, sno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
		}
	}
	/*
	 * 教师列表，分页
	 */
	public Map<String, Object> getTeacherList(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page - 1) * rows;
            String sql="select*from Teachers order by tno asc limit ?,?";
            Object[] params = {start,rows};
            List<Teacher> list = queryRunner.query(sql, new BeanListHandler<Teacher>(Teacher.class),params);
            String countsql = "select count(*) from teachers";
            Long total = queryRunner.query(countsql, new ScalarHandler<Long>());
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total",total);
            result.put("rows",list);
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
					e1.printStackTrace();
				}
            }
            return null;
        }
	}
	/*
	 * 教师列表，用于导出excel
	 */
	public List<Teacher> getTeacherList()
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="select*from Teachers order by tno asc";
			List<Teacher> list = queryRunner.query(sql, new BeanListHandler<Teacher>(Teacher.class));
			conn.commit();
			return list;
		} catch (Exception e) {
			e.printStackTrace();
			if(conn!=null)
			{
				try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}
			return null;
		}
	}
	/*
	 * 添加教师
	 */
	public String addTeacher(Teacher teacher) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "insert into teachers(Tno,Tname,Tsex,Tage,Tposition,Tphone) values (?,?,?,?,?,?)";
			Object[] params = {teacher.getTno(),teacher.getTname(),teacher.getTsex(),teacher.getTage(),teacher.getTposition(),teacher.getTphone()};
			queryRunner.update(sql, params);
			conn.commit();
            return "success";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            if(conn!=null)
            {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
            }
            return "fail";     
        }	
	}
	/*
	 * 删除教师
	 */
	public String deleteTeacher(Teacher teacher) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "delete from teachers where tno = ?";
			Object[] param = {teacher.getTno()};
			queryRunner.update(sql, param);
			conn.commit();
			return "success";
		}
		catch(Exception e) {
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
			return "fail";
		}
	}
	/*
	 * 修改教师信息
	 */
	public void setTeacherName(String tno, String tname) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="UPDATE  Teachers as t set t.tname=? WHERE t.tno=?";
            Object[] params = {tname,tno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
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
	 * 修改教师性别
	 */
	public void setTeacherSex(String tno, String tsex) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="UPDATE  Teachers as t set t.tsex=? WHERE t.tno=?";
            Object[] params = {tsex,tno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
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
	public void setTeacherAge(String tno, int tage) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            String sql="UPDATE  Teachers as t set t.tage=? WHERE t.tno=?";
            Object[] params = {tage,tno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
            e.printStackTrace();
            if(conn!=null) {
            	try {
					conn.rollback();
				} catch (SQLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
            }
		}
	}
	public void setTeacherPosition(String tno, String tposition) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="UPDATE  Teachers as t set t.tposition=? WHERE t.tno=?";
            Object[] params = {tposition,tno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
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
	public void setTeacherPhone(String tno, String tphone) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="UPDATE  Teachers as t set t.tphone=? WHERE t.tno=?";
            Object[] params = {tphone,tno};
            queryRunner.update(sql, params);
            conn.commit();
    	}
        catch(Exception e) {
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
	 * 修改课程信息
	 */
	public void setCourseName(String cno, String cname) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.cname=? WHERE c.cno=?";
			Object[] params = {cname,cno};
			queryRunner.update(sql, params);
			conn.commit();
    	}
        catch(Exception e) {
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
	public void setCourseTname(String cno, String tname)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.tname=? WHERE c.cno=?";
			Object[] params = {tname,cno};
			queryRunner.update(sql, params);
			conn.commit();
		}catch (Exception e) {
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
	public void setCourseTime(String cno, int chour) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.chour=? WHERE c.cno=?";
			Object[] params = {chour,cno};
			queryRunner.update(sql, params);
			conn.commit();
    	}
        catch(Exception e) {
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
	public void setCourseCredit(String cno, int ccredit) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.ccredit=? WHERE c.cno=?";
			Object[] params = {ccredit,cno};
			queryRunner.update(sql, params);
			conn.commit();
    	}
        catch(Exception e) {
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
	public void setCourseTerm(String cno, String cdate) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.cdate=? WHERE c.cno=?";
			Object[] params = {cdate,cno};
			queryRunner.update(sql, params);
			conn.commit();
    	}
        catch(Exception e) {
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
	public void setCourseTest(String cno, String cway) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "update Courses as c set c.cway=? WHERE c.cno=?";
			Object[] params = {cway,cno};
			queryRunner.update(sql, params);
			conn.commit();
    	}
        catch(Exception e) {
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
	 * 课程列表，分页
	 */
	public Map<String, Object> getCourseList(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page - 1) * rows;
            String sql="select*from courses order by cno asc limit ?,?";
            Object[] params = {start,rows};
            List<Course> list = queryRunner.query(sql, new BeanListHandler<Course>(Course.class),params);
            String countsql = "select count(*) from Courses";
            Long total = queryRunner.query(countsql, new ScalarHandler<Long>());
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
	 * 课程列表，用于导出excel
	 */
	public List<Course> getCourseList()
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="select*from courses order by cno asc";
			List<Course> list = queryRunner.query(sql, new BeanListHandler<Course>(Course.class));
			conn.commit();
			return list;
		} catch (Exception e) {
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
	 * 添加课程
	 */
	public String addCourse(Course course) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql = "insert into courses(Cno,Cname,Tname,Cdate,Cway,Ccredit,Chour) values(?,?,?,?,?,?,?)";
			Object[] params = {course.getCno(),course.getCname(),course.getTname(),course.getCdate(),course.getCway(),course.getCcredit(),course.getChour()};
			queryRunner.update(sql, params);
			conn.commit();
            return "success";
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
            return "fail";     
        }	
	}
	/*
	 * 删除课程
	 */
	public String deleteCourse(Course course) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        	String sql = "delete from courses where cno = ?";
        	Object[] param = {course.getCno()};
        	queryRunner.update(sql, param);
        	conn.commit();
            return "success";
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
            return "fail";     
        }
	}
	
	/*
	 * 用户列表，分页
	 */
	public Map<String, Object> getUserList(HttpServletRequest request, HttpServletResponse response) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			int page = Integer.parseInt(request.getParameter("page"));
			int rows = Integer.parseInt(request.getParameter("rows"));
			int start = (page - 1) * rows;
            String sql="select*from t_user order by account asc limit ?,?";
            Object[] params = {start,rows};
            List<UserDB> list = queryRunner.query(sql, new BeanListHandler<UserDB>(UserDB.class),params);
            String countsql = "select count(*) from t_user";
            Long total = queryRunner.query(countsql, new ScalarHandler<Long>());
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("total",total);
            result.put("rows",list);
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
	 * 用户列表，用于导出excel
	 */
	public List<UserDB> getUserList()
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String sql="select*from t_user order by account asc";
			List<UserDB> list = queryRunner.query(sql, new BeanListHandler<UserDB>(UserDB.class));
			conn.commit();
			return list;
		} catch (Exception e) {
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
	 * 添加用户
	 */
	public String addUser(UserDB userDB) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String[] sql = {"insert into students_account (sno,skey,sname) values (?,?,?)",
					"insert into teachers_account (tno,tkey,tname) values (?,?,?)",
					"insert into admin_account (ano,akey,aname) values (?,?,?)"};
			Object[] params = {userDB.getAccount(),userDB.getPassword(),userDB.getName()};
			queryRunner.update(sql[userDB.getRole()], params);
			conn.commit();
            return "success";
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
            return "fail";     
        }	
	}
	
	public void updateUser(UserDB userDB)
	{
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try {
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			String[] sql = {"update students_account set skey = ?, sname = ? where sno = ?",
					"update teachers_account set tkey = ?, tname = ? where tno = ?",
					"update admin_account set akey = ?, aname = ? where ano = ?"};
			Object[] params = {userDB.getPassword(), userDB.getName(),userDB.getAccount()};
			// 由于在前端是接收不到用户类型的数据，因此我这里还需要查询一下用户的role
			String select = "select role from t_user where account = ?";
			Object[] param = {userDB.getAccount()};
			UserDB temp = queryRunner.query(select, new BeanHandler<UserDB>(UserDB.class),param);
			Integer role = temp.getRole();
			queryRunner.update(sql[role], params);
			conn.commit();
			
		} catch (Exception e) {
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
	public String deleteUser(UserDB userDB) {
		Connection conn = null;
		conn = C3p0Tool.getConnection();
		try{
			conn.setAutoCommit(false);
			conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
			// 0学生，1教师，2管理员
			String[] sql = {"delete from students_account where sno = ?",
					"delete from teachers_account where tno = ?",
					"delete from admin_account where ano = ?"};
			Object[] params = {userDB.getAccount()};
			String select = "select role from t_user where account = ?";
			Object[] param = {userDB.getAccount()};
			UserDB temp = queryRunner.query(select, new BeanHandler<UserDB>(UserDB.class),param);
			Integer role = temp.getRole();
			queryRunner.update(sql[role], params);
			conn.commit();
            return "success";
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
            return "fail";     
        }
	}
}
