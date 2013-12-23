package com.lostleon.qdrealestate.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.helper.StringUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.lostleon.qdrealestate.bean.DailyMetrics;
import com.lostleon.qdrealestate.bean.District;
import com.lostleon.qdrealestate.bean.ProjectBean;
import com.lostleon.qdrealestate.bean.ProjectStatus;
import com.lostleon.qdrealestate.map.GeoHelper;
import com.lostleon.qdrealestate.storage.JedisHelper;
import com.lostleon.qdrealestate.util.Util;

public class Crawler {
	
	private final static Logger crawListlogger = LoggerFactory.getLogger("crawList");
	private final static Logger logger = LoggerFactory.getLogger("logger");
	
	private static final String URL_LIST = "http://www.qdfd.com.cn/complexPro.asp?districtID=&projectAdr=&projectName=&buildingType=1&houseArea=0&averagePrice=0&selState=-1&selCircle=0&page=";
	private static final String URL_PROJ = "http://www.qdfd.com.cn/proDetail.asp?projectID=";
	private static final String URL_UNIT = "http://www.qdfd.com.cn/Presell.asp?projectID=";
	private static final String URL_PRICE = "http://www.qdfd.com.cn/His_project_Chart.asp?projectID=";
	
	/* 暂存Project */
	private List<ProjectBean> projects = new ArrayList<ProjectBean>();
	
	/* 搜索页的页数 */
	private int listPageNum = 94;
	
	public void init() {
		Connection.init();
		JedisHelper.init();
	}
	
	public void close() {
		JedisHelper.close();
		Connection.close();
	}
	
	/**
	 * 获取Project列表页
	 * @throws CrawlException
	 */
	public void crawlList() throws CrawlException {
		
		int countPage = 0;
		int countRecord = 0;
		
//		for (int i = 1; i <= listPageNum; i ++) {
		for (int i = 1; i <= 2; i ++) {
			
			String ret = Connection.doGet(URL_LIST + i);
			if (StringUtil.isBlank(ret)) {
				throw new CrawlException("HttpGet blank:" + URL_LIST + i);
			}
			
			Document doc = Jsoup.parse(ret);
			Element count = doc.select("table#Table7").first();
			countPage = Integer.valueOf(count.select("option").last().text());
			countRecord = Integer.valueOf(count.select("font[color=blue]").first().text());
			listPageNum = countPage;
			crawListlogger.info("parseCount|countPage=" + countPage + "|countRecord=" + countRecord);
			/* 元素如下：
			 * 
			 <table width='100%' ID="Table7" class="px12">
			   <tr>
			     <td>
                   <td>第<font color=red>1</font>页/共94页</td>
                   <td>查到记录共<font color=blue>1865</font>条</td>
                   <td><A href='complexPro.asp?page=2&districtID=&projectAdr=&projectName=&buildingType=1&houseArea=0&averagePrice=0&selState=-1&selCircle=0'>[下一页]</A></td>
                 </td>
             ......
			 * 
			 * 
			 */
			Elements pxbb1s = doc.select(".pxbb1");
			if (pxbb1s.size() % 8 != 0) {
				throw new CrawlException("Parse .pxbb1 failed, size is:" + pxbb1s.size());
			}
			/*每行元素如下，所以.pxbb1应该是8的倍数（注意还有个<a></a>是.pxbb1）
			<tr>
			  <td class="pxbb1" height="25" align=center style="padding:3" style="color:red">在售</td>
			  <td class="pxbb1" align=center style="padding:3"><a class="pxbb1" href=proDetail.asp?projectID=1462&proname=海上罗兰>海上罗兰</a></td>
			  <td class="pxbb1" align=center style="padding:3">胶南市滨海大道路1399号</td>
			  <td class="pxbb1" align=center style="padding:3">1591</td>
			  <td class="pxbb1" align=center style="padding:3">3972</td>
			  <td class="pxbb1" align=center style="padding:3">胶南市</td>
			  <td class="pxbb1" align=center style="padding:3"><span style="cursor: hand" language=javascript  onmouseover="this.style.color='#FF0000'" onClick="OpenMapWindow('备案楼盘','海上罗兰','e_CS','1462')"><img src="imagesnew/xp%201.gif" width="20" height="17" alt="海上罗兰"></span></td>
			</tr>
			<tr>
			  <td height=1 background="/images/h_dot.gif" colspan=7></td>
			</tr>
			 */
			for (int j = 0; j < pxbb1s.size(); ) {
				ProjectStatus st = ProjectStatus.getStatusByCHN(ParseHelper.trimComma(pxbb1s.get(j).text()));
				j += 2;
				String prjHref = pxbb1s.get(j).attr("href");
				int prjHrefParamBegin = prjHref.indexOf("?") + 1;
				int prjId = Integer.valueOf(ParseHelper.getQueryValue(prjHref.substring(prjHrefParamBegin), "projectID"));
				j += 6;
				projects.add(new ProjectBean(prjId, st));
				crawListlogger.info("addProject|id=" + prjId + "|status=" + st);
			}
		} //end of pages
		crawListlogger.info("totalProject|expectNum=" + countRecord + "|realNum=" + projects.size());
	}
	
