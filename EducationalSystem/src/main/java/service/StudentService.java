package service;


import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import bean.ClassCourse;
import bean.Semester;
import bean.Student;
import bean.StudentCourseReport;
import dao.StudentDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;

public class StudentService {
	private StudentDao studentDao = new StudentDao();
	public void editPassword(String sno,String skey) {
		studentDao.update(sno,skey);
	}

	public String getStudent(String sno)
	{
		List<Student> list = studentDao.getStudent(sno);
		//以json格式返回数据
		String result = JSONArray.fromObject(list).toString();
		return result;
	}
	public String termList() {
		List<Semester> list = studentDao.getTermname();		
		//以json格式返回数据
        String result = JSONArray.fromObject(list).toString();	
		return result;
	}

	/*
	 * 成绩查询，分页
	 */
	public String getScoreList(HttpServletRequest request,HttpServletResponse response,String sno,String cname, String cdate) {
		Map<String, Object> list = studentDao.getScoreList(request, response, sno, cname, cdate);
        //以json格式返回数据
        String result = new Gson().toJson(list);
		return result;
	}
	/*
	 * 课程表，学期查询
	 */
	public String getCourseList(String clno,String termname) {
		List<ClassCourse> list = studentDao.getCourseList(clno,termname);
        //以json格式返回数据
        String result = JSONArray.fromObject(list).toString();

		return result;
	}
	/*
	 * 课程表，用于导出为excel
	 */
	public List<ClassCourse> getCourseListForExcel(String clno, String termname)
	{
		return studentDao.getCourseList(clno, termname);
	}
	
	/*
	 * 成绩查询，用于导出为excel
	 */
	public List<StudentCourseReport> getScoreList(String sno, String cname, String cdate)
	{
		List<StudentCourseReport> list = studentDao.getScoreList(sno, cname, cdate);
		System.out.println(list);
		return list;
	}
}
