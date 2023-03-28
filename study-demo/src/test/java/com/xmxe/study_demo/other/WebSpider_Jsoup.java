package com.xmxe.study_demo.other;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

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
		String[] timeArr = null;// 时间
		String[] qwArr = null;// 气温
		String[] windArr = null;// 风速
		String[] qyArr = null;// 气压
		String[] xdsdArr = null;// 相对湿度
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
			os.close();
			wb.close();
		}
	}

}
