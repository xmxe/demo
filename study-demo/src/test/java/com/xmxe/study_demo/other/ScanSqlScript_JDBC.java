package com.xmxe.study_demo.other;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.VerticalAlignment;

public class ScanSqlScript_JDBC {
	private static final String[] column_type = { "int", "bigint", "float", "char", "varchar", "nvarchar", "double","date", "datetime" };
	private static final String url = "jdbc:sqlserver://10.37.169.200:1433;DatabaseName=gsipV3";
	private static final String user = "sa";
	private static final String pass = "abc/123";
	public static Connection getConnection() {
		Connection conn = null;
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");			
			conn = DriverManager.getConnection(url, user, pass);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	public static String getProperties(String key){
		Properties pe = new Properties();
		InputStream is = ScanSqlScript_JDBC.class.getClassLoader().getResourceAsStream("db.properties");
		String value = "";
		try {
			pe.load(is);
			value = pe.getProperty(key);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return value;
	}

	public static Map<String,List<String>> getColumnData() {
		Connection conn = getConnection();
		Map<String,List<String>> dataMap = new HashMap<>();
		try {
			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rsTable = meta.getTables(null, null, null, new String[] { "TABLE" });
			//ResultSet rsMetaData = meta.getTables("zhongzhu", "root", "%", new String[] { "TABLE" });
			PreparedStatement stmt = null;
			ResultSet rs = null;
			
			while (rsTable.next()) {
				//System.out.println("表名：" + rs1.getString(3));
				String sql = "select * from  "+ rsTable.getString(3) + " where 1 = 2";
				
				try{
					stmt = conn.prepareStatement(sql);
					//stmt.setString(1, rs1.getString(3));
					rs = stmt.executeQuery();
					
					ResultSetMetaData data = rs.getMetaData();
					List<String> columnList = new ArrayList<>();
					for (int i = 1; i <= data.getColumnCount(); i++) {
						// 获得所有列的数目及实际列数
						// int columnCount = data.getColumnCount();
						// 获得指定列的列名
						String columnName = data.getColumnName(i);				
						/*
						int columnType = data.getColumnType(i); // 获得指定列的列值				
						String columnTypeName = data.getColumnTypeName(i); // 获得指定列的数据类型名 
						String catalogName = data.getCatalogName(i);//所在的Catalog名字 
						String columnClassName = data.getColumnClassName(i); // 对应数据类型的类 
						int columnDisplaySize = data.getColumnDisplaySize(i); // 在数据库中类型的最大字符个数 
						String columnLabel = data.getColumnLabel(i); //默认的列的标题 
						String schemaName = data.getSchemaName(i);//获得列的模式 
						int precision = data.getPrecision(i); //某列类型的精确度(类型的长度)
						int scale = data.getScale(i);  //小数点后的位数
						String tableName = data.getTableName(i); // 获取某列对应的表名
						boolean isAutoInctement = data.isAutoIncrement(i); // 是否自动递增
						boolean isCurrency = data.isCurrency(i); // 在数据库中是否为货币型 
						int isNullable = data.isNullable(i); // 是否为空
						isReadOnly = data.isReadOnly(i); // 是否为只读 boolean
						isSearchable = data.isSearchable(i);// 能否出现在where中 boolean
						 */
						//System.out.println("获得列" + i + "的字段名称:" + columnName);
						columnList.add(columnName);
					}
					dataMap.put("dbo."+rsTable.getString(3), columnList);
				}catch(Exception e ){
					break;
				}				
				
			}
			rsTable.close();
			rs.close();
			stmt.close();
			conn.close();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return dataMap;
	}

	public static Map<String,List<String>> readFileByLines(String fileName) {
		File file = new File(fileName);
		BufferedReader reader = null;
		String str = "";
		// 表示一个或多个空格的正则表达式
		String regex = "\\s+";
		StringBuffer bu = new StringBuffer();
		Map<String,List<String>> database = new HashMap<>();
		try {
			reader = new BufferedReader(new FileReader(file));
			String tempString = null;
			// 一次读入一行，直到读入null为文件结束
			while ((tempString = reader.readLine()) != null) {
				bu = bu.append(tempString);
			}
			//变成长字符串
			str = bu.toString().trim();
			// 去掉所有的空格
			String strNotrim = str.replaceAll(regex, " ");
			String strCreateTable = strNotrim.substring(strNotrim.indexOf("create table"));
			String[] strCreateTableArr = strCreateTable.split("create table");
			for (String strTable : strCreateTableArr) {
				if (strTable.contains("primary key")) {
					strTable = strTable.substring(0, strTable.indexOf("primary"));
					String tableName = strTable.substring(0, strTable.indexOf("("));
					List<String> columnList = new ArrayList<>();
					String[] strColumn = strTable.split(",");
					for (String column : strColumn) {
						String pa = "";
						for (String type : column_type) {
							Pattern p = Pattern.compile("(?<= )(.*?)(?= " + type + ")");
							Matcher m = p.matcher(column);
							if (m.find()) {
								pa = m.group();
								if (!pa.contains(" "))
									break;
								else
									continue;
							}

						}
						if(pa.contains("(")){
							pa = pa.substring(pa.indexOf("(")+1);
						}
						if(pa.contains("\"")){
							Pattern p1 = Pattern.compile("(?<=\")(.*?)(?=\")");
							Matcher m1 = p1.matcher(pa);
							if(m1.find())
								pa = m1.group();
						}
						if(pa.equals("")){
							continue;
						}
						columnList.add(pa.trim());
					}
					database.put(tableName.trim(), columnList);
				}

			}
			//System.err.println(database);
			reader.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e1) {
				}
			}
		}
		return database;
	}

	public static void writeExcel(Map<String,List<String>> map1 ,Map<String,List<String>> map2) throws IOException{
		String[] handers = {"sqlserver","powerdesigner"};
		HSSFWorkbook wb = new HSSFWorkbook();//创建工作簿
		HSSFSheet sheet = wb.createSheet("操作");//第一个sheet
		HSSFRow rowFirst = sheet.createRow(0);//第一个sheet第一行为标题
		rowFirst.setHeight((short) 500);			   
        HSSFCellStyle cellStyle = wb.createCellStyle();// 创建单元格样式对象  
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);       
		
		//写标题了
		 for (int i = 0; i < handers.length; i++) {
			 sheet.setColumnWidth((short) i, (short) 10000);// 设置列宽
			 //获取第一行的每一个单元格
			 HSSFCell cell = rowFirst.createCell(i+1);
			 //往单元格里面写入值
			 cell.setCellValue(handers[i]);
			 cell.setCellStyle(cellStyle);
		 }		
		 Set<Map.Entry<String,List<String>>> set1 = map1.entrySet();
		 Set<Map.Entry<String,List<String>>> set2 = map2.entrySet();
		 int count = 0;
		 for(Iterator<Map.Entry<String,List<String>>> car1  = set1.iterator();car1.hasNext(); ){
			 Map.Entry<String,List<String>> data1 = car1.next();
			 String t1 = data1.getKey();
			 List<String> l1 = data1.getValue();
			 String l1str = "";
			 if(l1.size()>0){
				 for(String tem : l1){
					 l1str = l1str+tem+",";
				 }
				 
			 }	
			 count ++;
			//创建数据行
			HSSFRow row = sheet.createRow(count);				
			row.setHeight((short) 400);   // 设置每行的高度
			//设置对应单元格的值
										
			row.createCell(0).setCellValue(t1);
			row.createCell(1).setCellValue(l1str);
				
		 }
		 int count2 = 0;
		 for(Iterator<Map.Entry<String,List<String>>> car2  = set2.iterator();car2.hasNext(); ){
			 Map.Entry<String,List<String>> data2 = car2.next();
//			 String t2 = data2.getKey();
			 List<String> l2 = data2.getValue();
			 String l1str = "";
			 if(l2.size()>0){
				 for(String tem : l2){
					 l1str = l1str+tem+",";
				 }
				 
			 }	
			 count2 ++;
			//创建数据行
			HSSFRow row = sheet.getRow(count2);				
			row.setHeight((short) 400);   // 设置每行的高度
			//设置对应单元格的值
										
			//row.createCell(0).setCellValue(t2);
			row.createCell(2).setCellValue(l1str);
				
		 }
		 for(int i = 0;i<map1.size();i++){
			 sheet.autoSizeColumn((short)i);//自动设置列宽
		 }
		 
		 OutputStream os = null;
		 try{
			 String name = String.valueOf(new Date().getTime())+".xls";
			 os = new FileOutputStream(new File("C:\\Users\\admin\\Desktop\\新建文件夹\\"+name)); 
			 wb.write(os);
		}catch(Exception e){
			 e.printStackTrace();
		} finally{        
			os.close();
			wb.close();
		 }
	
	}
	
