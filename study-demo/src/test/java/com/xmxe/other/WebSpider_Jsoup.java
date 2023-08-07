package com.xmxe.other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.alibaba.fastjson.JSONArray;

public class WebSpider_Jsoup {
	public static void main(String[] args) throws Exception {
		String url = "http://www.nmc.cn/publish/forecast/ASD/taian2.html";
		// Map<String,Object> map = new HashMap<>();
		// map.put("Id","6b340d4c17ed47449409f35a709ca298");//追加参数
		// HttpClientUtil client = new HttpClientUtil();
		// HttpResult result = client.doGet(url);
		// System.err.println(result.getBody());
		// String content = result.getBody();

		Document doc = Jsoup.connect(url).timeout(30000).get();
		// Document doc = Jsoup.parse(content);
		Elements times = doc.getElementById("day0").getElementsByClass("row first");// 时间
		List<String> timeList = times.eachText();
		Elements qw = doc.getElementById("day0").getElementsByClass("row wd");// 温度
		List<String> qwList = qw.eachText();
		Elements wind = doc.getElementById("day0").getElementsByClass("row winds");// 风速
		List<String> windList = wind.eachText();
		Elements qy = doc.getElementById("day0").getElementsByClass("row qy");// 气压
		List<String> qyList = qy.eachText();
		Elements xdsd = doc.getElementById("day0").getElementsByClass("row xdsd");// 相对湿度
		List<String> xdsdList = xdsd.eachText();
		String[] timeArr = new String[]{};// 时间
		String[] qwArr = new String[]{};// 气温
		String[] windArr = new String[]{};// 风速
		String[] qyArr = new String[]{};// 气压
		String[] xdsdArr = new String[]{};// 相对湿度
		if (timeList != null && timeList.size() > 0) {
			for (String time : timeList) {
				timeArr = time.split(" ");
			}
		}

		if (qwList != null && qwList.size() > 0) {
			for (String var : qwList) {
				qwArr = var.split(" ");
			}
		}
		if (qyList != null && qyList.size() > 0) {
			for (String var : qyList) {
				qyArr = var.split(" ");
			}
		}
		if (windList != null && windList.size() > 0) {
			for (String var : windList) {
				windArr = var.split(" ");
			}
		}
		if (xdsdList != null && xdsdList.size() > 0) {
			for (String var : xdsdList) {
				xdsdArr = var.split(" ");
			}
		}
		for (int i = 0; i < timeArr.length; i++) {
			System.err.println("时间" + timeArr[i] + "\t" + "的温度为" + qwArr[i] + "\t" + "风速为" + windArr[i] + "\t" + "气压为"
					+ qyArr[i] + "\t" + "相对湿度为" + xdsdArr[i]);

		}
		String[] handers = { "datetime（时间）", "temp（温度）", "ws（风速）", "hu（湿度）", "pressure(气压)" };
		String filedisplay = "test.xls";
		filedisplay = URLEncoder.encode(filedisplay, "UTF-8");
		HSSFWorkbook wb = new HSSFWorkbook();// 创建工作簿
		HSSFSheet sheet = wb.createSheet("操作");// 第一个sheet
		HSSFRow rowFirst = sheet.createRow(0);// 第一个sheet第一行为标题
		rowFirst.setHeight((short) 500);
		HSSFCellStyle cellStyle = wb.createCellStyle();// 创建单元格样式对象
		// cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
		// cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);
		for (int i = 0; i < handers.length; i++) {
			sheet.setColumnWidth((short) i, (short) 4000);// 设置列宽
		}
		// 写标题了
		for (int i = 0; i < handers.length; i++) {
			// 获取第一行的每一个单元格
			HSSFCell cell = rowFirst.createCell(i);
			// 往单元格里面写入值
			cell.setCellValue(handers[i]);
			cell.setCellStyle(cellStyle);
		}
		SimpleDateFormat sf = new SimpleDateFormat("yyyy/MM/dd");
		String now = sf.format(new Date());
		for (int i = 1; i < 9; i++) {

			// 创建数据行
			HSSFRow row = sheet.createRow(i + 1);
			row.setHeight((short) 400); // 设置每行的高度
			// 设置对应单元格的值

			if (timeArr[i].contains("日")) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(new Date());
				cal.add(Calendar.DATE, 1);
				String nextDay = sf.format(cal.getTime());
				now = nextDay;
				timeArr[i] = timeArr[i].substring(timeArr[i].indexOf("日") + 1);
			}
			row.createCell(0).setCellValue(now + " " + timeArr[i]);
			row.createCell(1).setCellValue(qwArr[i].replace("℃", ""));
			row.createCell(2).setCellValue(windArr[i].replace("米/秒", ""));
			row.createCell(3).setCellValue(xdsdArr[i].replace("%", ""));
			row.createCell(4).setCellValue(qyArr[i].replace("hPa", ""));
		}
		OutputStream os = null;
		try {
			String name = String.valueOf(new Date().getTime()) + ".xlsx";
			os = new FileOutputStream(new File("C:\\Users\\Administrator\\Desktop\\新建文件夹\\" + name));
			wb.write(os);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if(os != null)
				os.close();
			wb.close();
		}
	}
	

	public static void 爬取区域行政编码() throws IOException {
		class ChinaRegionsInfo{
			 /**
			 * 行政区域编码
			 */
			private String code;
			/**
			 * 行政区域名称
			 */
			private String name;
			/**
			 * 行政区域类型,1:省份,2：城市,3：区或者县城
			 */
			private Integer type;
			/**
			 * 上一级行政区域编码
			 */
			private String parentCode;

			public void setType(Integer type){
				this.type = type;
			}
			public Integer getType() {
				return type;
			}
			public void setParentCode(String parentCode){
				this.parentCode = parentCode;
			}
			public String getParentCode(){
				return parentCode;
			}
			public void setName(String name){
				this.name = name;
			}
			public String getName(){
				return name;
			}
			public void setCode(String code){
				this.code = code;
			}
			public String getCode(){
				return code;
			}
		}
		
		//需要抓取的网页地址
		String URL = "http://www.mca.gov.cn//article/sj/xzqh/2020/202006/202008310601.shtml";
		List<ChinaRegionsInfo> regionsInfoList = new ArrayList<>();
		//抓取网页信息
		Document document = Jsoup.connect(URL).get();
		//获取真实的数据体
		Element element = document.getElementsByTag("tbody").get(0);
		String provinceCode = "";//省级编码
		String cityCode = "";//市级编码
		if(Objects.nonNull(element)){
			Elements trs = element.getElementsByTag("tr");
			for (int i = 3; i < trs.size(); i++) {
				Elements tds = trs.get(i).getElementsByTag("td");
				if(tds.size() < 3){
					continue;
				}
				Element td1 = tds.get(1);//行政区域编码
				Element td2 = tds.get(2);//行政区域名称
				if(StringUtils.isNotEmpty(td1.text())){
					if(td1.classNames().contains("xl7030796")){
						if(td2.toString().contains("span")){
							//市级
							ChinaRegionsInfo chinaRegions = new ChinaRegionsInfo();
							chinaRegions.setCode(td1.text());
							chinaRegions.setName(td2.text());
							chinaRegions.setType(2);
							chinaRegions.setParentCode(provinceCode);
							regionsInfoList.add(chinaRegions);
							cityCode = td1.text();
						} else {
							//省级
							ChinaRegionsInfo chinaRegions = new ChinaRegionsInfo();
							chinaRegions.setCode(td1.text());
							chinaRegions.setName(td2.text());
							chinaRegions.setType(1);
							chinaRegions.setParentCode("");
							regionsInfoList.add(chinaRegions);
							provinceCode = td1.text();
						}

					} else {
						//区或者县级
						ChinaRegionsInfo chinaRegions = new ChinaRegionsInfo();
						chinaRegions.setCode(td1.text());
						chinaRegions.setName(td2.text());
						chinaRegions.setType(3);
						chinaRegions.setParentCode(StringUtils.isNotEmpty(cityCode) ? cityCode : provinceCode);
						regionsInfoList.add(chinaRegions);
					}
				}
			}
		}
		//打印结果
		System.out.println(JSONArray.toJSONString(regionsInfoList));
		regionsInfoList.forEach(x->{
			System.out.println(x.getCode()+x.getType()+x.getParentCode()+x.getName());
		});
	}

}
/*
直接载入url
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
System.out.println(document.toString());

通过文件加载文档
创建一个demo.html文件,内容如下：
<!DOCTYPE html>
<html>
 <head>
  <meta charset="utf-8">
  <title></title>
 </head>
 <body>
  Hello World!
 </body>
</html>
然后通过文件加载文档,获取文档信息
// 指定HTML文件地址,加载文档
String fileUrl = "/html/demo.html";
Document document = Jsoup.parse(new File(fileUrl), "utf-8" );
System.out.println(document.toString());

通过字符串加载文档
// 定义一个html页面的字符串信息
String html = "<html><head><title></title></head><body>Hello World</body></html>";
Document document = Jsoup.parse(html);
System.out.println(document.toString());
通过以上任意的一种方式,都可以实现HTML页面文档的加载,获取Document对象。

从HTML获取标题信息：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取页面标题
System.out.println(document.title());

从HTML获取元信息：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取元信息中的页面描述内容
String description = document.select("meta[name=description]").first().attr("content");
System.out.println("Meta description : " + description);

从HTML获取头部信息：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取页面头部标签信息
System.out.println(document.head().toString());

从HTML获取内容信息：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取页面头部标签信息
System.out.println(document.body().toString());

从HTML获取页面所有超链接：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取HTML页面中的所有链接
Elements links = document.select("a[href]");
for (Element link : links) {
    System.out.println("link : " + link.attr("href") + ",text : " + link.text());
}

从HTML获取页面所有图片：
// 直接加载百度连接,获取百度首页页面信息
Document document = Jsoup.connect("https://www.baidu.com").get();
// 获取HTML页面中的所有图片
Elements images = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
for (Element image : images) {
    String print = new StringBuilder()
            .append("src : " + image.attr("src"))
            .append(",height : " + image.attr("height"))
            .append(",width : " + image.attr("width"))
            .append(",alt : " + image.attr("alt"))
            .toString();
    System.out.println(print);
}

从HTML获取指定标签的内容：
String html = "<p><span>hello world</span></p>";
Document document = Jsoup.parse(html);
Elements elements = document.getElementsByTag("span");
System.out.println(elements.toString());

从HTML获取指定标签ID的内容：
String html = "<p><span id=\"span111\">hello world</span></p>";
Document document = Jsoup.parse(html);
Element element = document.getElementById("span111");
System.out.println(element.toString());

从HTML获取指定标签class的内容：
String html = "<p><span class=\"class111\">hello world</span></p>";
Document document = Jsoup.parse(html);
Elements elements = document.getElementsByClass("class111");
System.out.println(elements.toString());

从HTML获取指定标签属性的内容：
String html = "<p><span datafld=\"11111\">hello world</span></p>";
Document document = Jsoup.parse(html);
Elements elements = document.getElementsByAttribute("datafld");
System.out.println(elements.first().attributes().html());

从HTML获取页面元素中任意内容
// 指定HTML文件地址,加载文档
String fileUrl = "/html/demo.html";
Document document = Jsoup.parse(new File(fileUrl), "utf-8" );
// 获取table标签,一层一层的解析全部元素
Elements tables = document.body().getElementsByTag("table");
for (int i = 0; i < tables.size(); i++) {
    // 遍历行
    Elements trs = tables.get(i).getElementsByTag("tr");
    for (int j = 0; j < trs.size(); j++) {
        // 遍历列
        Elements tds = trs.get(j).getElementsByTag("td");
        for (int k = 0; k < tds.size(); k++) {
            int row = j + 1;
            int cell = k + 1;
            String print = new StringBuilder()
                    .append("第 : " + row + "行,")
                    .append("第 : " + cell + "列,")
                    .append("内容：" + tds.get(k).text())
                    .toString();
            System.out.println(print);
        }
    }
}

消除不信任的HTML(以防止XSS)
// 原始文件
String dirtyHTML = "<p><a href='http://www.baidu.com/' onclick='sendCookiesToMe()'>Link</a></p>";
// 需要清理的标签
String cleanHTML = Jsoup.clean(dirtyHTML, Whitelist.basic());
// 输出结果
System.out.println(cleanHTML);
*/
