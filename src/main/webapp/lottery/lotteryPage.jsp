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
        <title>夺宝首页</title>
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
        <script type='text/javascript' src='../js/jquery.textarea.autoheight.js'></script>
        <script type='text/javascript' src='../js/jquery.form.js'></script>
        <script type='text/javascript' src="../js/jquery-weui.js"></script>
        <script type='text/javascript' src='../js/fastclick.min.js'></script>
        <script type='text/javascript' src='../js/web3.min.js'></script>
        <script type='text/javascript' src='../js/bignumber.js'></script>
        <script type='text/javascript' src='../js/bootstrapValidator.min.js'></script>
        <script type="text/javascript" src="../js/aui_script/api.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-tab.js" ></script>
        <script type="text/javascript" src="../js/aui_script/aui-pull-refresh.js"></script>
        <script type="text/javascript">
            var itcode;
            var baseUrl = '/';
            var hbData;
            var smbData;
            var otherData;
            var newOpen;

            $(function() {
            	itcode = $("#itcode").text();
                $("#shouye").show();
                apiready = function(){
                    api.parseTapmode();
                }

                var tab = new auiTab({
                    element:document.getElementById("footer")
                },function(ret){
                    if(ret.index == 1) {
                        $("#wode").hide();
                        $("#zuixinjiexiao").hide();
                        $("#shouye").show();
                    }
                    if(ret.index == 2) {
                        $("#wode").hide();
                        $("#shouye").hide();
                        $("#zuixinjiexiao").show();
                    }
                    if(ret.index == 3) {
                        $("#shouye").hide();
                        $("#zuixinjiexiao").hide();
                        $("#wode").show();
                    }
                });

                //下拉刷新
                var pullRefresh = new auiPullToRefresh({
                    container: document.querySelector('.aui-refresh-content'),
                    triggerDistance: 100
                },function(ret){
                    if(ret.status=="success"){
                        setTimeout(function(){
                            location.reload();
                            pullRefresh.cancelLoading(); //刷新成功后调用此方法隐藏
                        },1000)
                    }
                })

                //  var toast = new auiToast({});
                //  toast.loading({
                //     title:"加载中",
                //     duration:2000
                // },function(ret){
                //     console.log(ret);
                //     setTimeout(function(){
                //         toast.hide();
                //     }, 3000)
                // });

                
                

                //
                window.showLotteryDetail = function(id) {
                    $.ajax({
                        type: "GET",
                        url: baseUrl + "getLotteryInfo",
                        data: {"jsonStr" : JSON.stringify({
                            "itcode" : itcode,
                            "id" : id
                        })},
                        dataType: "json",
                        success: function(data) {
                            if (data.success) {
                                var infoData = data.infoData;
                                var detailData = data.detailData;
                                var numberArr = infoData.winTicket.split("&");
                                var winnerArr = infoData.winner.split("&");
                                var htmlStr1 = "";
                                for(var index = 0; index < numberArr.length; index++) {
                                    htmlStr1 += "<tr><td style='word-break: break-all;text-align: center;'>"+numberArr[index]+"</td><td style='word-break: break-all;text-align: center;'>"+winnerArr[index]+"</td></tr>";
                                }
                                var htmlStr2 = "";
                                var rewordArr = [];
                                if(detailData.length == 0) {
                                    htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>抱歉，您未参与！</td></tr>";
                                    $("#modalResult").html(htmlStr1);
                                    $("#modalMine").html(htmlStr2);
                                    $("#resultModal").modal('show');
                                    return;
                                }

                                for(var index = 0; index < detailData.length; index++) {
                                    for(var j = 0; j < numberArr.length; j++) {
                                        if (numberArr[j] == detailData[index].ticket) {
                                            htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;color:red;'>"+detailData[index].ticket+"</td></tr>";
                                            rewordArr.push(detailData[index].winReword);
                                        } else {
                                            htmlStr2 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>"+detailData[index].ticket+"</td></tr>";
                                        }
                                    }
                                }

                                var htmlStr3 = "";
                                if(rewordArr.length == 0) {
                                    htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>很抱歉，您未中奖</td></tr>";
                                } else if(infoData.typeCode == 0) {
                                    var rewordStr = ""
                                    for (var i = 0; i < rewordArr.length; i++) {
                                        rewordStr += rewordArr[i] + "，";
                                    }
                                    htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>恭喜您获奖，红包码为：<strong style='color:red;'>" + rewordStr +"</strong>请在支付宝中搜索领取。</td></tr>";
                                } else if (infoData.typeCode == 1) {
                                    htmlStr3 += "<tr><td id='tdTime' style='word-break: break-all;text-align: center;'>恭喜您获奖，<strong style='color:red;'>" + infoData.reward+" SMB</strong>将在稍后发放到您的账户中！</td></tr>";
                                }

                                $("#modalResult").html(htmlStr1);
                                $("#modalMine").html(htmlStr2);
                                $("#modalHongbao").html(htmlStr3);
                                $("#lotteryTab").show();
                                $("#resultModal").modal('show');
                            }
                        }
                    });
                }

                //发送请求，获取夺宝信息，查看该用户是否参与此次夺宝，若参与，展示夺宝号码
                $.ajax({
                    type: "GET",
                    url: "getLotteryData",
                    data: {"itcode" : itcode},
                    dataType: "json",
                    success: function(data) {
                        if (data.success) {
                            hbData = data.hbData;
                            smbData = data.smbData;
                            otherData = data.otherData;
                            newOpen = data.newOpen;
                            var innerHtml = "";
                            var innerHtml2 = "";
                            
                            for(var index = 0; index < otherData.length; index++) {
                                innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+otherData[index].id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+otherData[index].id+"期】"+otherData[index].description+"</div><div class='aui-card-list-content' align='center'><img src='../img/lottery_korvschn_1.png' class='img-responsive center-block' style='height: 130px;width: 93%;'><div class='aui-content-padded'><font style='text-align: center;color: black;'>结果公布后开奖，当前已有&nbsp;<strong>" + otherData[index].nowSumAmount/10 + "</strong>&nbsp;人次参与</font></div>"

                                  + "<font style='text-align: center;color: gray;'><strong id='strOnce'>"+otherData[index].unitPrice+"</strong>&nbsp;SZB/夺宝码&nbsp;&nbsp;每人限购 <strong id='strLimit'>"+otherData[index].limitEveryday+"</strong>&nbsp;次&nbsp;&nbsp;</font></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>";
                            }

                            // if(itcode == "fannl" || itcode == "lizhe1" || itcode == "mojja" || itcode == "alexshen" || itcode == "zhoujingb") {
                            //     for(var index = 0; index < otherData.length; index++) {
                            //         innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+otherData[index].id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+otherData[index].id+"期】"+otherData[index].description+"</div><div class='aui-card-list-content' align='center'><img src='../img/lottery_pk_1.jpg' class='img-responsive center-block' style='height: 130px;width: 93%;'><div class='aui-content-padded'><font style='text-align: center;color: black;'>结果公布后开奖，当前已有&nbsp;<strong>" + otherData[index].nowSumAmount/10 + "</strong>&nbsp;人次参与</font></div>"

                            //           + "<font style='text-align: center;color: gray;'><strong id='strOnce'>"+otherData[index].unitPrice+"</strong>&nbsp;SZB/夺宝码&nbsp;&nbsp;每人限购 <strong id='strLimit'>"+otherData[index].limitEveryday+"</strong>&nbsp;次&nbsp;&nbsp;</font></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>";
                            //     }
                            // }

                            for(var index = 0; index < hbData.length; index++) {
                                if (hbData[index].backup3 == '') {
                                    innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+hbData[index].id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+hbData[index].id+"期】"+hbData[index].description+"</div><div class='aui-card-list-content' align='center'><img src='../img/lottery_rmb_1.jpg' class='img-responsive center-block' style='height: 130px;width: 93%;'><div class='aui-content-padded'><div class='aui-progress aui-progress-xs'><div class='aui-progress-bar' style='width: "+hbData[index].nowSumAmount/hbData[index].winSumAmount*100+"%;'></div></div></div><font style='text-align: center;color: gray;'><strong id='strOnce'>"+hbData[index].unitPrice+"</strong>&nbsp;SZB/夺宝码&nbsp;&nbsp;每人限购 <strong id='strLimit'>"+hbData[index].limitEveryday+"</strong>&nbsp;次&nbsp;&nbsp;已累计 <strong id='strHaved'>"+hbData[index].nowSumAmount+"</strong>&nbsp;SZB</font></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>";
                                } else {
                                    var imageUrl1 = hbData[index].backup3.replace("2","1");
                                    innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+hbData[index].id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+hbData[index].id+"期】"+hbData[index].name+"</div><div class='aui-card-list-content' align='center'><img src='../img/"+imageUrl1+"' class='img-responsive center-block' style='height: 130px;width: 93%;'><div class='aui-content-padded'>" + "<font style='text-align: center;color: black;'>" + hbData[index].description + "</font><br>" + "<font style='text-align: center;color: gray;'>当前累计参与&nbsp;<strong>" + hbData[index].nowSumAmount/10 + "</strong>&nbsp;人次</font><br>"

                                  + "<font style='text-align: center;color: gray;'><strong id='strOnce'>"+hbData[index].unitPrice+"</strong>&nbsp;SZB/夺宝码&nbsp;&nbsp;每人每日限购 <strong id='strLimit'>"+hbData[index].limitEveryday+"</strong>&nbsp;次&nbsp;&nbsp;</font></div></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>";
                                }
                            }
                            innerHtml += "<div class='aui-card-list' onClick='clickToDetail("+smbData.id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+smbData.id+"期】"+smbData.description+"</div><div class='aui-card-list-content' align='center'><img src='../img/lottery_szb_1.jpg' class='img-responsive center-block' style='height: 130px;width: 93%;'><div class='aui-content-padded'><div class='aui-progress aui-progress-xs'><div class='aui-progress-bar' style='width: "+smbData.nowSumAmount/smbData.winSumAmount*100+"%;'></div></div></div><font style='text-align: center;color: gray;'><strong id='strOnce'>"+smbData.unitPrice+"</strong>&nbsp;SZB/夺宝码&nbsp;&nbsp;每人限购 <strong id='strLimit'>"+smbData.limitEveryday+"</strong>&nbsp;次&nbsp;&nbsp;已累计 <strong id='strHaved'>"+smbData.nowSumAmount+"</strong>&nbsp;SZB</font></div><div class='aui-card-list-footer' align='center'><p><div><button type='button' class='btn btn-info btn-sm'>点击进入</button></div></p></div></div>"
                            $("#divLotteryList").prepend(innerHtml);

                            
                            for(var index = 0; index < newOpen.length; index++) {
                                var reg = new RegExp("&","g");
                                var winnerStr = newOpen[index].winner.replace(reg,",");
                                var ticketStr = newOpen[index].winTicket.replace(reg,",");

                                var bkImgStr = newOpen[index].backup2;
                                if (newOpen[index].typeCode == 0) {
                                    bkImgStr = "lottery_rmb_1.jpg";
                                } else if (newOpen[index].typeCode == 1) {
                                    bkImgStr = "lottery_szb_1.jpg";
                                }else if (newOpen[index].typeCode == 2) {
                                    // bkImgStr = "lottery_korvschn_1.png";
                                    bkImgStr = newOpen[index].backup3.replace("2","1");
                                }

                                innerHtml2 += "<div class='aui-card-list' style='background-color:#F7F0EA;' onClick='showLotteryDetail("+newOpen[index].id+")'><div class='aui-card-list-header' style='font-size:140%;'>【第"+newOpen[index].id+"期】 "+newOpen[index].name+"</div><div class='aui-card-list-content'><img src='../img/"+bkImgStr+"' class='img-responsive center-block' style='height: 130px;width: 93%;'></div><div class='aui-card-list-footer'>获奖者："+winnerStr+"<br>幸运号码："+ticketStr+"<br>开奖时间："+newOpen[index].backup1;

                                if (winnerStr.indexOf(itcode) > -1) {
                                    innerHtml2 += "<br>中奖提示：您已中奖！<br>点击进入查看详情。</div></div>";
                                } else {
                                    innerHtml2 += "<br>中奖提示：您未中奖！<br>点击进入查看详情。</div></div>";
                                }
                            }
                            $("#divNewOpenList").prepend(innerHtml2);
                        }
                    }
                });
              	//进入单次抽奖详情页面
                window.clickToDetail = function(id) {
                    /* window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryBuyPage.jsp?itcode="+itcode+"&id="+id; */
                    window.location.href = baseUrl + "lotteryBuyPage?itcode="+itcode+"&id="+id;
                }
              	
                $("#introduce").click(function() {
                    window.location.href = baseUrl + "lotteryIntroduce";
                });
                $("#myRecores").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("敬请期待！");
                });
                $("#myAchieve").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("敬请期待！");
                });
                $("#contactUs").click(function() {
                    // window.location.href="/mobile/plugin/dch/smbTest/lottery/lotteryIntroduce.jsp";
                    alert("如有任何疑问，请与fannl@digitalchina.com联系！");
                });
            });
        </script>
    </head>
    <body>
    	<font id="itcode" hidden="hidden">${itcode}</font>
        <div id="shouye" hidden="hidden">
            <section class="aui-refresh-content">
                <div class="aui-content">
                    <div id="divLotteryList">
                        <div style="height: 50px;"> 
                        </div>
                    </div>
                </div>
            </section>
        </div>
        <div id="zuixinjiexiao" hidden="hidden">
            <section class="aui-refresh-content">
                <div class="aui-content">
                    <div id="divNewOpenList">
                        <div style="height: 50px;">
                        </div>
                    </div>
                </div>
            </section>
        </div>
        <div id="wode" hidden="hidden">
            <section class="aui-content">
                <ul class="aui-list aui-list-in aui-margin-b-15">
                    <li id="introduce" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/wanfajianjie.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">玩法简介</div>
                        </div>
                    </li>
                    <li id="myRecores" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/duobaojilu.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">夺宝记录</div>
                        </div>
                    </li>
                    <li id="myAchieve" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/wodechengjiu.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">我的成就</div>
                        </div>
                    </li>
                    <li id="contactUs" class="aui-list-item">
                        <div class="aui-list-item-label-icon">
                            <i><img src="../img/lianxiwomen.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                        </div>
                        <div class="aui-list-item-inner aui-list-item-arrow">
                            <div class="aui-list-item-title">联系我们</div>
                        </div>
                    </li>
                </ul>
            </section>
        </div>
        <footer class="aui-bar aui-bar-tab" id="footer">
            <div class="aui-bar-tab-item aui-active" tapmode>
                <i><img src="../img/shouye.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">进行中</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/zuixinjiexiao.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">最新揭晓</div>
            </div>
            <div class="aui-bar-tab-item" tapmode>
                <i><img src="../img/wode.png"  class="img-responsive center-block" style="width: 25px; height: 25px;"></i>
                <div class="aui-bar-tab-label">我的</div>
            </div>
        </footer>
        <div class="modal" id="resultModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-body">
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">开奖结果</caption>
                            <col style="width: 50%" />
                            <col style="width: 50%" />
                            <thead>
                                <tr>
                                  <th style="text-align: center;color: gray;font-size: 80%;">获奖码</th>
                                  <th style="text-align: center;color: gray;font-size: 80%;">获奖人Itcode</th>
                                </tr>
                            </thead>
                            <tbody id="modalResult">
                            </tbody>
                        </table>
                        <table class="table table-condensed" style="table-layout: fixed;">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;">我的夺宝码</caption>
                            <tbody id="modalMine">
                            </tbody>
                        </table>
                        <table id="lotteryTab" class="table table-condensed" style="table-layout: fixed;" hidden="hidden">
                            <caption id="tabCaption" style="word-break: break-all;text-align: center;color: red;">中奖提示</caption>
                            <tbody id="modalHongbao">
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>