	/**
	 * 对比获取到的Projects和Redis中存储的数据
	 * @throws CrawlException 
	 */
	public void diffList() throws CrawlException {
		for (ProjectBean p : projects) {
			int id = p.getProjectId();
			ProjectStatus onlineStatus = p.getProjectStatus();
			String storedStatus = JedisHelper.getJedis().hget("PROJECT:" + p.getProjectId(), "status");
			
			if (storedStatus == null) {
				// 如果从未存储过，则存储
				crawListlogger.info("diffList|" + id + "|web=" + onlineStatus + "|redis=" + storedStatus + "|create");
				this.createProject(id, onlineStatus);
			} else if (onlineStatus.equals(ProjectStatus.PREPARE) || storedStatus.equals(ProjectStatus.SOLDOUT)) {
				// 否则，如果线上从未开售，或者存储中就已售完，则忽略
				crawListlogger.info("diffList|" + id + "|web=" + onlineStatus + "|redis=" + storedStatus + "|ignore");
			} else {
				// 否则，说明曾经存储过，且仍然在售，则解析价格，并更新存储
				crawListlogger.info("diffList|" + id + "|web=" + onlineStatus + "|redis=" + storedStatus + "|update");
				this.updateProject(id, onlineStatus);
			}
		}
	}
	
