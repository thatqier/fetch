package com.longriver.netpro.webview.cookieController;

import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Scanner;

import com.longriver.netpro.common.sina.SinaIdMidConverter;
import com.longriver.netpro.common.sina.WeiboSina;
import com.longriver.netpro.util.MQSender;
import com.longriver.netpro.webview.entity.TaskGuideBean;

/**
 * 微博点赞cookie方式
 * @author rhy
 * @date 2018-6-26 上午10:24:23
 * @version V1.0
 */
/**
 * @author rhy
 * @date 2018-6-26 上午10:51:50
 * @version V1.0
 */
public class WeiboPraiseCookie {

	public static void main(String[] args) {
		TaskGuideBean task = new TaskGuideBean();
		task.setCookieData("sso_info=v02m6alo5qztLGNs4S5jYOUsYyzlLONh5ymnZalpI-TmLSNo6C2jZOAsI2jpMA=;LT=1529978428;ALF=1561514428;ALC=ac%3D0%26bt%3D1529978428%26cv%3D5.0%26et%3D1561514428%26ic%3D1969532984%26login_time%3D1529978428%26scf%3D%26uid%3D6468650069%26vf%3D0%26vs%3D0%26vt%3D0%26es%3Dc25bed05193a112ac6dc1a946c0d6f48;SUBP=0033WrSXqPxfM725Ws9jqgMF55529P9D9WhakIHWNxfiCgkTk3PkD9l_5NHD95QcShqRSo-7ehq4Ws4DqcjeeKMp1KBfeKefe0xN;SUB=_2A252Ne5sDeRhGeBK7VoX9S7MzTWIHXVVQ1ikrDV_PUNbm9BeLVf3kW9NR5JbtJWIJx7srpLkYdks7_1Sy92jUZvb;tgc=TGT-NjQ2ODY1MDA2OQ==-1529978428-gz-7998234C9D1EFD57ED529DF1D1FC0CFF-1;");
		task.setCorpus("现在学生学习可真好");
		task.setCorpus("吕梁英雄传");
		task.setAddress("https://weibo.com/5056645402/G3mKyjCnM?from=page_1005055056645402_profile&wvr=6&mod=weibotime&type=comment#_rnd1530067748822");
//		task.setUrl("http://weibo.com/6468650069/");
		toComment(task);
//		getMid(task);
	}
	/**
	 * 微博点赞
	 * @param taskdo
	 */
	public static void toComment(TaskGuideBean task){
		
		try{
			
		String mid = getMid(task);
		URL url = new URL("https://weibo.com/aj/v6/like/add?ajwvr=6&__rnd="+System.currentTimeMillis());
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		
		String param = "act=post" +
				"&mid="+mid+"" +
				"&uid=6450817359" +
				"&forward=0" +
				"&isroot=0" +
				"&content="+URLEncoder.encode(task.getCorpus(), "utf-8")+"" +
				"&location=page_100505_single_weibo" +
				"&module=bcommlist" +
				"&pdetail=1005055056645402" +
				"&_t=0";
		
		connection.addRequestProperty("Host", "weibo.com");
		connection.addRequestProperty("Connection", "keep-alive");
		connection.addRequestProperty("Content-Length", String.valueOf(param.length()));
		connection.addRequestProperty("Origin", "https://weibo.com");
		connection.addRequestProperty("X-Requested-With", "XMLHttpRequest");
		connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.98 Safari/537.36 LBBROWSER");
		connection.addRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.addRequestProperty("Accept", "*/*");
		connection.addRequestProperty("Referer", task.getAddress());
		connection.addRequestProperty("Accept-Language", "zh-CN,zh;q=0.8");
		connection.addRequestProperty("Cookie", task.getCookieData());
		
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		
		PrintWriter pw = new PrintWriter(connection.getOutputStream());
		pw.print(param);
		pw.flush();
		
		Scanner sc = new Scanner(connection.getInputStream(),"utf-8");
		String result = "";
		while(sc.hasNext()){
			
			result += sc.nextLine();
		}
		String content = "";
		content = WeiboSina.getResultContent(result,content,task);
		
		isSuccess(task, content);
		
		}catch(Exception e){
			
			e.printStackTrace();
			isSuccess(task, "发生异常");
		}
	}
	
	/**
	 * 获取mid
	 * @param task
	 * @return
	 */
	public static String getMid(TaskGuideBean task) {
		String midEncode = task.getAddress().substring(task.getAddress().lastIndexOf("/")).replace("/", "");
		if(midEncode.indexOf("?") > -1){
			midEncode = midEncode.substring(0, midEncode.indexOf("?"));
		}
		if(midEncode.indexOf("#") > -1){
			midEncode = midEncode.substring(0, midEncode.indexOf("#"));
		}
		String mid = SinaIdMidConverter.midToId(midEncode);
		System.out.println(mid);
		return mid;
	}
	/**
	 * 判断是否成功
	 */
	public static void isSuccess(TaskGuideBean task,String msg){
		MQSender.toMQ(task,msg);
	}
}
