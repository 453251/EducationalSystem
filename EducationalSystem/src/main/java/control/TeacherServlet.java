package control;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Enumeration;
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
import bean.ClassCourse;
import bean.Student;
import bean.StudentCourseReport;
import bean.Teacher;
import service.TeacherService;

/**
 * Servlet implementation class TeacherServlet
 */
@WebServlet("/TeacherServlet")
public class TeacherServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;  
	private TeacherService teacherService = new TeacherService();
	public TeacherServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if(method.equals("TeacherPersonalView"))
			request.getRequestDispatcher("/WEB-INF/teacher/teacherPersonal.jsp").forward(request, response);
		else if(method.equals("ScoreManageView"))
			request.getRequestDispatcher("/WEB-INF/teacher/teacherScoreManage.jsp").forward(request, response);
		else if(method.equals("CourseListView"))
			request.getRequestDispatcher("/WEB-INF/teacher/teacherCourse.jsp").forward(request, response);
		else if(method.equals("CourseScoreView"))
			request.getRequestDispatcher("/WEB-INF/teacher/courseScore.jsp").forward(request, response);
		else if(method.equals("exportCourseScore"))
			exportCourseScore(request,response);
		else if(method.equals("exportCourseList"))
			exportCourseList(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if("editPassword".equals(method)){ //修改密码
			String account = request.getParameter("account");
			String password = request.getParameter("password");
			teacherService.editPassword(account,password);
			response.getWriter().write("success");
		}
		else if("termList".equals(method)){
			String result = teacherService.termList();
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("courseList".equals(method)){
			response.setContentType("text/html;charset=UTF-8");
			HttpSession session = request.getSession();
			Teacher teacher= (Teacher) session.getAttribute("user");
			String tname = teacher.getTname();
			String result;
			String termname = request.getParameter("grade");
			result = teacherService.getCourseList(tname,termname);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("scoreList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			String cno= request.getParameter("cno");
			String result = teacherService.getScoreList(cno);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("studentList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			HttpSession session = request.getSession();
			Teacher teacher= (Teacher) session.getAttribute("user");
			String tno = teacher.getTno();
			String cdate = request.getParameter("grade");
			String cname = request.getParameter("clazz");
			String result = teacherService.getStudentList(request,response, tno,cname,cdate);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("editStudentScore".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");

	        String sno = request.getParameter("sno");
	        String cno = request.getParameter("cno");
	        String cdate = request.getParameter("cdate");
	        int score = Integer.parseInt(request.getParameter("score"));
	        double credit = Double.parseDouble(request.getParameter("ccredit"));
	        if(score>0 &&score<=100)
	        {
	        	teacherService.setStudentScore(sno,cno,(float)score);
		        String msg="success";
				response.getWriter().write(msg);        	
	        }
	        else
	        {
		        String msg="fali";
				response.getWriter().write(msg);
	        }
		}
	}
	
	public void exportCourseScore(HttpServletRequest request,HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		Teacher teacher = (Teacher) session.getAttribute("user");
		String tname = teacher.getTname();
		String cno = request.getParameter("cno");
		
		List<StudentCourseReport> list = teacherService.getScoreListForExcel(cno);
		String cname = list.get(0).getCname();	//课程名
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("课程成绩");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 30*256);
			sheet.setColumnWidth(2, 15*256);
			sheet.setColumnWidth(3, 15*256);
			sheet.setColumnWidth(4, 15*256);
			sheet.setColumnWidth(5, 30*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("课程代码");		//课程代码
			firstRow.createCell(1).setCellValue("课程名");		//课程名
			firstRow.createCell(2).setCellValue("学号");		//任课教师
			firstRow.createCell(3).setCellValue("姓名");			//学期
			firstRow.createCell(4).setCellValue("成绩");			//分数
			firstRow.createCell(5).setCellValue("学期");			//学分
			
			int rowNumber = 1;
			for(StudentCourseReport studentCourseReport:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(studentCourseReport.getCno());
				row.createCell(1).setCellValue(studentCourseReport.getCname());
				row.createCell(2).setCellValue(studentCourseReport.getSno());
				row.createCell(3).setCellValue(studentCourseReport.getSname());
				row.createCell(4).setCellValue(studentCourseReport.getScore());
				row.createCell(5).setCellValue(studentCourseReport.getCdate());
			}
			
			//创建文件对象
			String fileName = cname + "课程成绩.xlsx";
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
		Teacher teacher = (Teacher) session.getAttribute("user");
		String tname = teacher.getTname();
		String cdate = request.getParameter("grade");
		
		List<ClassCourse> list = teacherService.getCourseListForExcel(tname, cdate);
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet(cdate + "课程表");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 30*256);
			sheet.setColumnWidth(2, 15*256);
			sheet.setColumnWidth(3, 15*256);
			sheet.setColumnWidth(4, 30*256);
			sheet.setColumnWidth(5, 10*256);
			sheet.setColumnWidth(6, 10*256);
			sheet.setColumnWidth(7, 15*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("课程代码");		//课程代码
			firstRow.createCell(1).setCellValue("课程名");		//课程名
			firstRow.createCell(2).setCellValue("班级编号");		//任课教师
			firstRow.createCell(3).setCellValue("班级");			//学期
			firstRow.createCell(4).setCellValue("学期");			//分数
			firstRow.createCell(5).setCellValue("学时");			//学分
			firstRow.createCell(6).setCellValue("学分");
			firstRow.createCell(7).setCellValue("考核方式");
			
			int rowNumber = 1;
			for(ClassCourse classCourse:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(classCourse.getCno());
				row.createCell(1).setCellValue(classCourse.getCname());
				row.createCell(2).setCellValue(classCourse.getClno());
				row.createCell(3).setCellValue(classCourse.getClname());
				row.createCell(4).setCellValue(classCourse.getCdate());
				row.createCell(5).setCellValue(classCourse.getChour());
				row.createCell(6).setCellValue(classCourse.getCcredit());
				row.createCell(7).setCellValue(classCourse.getCway());
			}
			
			//创建文件对象
			String fileName = cdate + "课程表.xlsx";
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
