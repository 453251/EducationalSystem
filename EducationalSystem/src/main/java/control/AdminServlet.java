package control;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import bean.Course;
import bean.Stu;
import bean.Student;
import bean.StudentCourseReport;
import bean.StudentGPA;
import bean.Teacher;
import bean.UserDB;
import service.AdminService;
import service.UserService;

/**
 * Servlet implementation class AdminServlet
 */
@WebServlet("/AdminServlet")
public class AdminServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AdminService adminService = new AdminService();
    public AdminServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if(method.equals("ScoreListView"))
			request.getRequestDispatcher("/WEB-INF/admin/scoreList.jsp").forward(request, response);
		else if(method.equals("StudentListView"))
			request.getRequestDispatcher("/WEB-INF/admin/studentList.jsp").forward(request, response);
		else if(method.equals("TeacherListView"))
			request.getRequestDispatcher("/WEB-INF/admin/teacherList.jsp").forward(request, response);
		else if(method.equals("MajorListView"))
			request.getRequestDispatcher("/WEB-INF/admin/majorList.jsp").forward(request, response);
		else if(method.equals("ClassListView"))
			request.getRequestDispatcher("/WEB-INF/admin/classList.jsp").forward(request, response);
		else if(method.equals("CourseListView"))
			request.getRequestDispatcher("/WEB-INF/admin/courseList.jsp").forward(request, response);
		else if(method.equals("UserListView"))
			request.getRequestDispatcher("/WEB-INF/admin/userList.jsp").forward(request, response);
		else if(method.equals("exportScoreList"))
			exportScoreList(request,response);
		else if(method.equals("exportUserList"))
			exportUserList(request,response);
		else if(method.equals("exportStudentList"))
			exportStudentList(request, response);
		else if(method.equals("exportTeacherList"))
			exportTeacherList(request, response);
		else if(method.equals("exportCourseList"))
			exportCourseList(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		System.out.println("received method:"+method);
		if("scoreList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String cdate = request.getParameter("grade");
			String result = adminService.getScoreList(request,response,cdate);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("studentList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String result = adminService.getStudentList(request, response);
			response.getWriter().write(result);
		}
		else if("teacherList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String result = adminService.getTeacherList(request, response);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("courseList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String result = adminService.getCourseList(request, response);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("addStudent".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Stu student = new Stu();
			student.setSno(request.getParameter("sno"));
			student.setSname(request.getParameter("sname"));
			student.setSpassword("12345");
			student.setSsex(request.getParameter("ssex"));
			student.setSage(Integer.parseInt(request.getParameter("sage")));
			student.setSyear(Integer.parseInt(request.getParameter("syear")));
			student.setScredit(Integer.parseInt(request.getParameter("scredit")));
			student.setSaddress(request.getParameter("saddress"));
			student.setClno(request.getParameter("clno"));
			String msg = adminService.addStudent(student);
			response.getWriter().write(msg); 
		}
		else if("editStudent".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Stu student = new Stu();
			student.setSno(request.getParameter("sno"));
			student.setSname(request.getParameter("sname"));
			student.setSsex(request.getParameter("ssex"));
			student.setSage(Integer.parseInt(request.getParameter("sage")));
			student.setSyear(Integer.parseInt(request.getParameter("syear")));
			student.setScredit(Integer.parseInt(request.getParameter("scredit")));
			student.setSaddress(request.getParameter("saddress"));
			student.setClno(request.getParameter("clno"));
			String msg = adminService.editStudent(student);
			response.getWriter().write(msg);
		}
		else if("deleteStudent".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Stu student = new Stu();
			student.setSno(request.getParameter("sno"));
			String msg = adminService.deleteStudent(student);
			response.getWriter().write(msg);
		}
		else if("addTeacher".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Teacher teacher = new Teacher();
			teacher.setTno(request.getParameter("tno"));
			teacher.setTname(request.getParameter("tname"));
			teacher.setTpassword("123456");
			teacher.setTsex(request.getParameter("tsex"));
			teacher.setTage(Integer.parseInt(request.getParameter("tage")));
			teacher.setTposition(request.getParameter("tposition"));
			teacher.setTphone(request.getParameter("tphone"));
			String msg = adminService.addTeacher(teacher);
			response.getWriter().write(msg);
		}
		else if("editTeacher".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Teacher teacher = new Teacher();
			teacher.setTno(request.getParameter("tno"));
			teacher.setTname(request.getParameter("tname"));
			teacher.setTsex(request.getParameter("tsex"));
			teacher.setTage(Integer.parseInt(request.getParameter("tage")));
			teacher.setTposition(request.getParameter("tposition"));
			teacher.setTphone(request.getParameter("tphone"));
			String msg = adminService.editTeacher(teacher);
			response.getWriter().write(msg);
		}
		else if("deleteTeacher".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Teacher teacher = new Teacher();
			teacher.setTno(request.getParameter("tno"));
			System.out.println(teacher.getTno());
			String msg = adminService.deleteTeacher(teacher);
			response.getWriter().write(msg);
		}
		else if("addCourse".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Course course = new Course();
			course.setCno(request.getParameter("cno"));
			course.setCname(request.getParameter("cname"));
			course.setTname(request.getParameter("tname"));
			course.setChour(Integer.parseInt(request.getParameter("chour")));
			course.setCcredit(Integer.parseInt(request.getParameter("credit")));
			course.setCway(request.getParameter("cway"));
			course.setCdate(request.getParameter("cdate"));
			String msg = adminService.addCourse(course);
			response.getWriter().write(msg);
		}
		else if("editCourse".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Course course = new Course();
			course.setCno(request.getParameter("cno"));
			course.setCname(request.getParameter("cname"));
			course.setTname(request.getParameter("tname"));
			course.setChour(Integer.parseInt(request.getParameter("chour")));
			course.setCcredit(Integer.parseInt(request.getParameter("credit")));
			course.setCway(request.getParameter("cway"));
			course.setCdate(request.getParameter("cdate"));
			String msg = adminService.editCourse(course);
			response.getWriter().write(msg);
		}
		else if("deleteCourse".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			Course course = new Course();
			course.setCno(request.getParameter("courseid"));
			String msg = adminService.deleteCourse(course);
			response.getWriter().write(msg);
		}
		else if("userList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String result = adminService.getUserList(request, response);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("addUser".equals(method))
		{
			response.setContentType("text/html;charset=UTF-8");
			UserDB userDB = new UserDB();
			userDB.setAccount(request.getParameter("account"));
			userDB.setPassword(request.getParameter("password"));
			userDB.setName(request.getParameter("name"));
			userDB.setRole(Integer.parseInt(request.getParameter("role")));
			String msg = adminService.addUser(userDB);
			response.getWriter().write(msg);
		}
		else if("editUser".equals(method))
		{
			response.setContentType("text/html;charset=UTF-8");
			UserDB userDB = new UserDB();
			userDB.setAccount(request.getParameter("account"));
			userDB.setPassword(request.getParameter("password"));
			userDB.setName(request.getParameter("name"));
			userDB.setRole(Integer.parseInt(request.getParameter("role")));
			String msg = adminService.updateUser(userDB);
			response.getWriter().write(msg);
		}
		else if("deleteUser".equals(method))
		{
			response.setContentType("text/html;charset=UTF-8");
			UserDB userDB = new UserDB();
			userDB.setAccount(request.getParameter("account"));
			String msg = adminService.deleteUser(userDB);
			response.getWriter().write(msg);
		}
		else if("ChartOfScoreList".equals(method))
		{
			response.setContentType("text/html;charset=UTF-8");
			String cdate = request.getParameter("grade");
			String msg = adminService.getScoreListForCharts(cdate);
			System.out.println(msg);
			response.getWriter().write(msg);
		}
	}
	
	public void exportScoreList(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		UserDB admin = (UserDB) session.getAttribute("user");
		String cdate = request.getParameter("grade");
		
		List<StudentGPA> list = adminService.getScoreList(cdate);
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("学生绩点表");
			//设置列宽
			sheet.setColumnWidth(0, 10*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 10*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("学号");		//学号
			firstRow.createCell(1).setCellValue("姓名");		//姓名
			firstRow.createCell(2).setCellValue("绩点");		//绩点
			
			int rowNumber = 1;
			for(StudentGPA sgpa:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(sgpa.getSno());
				row.createCell(1).setCellValue(sgpa.getSname());
				row.createCell(2).setCellValue(sgpa.getGPA());
			}
			
			//创建文件对象
			String fileName = "学生绩点表.xlsx";
			response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
//			File file = new File(fileName);
//			
//			try(BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(file));) {
//				excel.write(bf);
//				fileName = URLEncoder.encode(fileName,"UTF-8");	//设置导出excel的文件名（防止中文乱码）
//				response.setContentType("application/vnd.ms-excel;chartset=utf-8");	//设置响应头，告诉浏览器返回的是excel文件
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);	//设置响应头，告诉浏览器返回的文件名
//				ServletOutputStream out = response.getOutputStream();	//创建一个文件输出流
//				excel.write(out);	//将excel表格以流的形式输出
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportUserList(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		UserDB admin = (UserDB) session.getAttribute("user");
		
		List<UserDB> list = adminService.getUserList();
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("用户列表");
			//设置列宽
			sheet.setColumnWidth(0, 10*256);
			sheet.setColumnWidth(1, 15*256);
			sheet.setColumnWidth(2, 15*256);
			sheet.setColumnWidth(3, 15*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("账号");		//账号
			firstRow.createCell(1).setCellValue("姓名");		//姓名
			firstRow.createCell(2).setCellValue("密码");		//密码
			firstRow.createCell(3).setCellValue("用户类别");			//用户类别	
			int rowNumber = 1;
			for(UserDB userDB:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(userDB.getAccount());
				row.createCell(1).setCellValue(userDB.getName());
				row.createCell(2).setCellValue(userDB.getPassword());
				row.createCell(3).setCellValue(userDB.getFlag()[userDB.getRole()]);
			}
			
			//创建文件对象
			String fileName = "用户列表.xlsx";
			response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
//			File file = new File(fileName);
//			
//			try(BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(file));) {
//				excel.write(bf);
//				fileName = URLEncoder.encode(fileName,"UTF-8");	//设置导出excel的文件名（防止中文乱码）
//				response.setContentType("application/vnd.ms-excel;chartset=utf-8");	//设置响应头，告诉浏览器返回的是excel文件
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);	//设置响应头，告诉浏览器返回的文件名
//				ServletOutputStream out = response.getOutputStream();	//创建一个文件输出流
//				excel.write(out);	//将excel表格以流的形式输出
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportStudentList(HttpServletRequest request,HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		UserDB admin = (UserDB) session.getAttribute("user");
		
		List<Student> list = adminService.getStudentList();
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("学生信息列表");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 10*256);
			sheet.setColumnWidth(2, 10*256);
			sheet.setColumnWidth(3, 10*256);
			sheet.setColumnWidth(4, 15*256);
			sheet.setColumnWidth(5, 10*256);
			sheet.setColumnWidth(6, 30*256);
			sheet.setColumnWidth(7, 15*256);
			sheet.setColumnWidth(8, 20*256);
			sheet.setColumnWidth(9, 15*256);
			sheet.setColumnWidth(10, 20*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("学号");		//学号
			firstRow.createCell(1).setCellValue("姓名");		//姓名
			firstRow.createCell(2).setCellValue("性别");		//性别
			firstRow.createCell(3).setCellValue("年龄");		//年龄
			firstRow.createCell(4).setCellValue("入学年份");	
			firstRow.createCell(5).setCellValue("学分");	
			firstRow.createCell(6).setCellValue("地址");	
			firstRow.createCell(7).setCellValue("班级编号");	
			firstRow.createCell(8).setCellValue("班级");	
			firstRow.createCell(9).setCellValue("专业编号");	
			firstRow.createCell(10).setCellValue("专业");	
			int rowNumber = 1;
			for(Student student:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(student.getSno());
				row.createCell(1).setCellValue(student.getSname());
				row.createCell(2).setCellValue(student.getSsex());
				row.createCell(3).setCellValue(student.getSage());
				row.createCell(4).setCellValue(student.getSyear());
				row.createCell(5).setCellValue(student.getScredit());
				row.createCell(6).setCellValue(student.getSaddress());
				row.createCell(7).setCellValue(student.getClno());
				row.createCell(8).setCellValue(student.getClname());
				row.createCell(9).setCellValue(student.getMno());
				row.createCell(10).setCellValue(student.getMname());
			}
			
			//创建文件对象
			String fileName = "学生信息列表.xlsx";
			response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
//			File file = new File(fileName);
//			
//			try(BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(file));) {
//				excel.write(bf);
//				fileName = URLEncoder.encode(fileName,"UTF-8");	//设置导出excel的文件名（防止中文乱码）
//				response.setContentType("application/vnd.ms-excel;chartset=utf-8");	//设置响应头，告诉浏览器返回的是excel文件
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);	//设置响应头，告诉浏览器返回的文件名
//				ServletOutputStream out = response.getOutputStream();	//创建一个文件输出流
//				excel.write(out);	//将excel表格以流的形式输出
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportTeacherList(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		UserDB admin = (UserDB) session.getAttribute("user");
		
		List<Teacher> list = adminService.getTeacherList();
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("教师信息列表");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 10*256);
			sheet.setColumnWidth(2, 10*256);
			sheet.setColumnWidth(3, 10*256);
			sheet.setColumnWidth(4, 15*256);
			sheet.setColumnWidth(5, 20*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("工号");		//学号
			firstRow.createCell(1).setCellValue("姓名");		//姓名
			firstRow.createCell(2).setCellValue("性别");		//性别
			firstRow.createCell(3).setCellValue("年龄");		//年龄
			firstRow.createCell(4).setCellValue("职位");	
			firstRow.createCell(5).setCellValue("联系电话");	
			int rowNumber = 1;
			for(Teacher teacher:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(teacher.getTno());
				row.createCell(1).setCellValue(teacher.getTname());
				row.createCell(2).setCellValue(teacher.getTsex());
				row.createCell(3).setCellValue(teacher.getTage());
				row.createCell(4).setCellValue(teacher.getTposition());
				row.createCell(5).setCellValue(teacher.getTphone());
			}
			
			//创建文件对象
			String fileName = "教师信息列表.xlsx";
			response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
//			File file = new File(fileName);
//			
//			try(BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(file));) {
//				excel.write(bf);
//				fileName = URLEncoder.encode(fileName,"UTF-8");	//设置导出excel的文件名（防止中文乱码）
//				response.setContentType("application/vnd.ms-excel;chartset=utf-8");	//设置响应头，告诉浏览器返回的是excel文件
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);	//设置响应头，告诉浏览器返回的文件名
//				ServletOutputStream out = response.getOutputStream();	//创建一个文件输出流
//				excel.write(out);	//将excel表格以流的形式输出
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void exportCourseList(HttpServletRequest request, HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		UserDB admin = (UserDB) session.getAttribute("user");
		
		List<Course> list = adminService.getCourseList();
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("课程信息列表");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 30*256);
			sheet.setColumnWidth(2, 15*256);
			sheet.setColumnWidth(3, 10*256);
			sheet.setColumnWidth(4, 10*256);
			sheet.setColumnWidth(5, 15*256);
			sheet.setColumnWidth(6, 30*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("课程代码");		//学号
			firstRow.createCell(1).setCellValue("课程名");		//姓名
			firstRow.createCell(2).setCellValue("任课教师");		//性别
			firstRow.createCell(3).setCellValue("学时");		//年龄
			firstRow.createCell(4).setCellValue("学分");	
			firstRow.createCell(5).setCellValue("考核方式");	
			firstRow.createCell(6).setCellValue("学期");
			int rowNumber = 1;
			for(Course course:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(course.getCno());
				row.createCell(1).setCellValue(course.getCname());
				row.createCell(2).setCellValue(course.getTname());
				row.createCell(3).setCellValue(course.getChour());
				row.createCell(4).setCellValue(course.getCcredit());
				row.createCell(5).setCellValue(course.getCway());
				row.createCell(6).setCellValue(course.getCdate());
			}
			
			//创建文件对象
			String fileName = "课程信息列表.xlsx";
			response.setContentType("application/vnd.ms-excel");
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream out = response.getOutputStream();
            excel.write(out);
//			File file = new File(fileName);
//			
//			try(BufferedOutputStream bf = new BufferedOutputStream(new FileOutputStream(file));) {
//				excel.write(bf);
//				fileName = URLEncoder.encode(fileName,"UTF-8");	//设置导出excel的文件名（防止中文乱码）
//				response.setContentType("application/vnd.ms-excel;chartset=utf-8");	//设置响应头，告诉浏览器返回的是excel文件
//				response.setHeader("Content-Disposition", "attachment;filename=" + fileName);	//设置响应头，告诉浏览器返回的文件名
//				ServletOutputStream out = response.getOutputStream();	//创建一个文件输出流
//				excel.write(out);	//将excel表格以流的形式输出
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
