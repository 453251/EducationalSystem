package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import bean.ClassCourse;
import bean.Semester;
import bean.StudentCourseReport;
import dao.TeacherDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;

public class TeacherService {
	private TeacherDao teacherDao = new TeacherDao();
	public void editPassword(String account,String password) {
		teacherDao.update(account,password);
	}
	public String termList() {
		List<Semester> list = teacherDao.getTermname();		
		//格式化以json格式返回数据
        String result = JSONArray.fromObject(list).toString();	
		return result;
	}
	/*
	 * 教师课表，按学期查询
	 */
	public String getCourseList(String tname, String termname) {
		List<ClassCourse> list = teacherDao.getCourseList(tname,termname);
        //格式化以json格式返回数据
        String result = JSONArray.fromObject(list).toString();
		return result;
	}
	/*
	 * 教师课表，用于导出为excel
	 */
	public List<ClassCourse> getCourseListForExcel(String tname, String termname)
	{
		return teacherDao.getCourseList(tname, termname);
	}
	/*
	 * 教师教的某门课的成绩表
	 */
	public String getScoreList(String cno) {
		List<StudentCourseReport> list = teacherDao.getScoreList(cno);
        //格式化以json格式返回数据
        String result = JSONArray.fromObject(list).toString();
		return result;
	}
	/*
	 * 教师的某门课的成绩表，用于导出为excel
	 */
	public List<StudentCourseReport> getScoreListForExcel(String cno)
	{
		return teacherDao.getScoreList(cno);
	}
	/*
	 * 教师登分表
	 */
	public String getStudentList(HttpServletRequest request, HttpServletResponse response, String tno, String cname, String cdate) {
		Map<String, Object> list = teacherDao.getStudentList(request, response, tno,cname,cdate);
        //格式化json格式返回数据
        String result = new Gson().toJson(list);
		return result;
	}
	public void setStudentScore(String sno, String cno,float report) {
		teacherDao.setStudentScore(sno,cno, report);
	}
//	public void setStudentCredit(String sno, double credit) {
//		teacherDao.setStudentCredit(sno,credit);		
//	}
}