	/**
	 * 获取单个Project页
	 * @param id
	 * @return
	 * @throws CrawlException 
	 */
	public ProjectBean crawlProject(int id, ProjectStatus status) throws CrawlException {
		String ret = Connection.doGet(URL_PROJ + id);
		if (StringUtil.isBlank(ret)) {
			throw new CrawlException("HttpGet blank:" + URL_PROJ + id);
		}
		
		Document doc = Jsoup.parse(ret);
		Elements table2s = doc.select("td.table2");
		/*
		<table width="99%"  border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td class="table1">项目名称：</td>
				<td class="table2">青岛禧徕乐国际家居博览城小区</td>
				<td class="table1">所在区县：</td>
				<td class="table2">胶州市</td>
			</tr>
			<tr>
				<td class="table1">项目地址：</td>
				<td class="table2">胶州市兰州西路819号</td>
				<td class="table1">企业名称：</td>
				<td class="table2"><a href="Enterprisedetail.asp?Comp_ID=849">禧徕乐（青岛）投资发展有限公司</a></td>
			</tr>
		</table>
		<table width="99%"  border="0" cellspacing="0" cellpadding="0">
			<tr>
				<td width="16%" class="table1">住宅套数</td>
				<td width="8%" class="table2">292</td>
				<td width="16%" class="table1">住宅面积</td>
				<td width="10%" class="table2">37,050 ㎡</td>
				<td width="16%" class="table1">总套数</td>
				<td width="8%" class="table2">2819</td>
				<td width="16%" class="table1">总面积</td>
				<td width="10%" class="table2">285,607 ㎡</td>
			</tr>
			<tr>
				<td class="table1">可售住宅套数</td>
				<td class="table2"><span class="style2">222</span></td>
				<td class="table1">可售住宅面积</td>
				<td class="table2"><span class="style2">28,459 ㎡</span></td>
				<td class="table1">可售总套数</td>
				<td class="table2"><span class="style2">1526</span></td>
				<td class="table1">可售总面积</td>
				<td class="table2"><span class="style2">206,606 ㎡</span></td>
			</tr>
			<tr>
				<td class="table1">预定住宅套数</td>
				<td class="table2">0</td>
				<td class="table1">预定住宅面积</td>
				<td class="table2">0 ㎡</td>
				<td class="table1">预定总套数</td>
				<td class="table2">0</td>
				<td class="table1">预定总面积</td>
				<td class="table2">0 ㎡</td>
			</tr>
			<tr>
				<td class="table1">已售住宅套数</td>
				<td class="table2">70</td>
				<td class="table1">已售住宅面积</td>
				<td class="table2">8,591 ㎡</td>
				<td class="table1">已售总套数</td>
				<td class="table2">1293</td>
				<td class="table1">已售总面积</td>
				<td class="table2">79,001 ㎡</td>
			</tr>
			<tr>
				<td class="table1">已登记住宅套数</td>
				<td class="table2"><span class="style1">49</span></td>
				<td class="table1">已登记住宅面积</td>
				<td class="table2"><span class="style1">6,005 ㎡</span></td>
				<td class="table1">已登记总套数</td>
				<td class="table2"><span class="style1">626</span></td>
				<td class="table1">已登记总面积</td>
				<td class="table2"><span class="style1">40,160 ㎡</span></td>
			</tr>
		</table>
		 */
		
		String name = ParseHelper.trim(table2s.get(0).text());
		District district = District.getDistrictByCHN(ParseHelper.trim(table2s.get(1).text()));
		String addr = ParseHelper.trim(table2s.get(2).text());
		String company = ParseHelper.trim(table2s.get(3).text());
		
		int residentNumTotal = Integer.valueOf(ParseHelper.trimComma(table2s.get(4).text()));
		int residentNumAvail = Integer.valueOf(ParseHelper.trimComma(table2s.get(8).text()));
		//TODO should be accurate
		float residentSizeTotal = Float.valueOf(ParseHelper.trimCommaAndReserveFirst(table2s.get(5).text()));
		float residentSizeAvail = Float.valueOf(ParseHelper.trimCommaAndReserveFirst(table2s.get(9).text()));
		
		// now get latlng from Baidu API
		if (StringUtil.isBlank(addr)) {
			addr = name;	// 如果地址为空，则设置项目名称为地址
		}
		String addrEncoded = null;
		try {
			addrEncoded = URLEncoder.encode(addr, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("crawlProject|AddrEncodeException:" + addr, e);
		}
		String location = Connection.doGet(GeoHelper.GEOCODING_URL + addrEncoded);
		if (StringUtil.isBlank(location)) {
			throw new CrawlException("HttpGet blank:" + GeoHelper.GEOCODING_URL + addrEncoded);
		}
		/*
		 * {"status":0,"result":{"location":{"lng":119.95106201677,"lat":36.788550047135},"precise":0,"confidence":14,"level":"\u533a\u53bf"}}
		 */
		JsonElement locJson = new JsonParser().parse(location);
		double lng = locJson.getAsJsonObject().get("result").getAsJsonObject()
				.get("location").getAsJsonObject().get("lng").getAsDouble();
		double lat = locJson.getAsJsonObject().get("result").getAsJsonObject()
				.get("location").getAsJsonObject().get("lat").getAsDouble();
		
		ProjectBean p = new ProjectBean(id, status, name, district, addr,
				company, lng, lat, residentNumTotal, residentNumAvail,
				residentSizeTotal, residentSizeAvail);
		return p;

	}
	
	/**
	 * 爬取并存储一个新的Project
	 * @param id
	 * @param st
	 */
	private void createProject(int id, ProjectStatus st) {
		ProjectBean richPrj = null;
		try {
			richPrj = this.crawlProject(id, st);
		} catch (CrawlException e) {
			logger.error("createProject|CrawlProjectException|" + id, e);
		}
		JedisHelper.createProject(richPrj);
		if (st.equals(ProjectStatus.SOLDOUT)) {
			//TODO 如果是售完项目，则把最终的价格保存起来
		}
	}
	
	/**
	 * 爬取并更新一个已有的Project
	 * @param id
	 * @param st
	 * @throws CrawlException 
	 */
	private void updateProject(int id, ProjectStatus st) throws CrawlException {
		JedisHelper.updateProjectStatus(id, st);
		String ret = Connection.doGet(URL_PRICE + id);
		if (StringUtil.isBlank(ret)) {
			throw new CrawlException("HttpGet blank:" + URL_PROJ + id);
		}
		
		Document doc = Jsoup.parse(ret);
		Elements scripts = doc.select("script[LANGUAGE=Vbscript]");
		if (scripts.size() == 1) {
			String data = scripts.first().data();
			
			/*
			 * ct = 6
				ReDim arrName(6)
				ReDim arrValue(6,2)
				arrName(0)="11-26"
				arrValue(0,0)="7447.65"
				arrValue(0,1)="7384.42"
				arrName(1)="11-27"
				arrValue(1,0)="7448.88"
				arrValue(1,1)="7385.69"
				arrName(2)="12-03"
				arrValue(2,0)="7450.49"
				arrValue(2,1)="7387.37"
				arrName(3)="12-04"
				arrValue(3,0)="7454.93"
				arrValue(3,1)="7391.99"
				arrName(4)="12-07"
				arrValue(4,0)="7455.02"
				arrValue(4,1)="7392.12"
				arrName(5)="12-09"
				arrValue(5,0)="7456.6"
				arrValue(5,1)="7393.76"
			 */
			
			//获取最新均价的日期
			Pattern patternName = Pattern.compile("arrName\\(\\d+\\)=\"\\d{2}-\\d{2}\"");    
			Matcher matcherName = patternName.matcher(data);
			String arrNameStr = null;
			while (matcherName.find()) {
				arrNameStr = matcherName.group();	//找到最后一个arrName
			}
			String dateStr = arrNameStr.substring(arrNameStr.length() - 6, arrNameStr.length() - 1);
			Date dateWeb = Util.str2date(dateStr);
			
			//计算昨天（也即最新的日期）是否有最新报价，如果有，说明有成交，如果没有则忽略
			Date dateCur = new Date(); 
			if (dateCur.getTime() - dateWeb.getTime() < 24 * 60 * 60 * 1000) {
				
				//获取截止到昨天23:59的均价
				Pattern patternValue=Pattern.compile("arrValue\\(\\d+,1\\)=\"\\d+\\.?\\d+?\"");    
				Matcher matcherValue=patternValue.matcher(data);
				String arrValueStr = null;
				while (matcherValue.find()) {
					arrValueStr = matcherValue.group();	//找到最后一个arrValue
				}
				String priceStr = arrValueStr.substring(arrValueStr.indexOf("\"") + 1, arrValueStr.lastIndexOf("\""));
				float price = Float.valueOf(priceStr);
				
				//获取之前记录的成交面积、成交数量
				float lastTotalSoldSize = 0.0f;
				int lastTotalSoldNum = 0;
				ProjectBean lastPrj = JedisHelper.getProject(id);
				if (lastPrj != null) {
					lastTotalSoldSize = lastPrj.getResidentSizeTotal() - lastPrj.getResidentSizeAvail();
					lastTotalSoldNum = lastPrj.getResidentNumTotal() - lastPrj.getResidentNumAvail();
				}
				
				//获取截止到昨天23:59的成交面积、成交数量
				ProjectBean curPrj = this.crawlProject(id, st);
				float curTotalSoldSize = curPrj.getResidentSizeTotal() - curPrj.getResidentNumAvail();
				int curTotalSoldNum = curPrj.getResidentNumTotal() - curPrj.getResidentNumAvail();
				
				//最新成交价=（截止昨日末均价*截止昨日末成交总面积-原均价*原总成交面积）/昨天成交面积
				float todayAvgPrice = price;
				DailyMetrics lastMet = JedisHelper.getLatestDailyMetrics(id);
				if (lastMet != null) {
					float lastTotalAvgPrice = lastMet.getTotalAvgPrice();
					todayAvgPrice = (price * curTotalSoldSize - lastTotalAvgPrice * lastTotalSoldSize) / (curTotalSoldSize - lastTotalSoldSize);
				}
				
				//终于拿到想要的Metrics了
				float todaySoldSize = curTotalSoldSize - lastTotalSoldSize;
				int todaySoldNum = curTotalSoldNum - lastTotalSoldNum;
				DailyMetrics m = new DailyMetrics(dateWeb, curTotalSoldSize,
						curTotalSoldNum, price, todaySoldSize, todaySoldNum,
						todayAvgPrice);
				
				//保存DailyMetrics信息和Project信息
				JedisHelper.addDailyMetrics(id, m);
				JedisHelper.updateProjectMetrics(id, curPrj.getResidentNumAvail(), curPrj.getResidentSizeAvail());
			}
		}

	}
}
