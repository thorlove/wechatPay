package com.servlet;

import java.io.IOException;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.SingleThreadModel;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.bean.WxPayBean;
import com.utils.CommonUtil;
import com.utils.GetWxOrderno;
import com.utils.RequestHandler;
import com.utils.Sha1Util;
import com.utils.TenpayUtil;


public class TopayServlet extends HttpServlet{
	public void init() throws ServletException {
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//网页授权后获取传递的参数
		String userId = request.getParameter("userId"); 	
		String orderNo = request.getParameter("orderNo"); 
		String money = "0.01";
		String describe = "测试支付";
		String code = request.getParameter("code");
		//金额转化为分为单位
		float sessionmoney = Float.parseFloat(money);
		String finalmoney = String.format("%.2f", sessionmoney);
		finalmoney = finalmoney.replace(".", "");
		
		WxPayBean wpb=new WxPayBean();
		//商户相关资料 
		String appid = wpb.getAppid();
		String appsecret = wpb.getAppsecret();
		String partner = wpb.getPartner();
		String partnerkey = wpb.getPartnerkey();
		String apikey= wpb.getApikey();
		String mch_id = wpb.getMch_id();
				
		String openId ="";
		String URL = "https://api.weixin.qq.com/sns/oauth2/access_token?appid="+appid+"&secret="+appsecret+"&code="+code+"&grant_type=authorization_code";
		
		JSONObject jsonObject = CommonUtil.httpsRequest(URL, "GET", null);
		if (null != jsonObject) {
			openId = jsonObject.getString("openid");
		}
		
		//获取openId后调用统一支付接口https://api.mch.weixin.qq.com/pay/unifiedorder
		String currTime = TenpayUtil.getCurrTime();
		//8位日期
		String strTime = currTime.substring(8, currTime.length());
		//四位随机数
		String strRandom = TenpayUtil.buildRandom(4) + "";
		//10位序列号,可以自行调整。
		String strReq = strTime + strRandom;
		
		
		//子商户号  非必输
		//String sub_mch_id="";
		//设备号   非必输
		String device_info="";
		//随机数 
		String nonce_str = strReq;	
		//商品描述根据情况修改
		String body = describe;
		//附加数据
		String attach = userId;
		//商户订单号
		String out_trade_no = orderNo;
		int intMoney = Integer.parseInt(finalmoney);
		
		//总金额以分为单位，不带小数点
		int total_fee = intMoney;
		//订单生成的机器 IP
		String spbill_create_ip = request.getRemoteAddr();
		//订 单 生 成 时 间   非必输
		//String time_start ="";
		//订单失效时间      非必输
		//String time_expire = "";
		//商品标记   非必输
		//String goods_tag = "";
		
		//这里notify_url是 支付完成后微信发给该链接信息，可以判断会员是否支付成功，改变订单状态等。
		String notify_url =wpb.getNotify_url();
		
		String trade_type = "JSAPI";
		String openid = openId;
		//非必输
		//String product_id = "";
		SortedMap<String, String> packageParams = new TreeMap<String, String>();
		packageParams.put("appid", appid);  
		packageParams.put("mch_id", mch_id);  
		packageParams.put("nonce_str", nonce_str);  
		packageParams.put("body", body);  
		packageParams.put("attach", attach);  
		packageParams.put("out_trade_no", out_trade_no);  
		
		packageParams.put("total_fee", total_fee+""); 
		packageParams.put("spbill_create_ip", spbill_create_ip);  
		packageParams.put("notify_url", notify_url);  
		
		packageParams.put("trade_type", trade_type);  
		packageParams.put("openid", openid);  

		RequestHandler reqHandler = new RequestHandler(request, response);
		reqHandler.init(appid, appsecret, partnerkey,apikey);
		
		String sign = reqHandler.createSign(packageParams);
		String xml="<xml>"+
				"<appid>"+appid+"</appid>"+
				"<mch_id>"+mch_id+"</mch_id>"+
				"<nonce_str>"+nonce_str+"</nonce_str>"+
				"<sign>"+sign+"</sign>"+
				"<body><![CDATA["+body+"]]></body>"+
				"<attach>"+attach+"</attach>"+
				"<out_trade_no>"+out_trade_no+"</out_trade_no>"+
				"<total_fee>"+total_fee+"</total_fee>"+
				"<spbill_create_ip>"+spbill_create_ip+"</spbill_create_ip>"+
				"<notify_url>"+notify_url+"</notify_url>"+
				"<trade_type>"+trade_type+"</trade_type>"+
				"<openid>"+openid+"</openid>"+
				"</xml>";
		String createOrderURL = "https://api.mch.weixin.qq.com/pay/unifiedorder";
		String prepay_id="";
		try {
			prepay_id = new GetWxOrderno().getPayNo(createOrderURL, xml);
			if(prepay_id.equals("")){
				request.setAttribute("ErrorMsg", "统一支付接口获取预支付订单出错");
				response.sendRedirect("error.jsp");
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SortedMap<String, String> finalpackage = new TreeMap<String, String>();
		String appid2 = appid;
		String timestamp = Sha1Util.getTimeStamp();
		String nonceStr2 = nonce_str;
		String prepay_id2 = "prepay_id="+prepay_id;
		String packages = prepay_id2;
		finalpackage.put("appId", appid2);  
		finalpackage.put("timeStamp", timestamp);  
		finalpackage.put("nonceStr", nonceStr2);  
		finalpackage.put("package", packages);  
		finalpackage.put("signType", "MD5");
		String finalsign = reqHandler.createSign(finalpackage);
		System.out.println("pay.jsp?appid="+appid2+"&timeStamp="+timestamp+"&nonceStr="+nonceStr2+"&package="+packages+"&sign="+finalsign);
		if(!response.isCommitted()){
		response.sendRedirect("pay.jsp?appid="+appid2+"&timeStamp="+timestamp+"&nonceStr="+nonceStr2+"&package="+packages+"&sign="+finalsign);
		}
		//return;
	}

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}

	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}
}
