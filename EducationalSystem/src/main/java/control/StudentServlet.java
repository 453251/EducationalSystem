package control;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.sl.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
import service.StudentService;

/**
 * Servlet implementation class StudentServlet
 */
@WebServlet("/StudentServlet")
public class StudentServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private StudentService studentService = new StudentService();     

    public StudentServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if(method.equals("StudentPersonalView"))
			request.getRequestDispatcher("/WEB-INF/student/studentPersonal.jsp").forward(request, response);
		else if(method.equals("StudentScoreView"))
			request.getRequestDispatcher("/WEB-INF/student/studentScore.jsp").forward(request, response);
		else if(method.equals("CLassCourseListView"))
			request.getRequestDispatcher("/WEB-INF/student/classCourse.jsp").forward(request, response);
		else if(method.equals("exportExcel"))
			exportExcel(request,response);
		else if(method.equals("exportCourseList"))
			exportCourseList(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String method = request.getParameter("method");
		if("editPassword".equals(method)){ //修改密码
			String account = request.getParameter("sno");
			String password = request.getParameter("skey");
			studentService.editPassword(account,password);
			response.getWriter().write("success");
		}
		else if("termList".equals(method)){
			String result = studentService.termList();
			response.getWriter().write(result);
		}
		else if("courseList".equals(method)){
			response.setContentType("text/html;charset=UTF-8");
			HttpSession session = request.getSession();
			Student student= (Student) session.getAttribute("user");
			String clno = student.getClno();
			String termname = request.getParameter("grade");
			String result = studentService.getCourseList(clno,termname);
			System.out.println(result);
			response.getWriter().write(result);
		}
		else if("scoreList".equals(method)) {
			response.setContentType("text/html;charset=UTF-8");
			HttpSession session = request.getSession();
			Student student= (Student) session.getAttribute("user");
			String sno = student.getSno();
			String cdate = request.getParameter("grade");
			String cname = request.getParameter("clazz");
			String result = studentService.getScoreList(request,response,sno,cname,cdate);
			System.out.println(result);
			response.getWriter().write(result);
		}

	}
	
	/*
	 * 导出数据为excel
	 */
	public void exportExcel(HttpServletRequest request,HttpServletResponse response)
	{
		response.setContentType("text/html;charset=UTF-8");
		HttpSession session = request.getSession();
		Student student= (Student) session.getAttribute("user");
		String sno = student.getSno();
		String cdate = request.getParameter("grade");
		String cname = request.getParameter("cname");
		
		List<StudentCourseReport> scoreList = studentService.getScoreList(sno, cname, cdate);
		String sname = scoreList.get(0).getSname();	//姓名
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet("我的成绩");
			//设置列宽
			sheet.setColumnWidth(0, 10*256);
			sheet.setColumnWidth(1, 30*256);
			sheet.setColumnWidth(2, 20*256);
			sheet.setColumnWidth(3, 30*256);
			sheet.setColumnWidth(4, 10*256);
			sheet.setColumnWidth(5, 10*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("课程代码");		//课程代码
			firstRow.createCell(1).setCellValue("课程名");		//课程名
			firstRow.createCell(2).setCellValue("任课教师");		//任课教师
			firstRow.createCell(3).setCellValue("学期");			//学期
			firstRow.createCell(4).setCellValue("分数");			//分数
			firstRow.createCell(5).setCellValue("学分");			//学分
			
			int rowNumber = 1;
			for(StudentCourseReport studentCourseReport:scoreList)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(studentCourseReport.getCno());
				row.createCell(1).setCellValue(studentCourseReport.getCname());
				row.createCell(2).setCellValue(studentCourseReport.getTname());
				row.createCell(3).setCellValue(studentCourseReport.getCdate());
				row.createCell(4).setCellValue(studentCourseReport.getScore());
				row.createCell(5).setCellValue(studentCourseReport.getCcredit());
			}
			
			//创建文件对象
			String fileName = sname + "的成绩单.xlsx";
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
		Student student= (Student) session.getAttribute("user");
		String clno = student.getClno();
		String cdate = request.getParameter("grade");
		
		List<ClassCourse> list = studentService.getCourseListForExcel(clno, cdate);
		String sname = student.getSname();
		
		try (XSSFWorkbook excel = new XSSFWorkbook()) {
			XSSFSheet sheet = excel.createSheet(sname + "的课程表");
			//设置列宽
			sheet.setColumnWidth(0, 15*256);
			sheet.setColumnWidth(1, 30*256);
			sheet.setColumnWidth(2, 15*256);
			sheet.setColumnWidth(3, 30*256);
			sheet.setColumnWidth(4, 10*256);
			sheet.setColumnWidth(5, 10*256);
			sheet.setColumnWidth(6, 15*256);
			XSSFRow firstRow = sheet.createRow(0);	//表头创建一行
			
			
			//在表头行中创建列并设置值
			firstRow.createCell(0).setCellValue("课程代码");		//课程代码
			firstRow.createCell(1).setCellValue("课程名");		//课程名
			firstRow.createCell(2).setCellValue("任课教师");		//任课教师
			firstRow.createCell(3).setCellValue("学期");			//学期
			firstRow.createCell(4).setCellValue("学时");			//分数
			firstRow.createCell(5).setCellValue("学分");			//学分
			firstRow.createCell(6).setCellValue("考核形式");	
			
			int rowNumber = 1;
			for(ClassCourse classCourse:list)
			{
				XSSFRow row = sheet.createRow(rowNumber++);
				row.createCell(0).setCellValue(classCourse.getCno());
				row.createCell(1).setCellValue(classCourse.getCname());
				row.createCell(2).setCellValue(classCourse.getTname());
				row.createCell(3).setCellValue(classCourse.getCdate());
				row.createCell(4).setCellValue(classCourse.getChour());
				row.createCell(5).setCellValue(classCourse.getCcredit());
				row.createCell(6).setCellValue(classCourse.getCway());
			}
			
			//创建文件对象
			String fileName = sname + "的课程表.xlsx";
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
