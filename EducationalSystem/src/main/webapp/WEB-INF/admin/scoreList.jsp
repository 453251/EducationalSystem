<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta charset="UTF-8">
	<title>学生列表</title>
	<link rel="stylesheet" type="text/css" href="easyui/themes/default/easyui.css">
	<link rel="stylesheet" type="text/css" href="easyui/themes/icon.css">
	<link rel="stylesheet" type="text/css" href="easyui/css/demo.css">
	<script type="text/javascript" src="easyui/jquery.min.js"></script>
	<script type="text/javascript" src="easyui/jquery.easyui.min.js"></script>
	<script type="text/javascript" src="easyui/js/validateExtends.js"></script>
	<script src="echarts.min.js"></script>
	<script type="text/javascript">
	$(function() {	
		//datagrid初始化 
	    $('#dataList').datagrid({ 
	        title:'学生列表', 
	        iconCls:'icon-more',//图标 
	        border: true, 
	        collapsible:false,//是否可折叠的 
	        fit: true,//自动大小 
	        method: "post",
	        url:"AdminServlet?method=scoreList",
	        idField:'cno', 
	        singleSelect:false,//是否单选 
	        pagination:true,//分页控件 
	        rownumbers:true,//行号 
	        sortOrder:'Asc', 
	        remoteSort: false,
	        queryParams:{grade:$("#gradeList").combobox("getValue")},
	        columns: [[  
				{field:'chk',checkbox: true,width:50},
				{field:'sno', title:'学号',width:100, sortable:true},
				{field:'sname', title:'姓名', width:100},
				{field:'GPA',title:'绩点',width:100,sortable:true}
	 		]], 
	        toolbar: "#toolbar"
	    }); 
	    //设置分页控件 
	    var p = $('#dataList').datagrid('getPager'); 
	    $(p).pagination({ 
	        pageSize: 10,//每页显示的记录条数，默认为10 
	        pageList: [10,20,30,50,100],//可以设置每页记录条数的列表 
	        beforePageText: '第',//页数文本框前显示的汉字 
	        afterPageText: '页    共 {pages} 页', 
	        displayMsg: '当前显示 {from} - {to} 条记录   共 {total} 条记录', 
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
	    	var url = "AdminServlet?method=exportScoreList&grade="+ encodeURIComponent(grade);
	    	window.location.href = url;
	    });
	  
	    function generateChart() {
		    var grade = $("#gradeList").combobox("getValue");
		    console.log("Sending AJAX request with grade: " + grade); // 添加日志
		    $.ajax({
		        url:"AdminServlet?method=ChartOfScoreList",
		        type: "post",
		        data: { grade: grade },
		        success: function(response) {
		        	console.log("Received response: " + response);
		            var data = JSON.parse(response);
		            var names = [];
		            var scores = [];
		            for(var i = 0; i<data.length; i++)
		            	{
		            		scores.push({ value: data[i].GPA, name: data[i].sname });
		            	}
		            console.log(names);
		            
		            var chartDom = document.getElementById('myChart');
		            if (!chartDom) {
		            	console.error('Element #myChart not found');
		            	return;
		            }
		            var myChart = echarts.init(chartDom);
		            console.log(myChart);
		            myChart.showLoading();
		            var option = {
			                title: {
			                    text: '学生GPA图表',
			                    left: 'center'
			                },
			                tooltip: {
			                    trigger: 'item'
			                },
			                legend: {
			                    orient: 'vertical',
			                    left: 'left',
			                    show:false
			                },
			                series: [
			                    {
			                        name: 'GPA',
			                        type: 'pie',
			                        radius: '50%',
			                        data: scores,
			                        emphasis: {
			                            itemStyle: {
			                                shadowBlur: 10,
			                                shadowOffsetX: 0,
			                                shadowColor: 'rgba(0, 0, 0, 0.5)'
			                            }
			                        },
			                        label: {
			                            formatter: '{b}: {c} ({d}%)',
			                            position: 'outside'
			                        },
			                        labelLine: {
			                            length: 10,
			                            length2: 20
			                        },
			                        
			                    }
			                ]
			            };
		            console.log(option);
		            myChart.setOption(option);
		            myChart.hideLoading();
		            console.log(myChart);
		        },
		            
			     error: function() 
			     	{
			           alert("获取数据失败！");
			        }
			    });
		    }
		            
		            
	 // 新增生成图表按钮点击事件
	    $("#generateChart").click(function() {
	    	$('#chartDialog').dialog('open');
           generateChart();
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
		<div style="float: right; margin-right: 10px;">
    		<a id="generateChart" href="javascript:;" class="easyui-linkbutton" data-options="iconCls:'icon-chart_bar',plain:true">生成图表</a>
		</div>
	</div>
	
	<!-- 图表显示区域 -->
	<div id="chartDialog" class="easyui-dialog" title="学生GPA图表" style="width:650px;height:450px;padding:10px" 
		data-options="closed:true, modal:true">
	    <div id="myChart" style="width:600px;height:400px;"></div>
	</div>
</body>
</html>