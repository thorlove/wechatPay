<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";

String appId = request.getParameter("appid");
String timeStamp = request.getParameter("timeStamp");
String nonceStr = request.getParameter("nonceStr");
String packageValue = request.getParameter("package");
String paySign = request.getParameter("sign");
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>微信支付</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  </body>
  <script type="text/javascript">
  	document.addEventListener('WeixinJSBridgeReady', function onBridgeReady() {
		//alert("init");
		//公众号支付
		function topay(){
			WeixinJSBridge.invoke('getBrandWCPayRequest',{
				"appId" : "<%=appId%>", //公众号名称，由商户传入
				"timeStamp" : "<%=timeStamp%>", //时间戳
				"nonceStr" : "<%=nonceStr%>", //随机串
				"package" : "<%=packageValue%>", ////扩展包
				"signType" : "MD5", //微信签名方式:md5
				"paySign" : "<%=paySign%>" ////微信签名
			},function(res){
				//alert( res.err_msg ); // alert("OK");
				if(res.err_msg == "get_brand_wcpay_request:ok"){  
	                alert("微信支付成功!");  
	            }else if(res.err_msg == "get_brand_wcpay_request:cancel"){  
	                alert("用户取消支付!");  
	            }else{  
	               alert("支付失败!");  
	            } 
				// 使用以上方式判断前端返回,微信团队郑重提示：res.err_msg将在用户支付成功后返回ok，但并不保证它绝对可靠。
				//因此微信团队建议，当收到ok返回时，向商户后台询问是否收到交易成功的通知，若收到通知，前端展示交易成功的界面；若此时未收到通知，商户后台主动调用查询订单接口，查询订单的当前状态，并反馈给前	展示相应的界面。
			});
		}
		WeixinJSBridge.log('yo~ ready.');
		topay();
	}, false);
  </script>
  
</html>
