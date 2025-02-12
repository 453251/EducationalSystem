<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>课程列表</title>
	<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="easyui/css/demo.css">
	<script type="text/javascript" src="easyui/jquery.min.js"></script>
	<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="easyui/js/validateExtends.js"></script>
	<script type="text/javascript">
	$(function() {	
		//datagrid初始化 
	    $('#dataList').datagrid({ 
	        title:'课程列表', 
	        iconCls:'icon-more',//图标 
	        border: true, 
	        collapsible:false,//是否可折叠的 
	        fit: true,//自动大小 
	        method: "post",
	        url:"TeacherServlet?method=courseList",
	        idField:'cno', 
	        singleSelect:false,//是否单选 
	        pagination:false,//分页控件 
	        rownumbers:true,//行号 
	        sortOrder:'Asc', 
	        remoteSort: false,
	        queryParams:{grade:$("#gradeList").combobox("getValue")},
	        columns: [[  
				{field:'chk',checkbox: true,width:50},
 		        {field:'cno', title:'课程代码',width:100, sortable:true},
 		        {field:'cname',title:'课程名',width:150},
 		        {field:'clno',title:'班级编号',width:100, sortable:true},
 		        {field:'clname',title:'班级',width:100},
 		        {field:'cdate',title:'学期',width:200,sortable: true},
 		        {field:'chour',title:'学时',width:150},    
 		        {field:'ccredit',title:'学分',width:150},
 		        {field:'cway',title:'考核方式',width:100}
	 		]], 
	        toolbar: "#toolbar"
	    }); 
	    
	    
	    //查询按钮单击事件，利用学期查询和利用课程的模糊查询
	    $("#search").click(function(){
	    	$("#dataList").datagrid('load',{
	    		grade:$("#gradeList").combobox("getValue")
	    	});
	    });
	  	
	  //导出按钮事件
	    $("#export").click(function(){
	    	var grade = $("#gradeList").combobox("getValue");
	    	var url = "TeacherServlet?method=exportCourseList&grade="+ encodeURIComponent(grade);
	    	window.location.href = url;
	    });
	  
	});  	
	</script>
	<style>
		#toolbar {
			overflow: hidden;
			padding: 5px;
		}
		#toolbar div {
			float: left;
			margin-right: 10px;
		}
	</style>
</head>
<body>
	<!-- 学生列表 -->
	<table id="dataList" cellspacing="0" cellpadding="0"> 
	    
	</table> 
	<!-- 工具栏 -->
	<div id="toolbar">
 		<div style="float: left; margin: 0 10px 0 10px">学期：<select id="gradeList" class="easyui-combobox" name="grade" >
	 			<option value="">全部</option>
	 			<option value="2022-2023学年第1学期">2022-2023学年第1学期</option>
	 			<option value="2022-2023学年第2学期">2022-2023学年第2学期</option>
	 			<option value="2023-2024学年第1学期">2023-2024学年第1学期</option>
	 			<option value="2023-2024学年第2学期">2023-2024学年第2学期</option>
	 			<option value="2024-2025学年第1学期">2024-2025学年第1学期</option>
	 			<option value="2024-2025学年第2学期">2024-2025学年第2学期</option>
	 			<option value="2025-2026学年第1学期">2025-2026学年第1学期</option>
	 			<option value="2025-2026学年第2学期">2025-2026学年第2学期</option>
 			</select>
 		</div>
  		<div style="float: left;"><a id="search" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-search',plain:true">查询</a></div> 
		<div style="float: right;"><a id="export" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-print',plain:true">导出数据</a></div>
	</div>	
</body>
</html>