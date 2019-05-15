<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
<% String code = request.getParameter("code"); %>

<!DOCTYPE html>
<html>
    <head>
      <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="author" content="Weaver E-Mobile Dev Group" />
        <meta name="description" content="Weaver E-mobile" />
        <meta name="keywords" content="weaver,e-mobile" />
        <meta name="viewport" content="width=device-width,minimum-scale=1.0, maximum-scale=1.0" />
        <title>建表赢奖励</title>
        <link rel="stylesheet" href="../css/trav.css"/>
        <link rel="stylesheet" href="../css/bootstrap.min.css" type="text/css" />
        <script src="../js/jquery-1.11.3.min.js" type="text/javascript"></script>
        <script src="../js/bootstrap.min.js" type="text/javascript"></script>
        <link rel="stylesheet" href="../css/weui.min.css" />
        <link rel="stylesheet" href="../css/jquery-weui.min.css" />
        <link rel="stylesheet" href="../css/icon.css" />
        <link rel="stylesheet" href="../css/task.css" />
        <link rel="stylesheet" href="../css/bootstrapValidator.min.css" />
        <link rel="stylesheet" href="../css/aui_css/aui.css" />
        <link rel="stylesheet" type="text/css" href="../css/aui_css/aui-pull-refresh.css" />
        <link rel="stylesheet" href="../css/bootstrap-select.min.css" />
        <script type='text/javascript' src='../js/jquery.textarea.autoheight.js'></script>
        <script type='text/javascript' src='../js/jquery.form.js'></script>
        <script type='text/javascript' src="../js/jquery-weui.js"></script>
        <script type='text/javascript' src='../js/fastclick.min.js'></script>
        <script type='text/javascript' src='../js/web3.min.js'></script>
        <script type='text/javascript' src='../js/bignumber.js'></script>
        <script type='text/javascript' src='../js/bootstrapValidator.min.js'></script>
        <script type='text/javascript' src='../js/bootstrap-select.min.js'></script>
        <script type="text/javascript" src="../js/aui_script/api.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-tab.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-pull-refresh.js"></script>
        <script type="text/javascript" src="../js/aui_script/aui-toast.js" ></script>
        <script type="text/javascript">
        window.commit = function(){
        	var screen_name = $("#screen_name").val();
        	alert(screen_name);
        	var code = '<%=code%>';
        	$.ajax({
        		type:"GET",
        		url:"/weibo/code",
        		data:{
        			"code":code,
        			"screen_name":screen_name
        		},
        		dataType:"json",
        		success:function(data){
        				alert(data.success);
        		}
        	});
        }
        </script>
    </head>
    <body style="font-family:微软雅黑;" >
    	  <div class="aui-content aui-margin-b-15">
    	  <h1 align="center">添加数据</h1>
    	   <ul class="aui-list aui-form-list" id = "form">
    	
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    微博名
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="微博名" id="screen_name">
                </div>
            </div>
        </li>
        </ul>
	<div class="aui-content aui-margin-b-15">
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="commit()">提交</div>
     </div>
</div> 
    </body>
    
</html>