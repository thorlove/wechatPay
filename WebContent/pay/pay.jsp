<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
String jsondata = request.getParameter("jsonData");
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
<script type="text/javascript" src="../js/jquery-1.4.2.js"></script>
</head>

<body>
	<%-- <input type="hidden" name="appId" id="appId" value=<%=appId%> />
	<input type="hidden" name="timeStamp" id="timeStamp"
		value=<%=timeStamp%> />
	<input type="hidden" name="nonceStr" id="nonceStr" value=<%=nonceStr%> />
	<input type="hidden" name="packageValue" id="packageValue"
		value=<%=packageValue%> />
	<input type="hidden" name="paySign" id="paySign" value=<%=paySign%> /> --%>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<br>
	<table border="1">
		<tr>
			<>
		</tr>
	</table>
	<div style="text-align: center; size: 30px;">
		<input type="button" style="width: 200px; height: 80px;" value="微信支付"
			onclick="callpay()">
	</div>

</body>
<script type="text/javascript">

	


	function callpay() {
		WeixinJSBridge.invoke('getBrandWCPayRequest', <%=jsondata%>, function(res) {
			WeixinJSBridge.log(res.err_msg);
			alert(res.err_code + res.err_desc + res.err_msg);
			var json = <%=jsondata%>;
			alert("appId---"+json.appId);
			if (res.err_msg == "get_brand_wcpay_request:ok") {
				alert("微信支付成功!");
			} else if (res.err_msg == "get_brand_wcpay_request:cancel") {
				alert("用户取消支付!");
			} else {
				alert("支付失败!");
			}
		});
	}
</script>

</html>