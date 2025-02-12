package service;


import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;

import bean.Course;
import bean.Stu;
import bean.Student;
import bean.StudentCourseReport;
import bean.StudentGPA;
import bean.Teacher;
import bean.UserDB;
import dao.AdminDao;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.sf.json.JSONArray;
import util.PageTool;

public class AdminService {
	private AdminDao adminDao = new AdminDao();
	/*
	 * 绩点表，分页
	 */
	public String getScoreList(HttpServletRequest request,HttpServletResponse response,String cdate) {
		DecimalFormat df = new DecimalFormat("#.00");  
		Map<String, Object> temp = adminDao.getScoreList(request, response,cdate);
		List<StudentGPA> list = (List<StudentGPA>) temp.get("rows");
		for(StudentGPA sgpa: list)
		{
			sgpa.setGPA(sgpa.getTotalScore() / sgpa.getTotalCredits() / 20);
			sgpa.setGPA(Double.parseDouble(df.format(sgpa.getGPA())));
		}
		temp.put("rows", list);
        //以json格式返回数据
		String result = new Gson().toJson(temp);
		return result;
	}
	/*
	 * 绩点表，用于导出excel
	 */
	public List<StudentGPA> getScoreList(String cdate)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		List<StudentGPA> list = adminDao.getScoreList(cdate);
		for(StudentGPA sgpa:list)
		{
			sgpa.setGPA(sgpa.getTotalScore() / sgpa.getTotalCredits() / 20);
			sgpa.setGPA(Double.parseDouble(df.format(sgpa.getGPA())));
		}
		return list;
	}
	public String getScoreListForCharts(String cdate)
	{
		DecimalFormat df = new DecimalFormat("#.00");
		List<StudentGPA> list = adminDao.getScoreList(cdate);
		for(StudentGPA sgpa:list)
		{
			sgpa.setGPA(sgpa.getTotalScore() / sgpa.getTotalCredits() / 20);
			sgpa.setGPA(Double.parseDouble(df.format(sgpa.getGPA())));
		}
		System.out.println(list);
		String result = JSONArray.fromObject(list).toString();
		return result;
	}
	/*
	 * 学生列表，前端分页
	 */
	public String getStudentList(HttpServletRequest request, HttpServletResponse response) {
		Map<String,Object> list = adminDao.getStudentList(request, response);
        //以json格式返回数据
        String result = new Gson().toJson(list);
		return result;
	}
	/*
	 * 学生列表，用于导出excel
	 */
	public List<Student> getStudentList()
	{
		return adminDao.getStudentList();
	}
	/*
	 * 学生列表，后端分页
	 */
	public PageTool<Student> getStudentList(String currentPage, String pageSize)
	{
		return adminDao.getStudentList(currentPage, pageSize);
	}
	/*
	 * 添加学生
	 */
	public String addStudent(Stu student) {	
		return adminDao.addStudent(student);
	}	
	/*
	 * 修改学生
	 */
	public String editStudent(Stu student) {
		try {
			String sno = student.getSno();		
			adminDao.setStudentName(sno,student.getSname());
			adminDao.setStudentSex(sno,student.getSsex());
			adminDao.setStudentAge(sno,student.getSage());
			adminDao.setStudentYear(sno,student.getSyear());
			adminDao.setStudentAddress(sno,student.getSaddress());
			adminDao.setStudentCredit(sno,student.getScredit());
			adminDao.setStudentClno(sno,student.getClno());
			return "success";
		}
        catch(Exception e) {
        	e.printStackTrace();
        	return "fail";
		}

	}
	/*
	 * 删除学生
	 */
	public String deleteStudent(Stu student) {
		return adminDao.deleteStudent(student);
	}
	/*
	 * 添加教师
	 */
	public String addTeacher(Teacher teacher) {
		return adminDao.addTeacher(teacher);
	}
	/*
	 * 修改教师
	 */
	public String editTeacher(Teacher teacher) {
		try {
			String tno = teacher.getTno();
			adminDao.setTeacherName(tno,teacher.getTname());
			adminDao.setTeacherSex(tno,teacher.getTsex());
			adminDao.setTeacherAge(tno,teacher.getTage());
			adminDao.setTeacherPosition(tno,teacher.getTposition());
			adminDao.setTeacherPhone(tno,teacher.getTphone());
			return "success";
		}
        catch(Exception e) {
        	e.printStackTrace();
        	return "fail";
		}
	}
	/*
	 * 删除教师
	 */
	public String deleteTeacher(Teacher teacher) {
		return adminDao.deleteTeacher(teacher);
	}
	/*
	 * 教师列表，分页
	 */
	public String getTeacherList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> list = adminDao.getTeacherList(request,response);
        //以json格式返回数据
        String result = new Gson().toJson(list);
		return result;
	}
	/*
	 * 教师列表，用于导出excel
	 */
	public List<Teacher> getTeacherList()
	{
		return adminDao.getTeacherList();
	}
	/*
	 * 修改课程
	 */
	public String editCourse(Course course) {
		try {
			String cno = course.getCno();
			adminDao.setCourseName(cno,course.getCname());
			adminDao.setCourseTname(cno, course.getTname());
			adminDao.setCourseTime(cno,course.getChour());
			adminDao.setCourseCredit(cno,course.getCcredit());
			adminDao.setCourseTest(cno,course.getCway());
			adminDao.setCourseTerm(cno,course.getCdate());
			return "success";
		}
        catch(Exception e) {
        	e.printStackTrace();
        	return "fail";
		}
	}
	/*
	 * 课程列表，分页
	 */
	public String getCourseList(HttpServletRequest request, HttpServletResponse response) {
		Map<String, Object> list = adminDao.getCourseList(request,response);
        //以json格式返回数据
        String result = new Gson().toJson(list);
		return result;
	}
	/*
	 * 课程列表，用于导出excel
	 */
	public List<Course> getCourseList()
	{
		return adminDao.getCourseList();
	}
	/*
	 * 添加课程
	 */
	public String addCourse(Course course) {
		return adminDao.addCourse(course);
	}
	/*
	 * 删除课程
	 */
	public String deleteCourse(Course course) {
		return adminDao.deleteCourse(course);
	}
	/*
	 * 用户列表，分页
	 */
	public String getUserList(HttpServletRequest request, HttpServletResponse response)
	{
		Map<String, Object> list = adminDao.getUserList(request, response);
		//以json格式返回数据
		String result = new Gson().toJson(list);
		return result;
	}
	
	/*
	 * 用户列表，用于导出excel
	 */
	public List<UserDB> getUserList()
	{
		List<UserDB> list = adminDao.getUserList();
		return list;
	}
	/*
	 * 添加用户
	 */
	public String addUser(UserDB userDB)
	{
		return adminDao.addUser(userDB);
	}
	/*
	 * 修改用户
	 */
	public String updateUser(UserDB userDB)
	{
		try {
			adminDao.updateUser(userDB);
			return "success";
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "fail";
	}
	/*
	 * 删除用户
	 */
	public String deleteUser(UserDB userDB)
	{
		return adminDao.deleteUser(userDB);
	}
}
