<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%@ page import="weaver.conn.RecordSet"%>
<%@ page import="weaver.general.BaseBean"%>
<%@ page import="weaver.file.FileUpload"%>
<%@ page import="weaver.hrm.*" %>
<%@ page import="weaver.general.*" %>
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
        <script type='text/javascript' src='../js/check_login.js'></script>
		<script type="text/javascript" src='../js/cookie_util.js'></script>
        <script type="text/javascript">
        var itcode;
        var baseUrl = '/';
        $(function() {
        	$("#list").show(); 
        	itcode = getCookie("itcode");
            apiready = function(){
                api.parseTapmode();
            }

            var tab = new auiTab({
                element:document.getElementById("footer")
            },function(ret){
            	if(ret.index == 1) {
                    $("#introduce").hide();
                     $("#create").hide();
                     $("#list").show(); 
                 }
                 if(ret.index == 2) {
                     $("#list").hide();
                     $("#introduce").hide();
                     $("#create").show();
                 }
                 if(ret.index == 3) {
                     $("#list").hide();
                     $("#create").hide();
                     $("#introduce").show();
                 }
            });
            
            //查找当前用户建的所有表
            $.ajax({
            	type:"GET",
            	data:{"itcode":itcode},
            	dataType:"json",
            	url:baseUrl+"table/getTableList",
            	success:function(data){
            		var list = data.list;
            		var tableName = "";
            		var listHtml = "";
            		for(var i = 0;i<list.length;i++){
            			tableName = list[i].table_name;
            			listHtml +="<li class='aui-list-item' onclick='addData(\""+tableName+"\")'><div class='aui-list-item-inner'><div class='aui-list-item-label'>表名</div><div class='aui-list-item-input' >"+tableName+"</div></div></li>";
            			if(i == list.length-1){
            				listHtml += "<li class='aui-list-item' onclick='addData(\""+tableName+"\")'><div class='aui-list-item-inner'><div class='aui-list-item-label'>表名</div><div class='aui-list-item-input' >"+tableName+"</div></div></li>";
            			}
            		}
            		console.log(listHtml);
            		$("#form2").append(listHtml);
            	}
            });
            
        })
        //点击表名跳转至添加数据页面
        function addData(tableName){
				window.location.href=baseUrl+"table/addData.jsp?tableName="+tableName;
        }
        //点击建表页面的添加字段
        window.addField = function() {
	        var i = $("#form li:last").children().children().html();
        	var num= Number(i.replace(/[^0-9]/ig,""))+1;
        	//alert(parseInt(num));
        	$("#form").append("<li class='aui-list-item'><div class='aui-list-item-inner'><div class='aui-list-item-label'>字段"+num+"</div><div class='aui-list-item-input'><input type='text' placeholder='名' id='field"+num+"'></div><div class='aui-list-item-label'>类型</div><div class='aui-list-item-input'><select id='type"+num+"'><option value = 'int'>int</option><option value = 'varchar(255)'>varchar(255)</option><option value = 'varchar(20)'>varchar(20)</option><option value='double'>double</option></select></div></li>");
        }
        //点击建表页面的提交按钮提交数据
        window.commit = function(){
	            var li = $("#form li:last").children().children().html();
            	var num = Number(li.replace(/[^0-9]/ig,""));
            	var jsonStr = "";
            	var Str; 
            	var StrType;
            	for(var i = 1;i<=num; i++){
            		Str = "#field";
            		Str += i;
            		StrType = "#type";
            		StrType += i;
            		//alert($(StrType).val());
            		jsonStr += '"'+Str+'"'+':'+'"'+$(Str).val()+'"'+','+'"'+ StrType+'"'+':'+'"'+$(StrType).val()+'"'+',';
            	}
            	var tableName = $("#tableName").val();
            	var jsonStr1 = '{'+jsonStr+'"tableName":"'+tableName+'","itcode":"'+itcode+'"}';
            	var jsonStr2 = jsonStr1.replace(/\s/g,'');
            	jsonStr2 = jsonStr1.replace(/#/g,'');
        		showDefault("loading");
            	$.ajax({
                type: "GET",
	            url: baseUrl + "table/createTable",
	            data: {"param":jsonStr2},
	            dataType: "json",
	            success: function(data) {
	            	$("#create").hide();
	            	$("#list").show();
	           		if(data.success){
		        		alert(data.msg);
		        		window.location.href = "/table/tableList.jsp";
	           			}else{
	            		alert("建表失败，请检查表名，字段名是否符合要求（表名字段名不能以数字开头，不能过长）");
	            		window.location.reload();
	             		}
	             	}
            	});
            }
        window.upload = function(){
        	showDefault("loading");
        	var formData = new FormData();
        	formData.append("itcode",itcode);
        	formData.append("file",document.getElementById("file").files[0]);
        	$.ajax({
                type: "post",
                url: baseUrl + "table/uploadFile",
                data: formData,
                contentType: false,
                processData: false,
                dataType: "json",
                success: function(data) {
                	alert(data.msg);
                	window.location.reload();
                }
        	});
        }
        
        
        
        apiready = function(){
            api.parseTapmode();
        }
        var toast = new auiToast();
        function showDefault(type){
            switch (type) {
                case "success":
                    toast.success({
                        title:"提交成功",
                        duration:2000
                    });
                    break;
                case "fail":
                    toast.fail({
                        title:"提交失败",
                        duration:2000
                    });
                    break;
                case "custom":
                    toast.custom({
                        title:"提交成功",
                        html:'<i class="aui-iconfont aui-icon-laud"></i>',
                        duration:2000
                    });
                    break;
                case "loading":
                    toast.loading({
                        title:"加载中",
                        duration:2000
                    });
                    break;
                case "cancel":
                    toast.cancel();
                    break;
                default:
                    break;
            }
        }
        </script>
    </head >
    <body >
    <div id = "list" hidden="hidden">
    <h1 align="center">创建的表</h1>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-form-list" id = "form2">    
    </ul>
	</div>
	</div>
	
	<div id="create" hidden="hidden">
    <h1 align="center">建表</h1>
    <div class="aui-content aui-margin-b-15">
    <ul class="aui-list aui-form-list" id = "form">
    	
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    表名
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="表名" id="tableName">
                </div>
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    字段1
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="名" id=field1>
                </div>
                <div class="aui-list-item-label">
                    类型
                </div>
                <div class="aui-list-item-input">
                    <select id="type1">
                        <option value = "int">int</option>
                        <option value = "varchar(255)">varchar(255)</option>
                        <option value = "varchar(20)">varchar(20)</option>                        
                        <option value="double">double</option>
                    </select>
                </div>
            </div>
        </li>
        <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    字段2
                </div>
                <div class="aui-list-item-input">
                    <input type="text" placeholder="名" id="field2">
                </div>
                <div class="aui-list-item-label">
                    类型
                </div>
                <div class="aui-list-item-input">
                    <select id="type2">
                        <option value = "int">int</option>
                        <option value = "varchar(255)">varchar(255)</option>
                        <option value = "varchar(20)">varchar(20)</option>                        
                        <option value="double">double</option>
                    </select>
                </div>
            </div>
        </li>
        
    </ul>
     <div class="aui-content aui-margin-b-15">
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="addField()">添加字段</div>
		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="commit()">提交</div>
	    <div class=" aui-btn-block"></div>
     </div>

</div>
</div>

<div class="container" style=""	id="introduce" hidden="hidden">
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <h3 class="display-3" style="color:#FF0000">建表奖励介绍</h3><br>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12 col-md-12">
                    <p class="lead" style="font-weight: bold;">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;建表奖励神州币</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;每次建表之后，当用户给表中每添加一条数据时，会对用户发放神州币奖励，建表之后永久存在不可删除。</p>
                    <p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;数据越多奖励越多哦。</p>
                </div>
            </div>           
            <div class="row">
                <div class="col-xs-12 col-md-12" align="center">
                    <br><p class="lead" style="font-weight: bold;">Good Luck！</p><br>
                </div>
            </div>
            <ul class="aui-list aui-form-list" id = "form3">
    	
         <li class="aui-list-item">
            <div class="aui-list-item-inner">
                <div class="aui-list-item-label">
                    上传文件
                </div>
                <div class="aui-list-item-input">
                    <input type="file" placeholder="上传文件" id="file">
                </div>
            </div>
        </li>
        </ul>
        		<div class="aui-btn aui-btn-primary aui-btn-block aui-btn-outlined" onClick="upload()">提交</div>
        </div> 
       
        <footer class="aui-bar aui-bar-tab" id="footer">
            <div class="aui-bar-tab-item aui-active" tapmode>
                <i><img src="../img/shouye.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">列表</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/zuixinjiexiao.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">建表</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/wode.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">我的</div>
            </div>
        </footer>
    </body>
</html>