	public static void main(String[] args) throws SQLException, IOException {
		Map<String,List<String>> map1 = new HashMap<>();
		Map<String,List<String>> map2 = new HashMap<>();
		Map<String,List<String>> dataMap1 = getColumnData();
		//System.err.println(dataMap1);
		Map<String,List<String>> dataMap2 = readFileByLines("C:\\Users\\admin\\Desktop\\crebas.sql");
		//System.err.println(dataMap2);
		Set<Map.Entry<String,List<String>>> set1 = dataMap1.entrySet();
		Set<Map.Entry<String,List<String>>> set2 = dataMap2.entrySet();
		for(Iterator<Map.Entry<String,List<String>>> car1  = set1.iterator();car1.hasNext(); ){
			Map.Entry<String,List<String>> val1 = car1.next();
			String tableName1 = val1.getKey();
			List<String> column1 = val1.getValue();
			for(Iterator<Map.Entry<String,List<String>>> car2  = set2.iterator();car2.hasNext(); ){
				Map.Entry<String,List<String>> val2 = car2.next();
				String tableName2 = val2.getKey();
				List<String> column2 = val2.getValue();
				if(tableName1.equals(tableName2)){
					List<String> column3 = new ArrayList<>();
					column3.addAll(column1);
					List<String> column4 = new ArrayList<>();
					column4.addAll(column2);
					
					column1.removeAll(column2);
					//System.err.println("数据库："+tableName1+"多了"+column1);
					column4.removeAll(column3);
					//System.err.println("powerdesigner："+tableName2+"多了"+column4);
					map1.put(tableName1, column1);
					map2.put(tableName2, column4);
				}
			}
		}
		System.err.println(map1);
		System.err.println(map2);
		writeExcel(map1,map2);
	}

}
