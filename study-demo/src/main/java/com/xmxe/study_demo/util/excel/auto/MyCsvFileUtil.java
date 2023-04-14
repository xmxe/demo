package com.xmxe.study_demo.util.excel.auto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.xmxe.study_demo.entity.Student;

/**
 * 万能的导出excel工具
 * 导出excel时类是不确定的.User?Student?District?不确定。但是我们封装出来的函数，要足够支撑不同的类，我们自动去读取遍历list,然后导出生成文件。
 * 思路是什么：
 * ①我们从不确定的类的集合list中，取出里面的类。反射一手，拿出里面的属性名，做第一行表格行标题名称拼接。
 * ②拼接内容.因为类不确定,那么我们就采取反射把类全部字段属性作为key丢到map里面，同时把值丢到value里面。
 */
public class MyCsvFileUtil {
    public static final Logger log = LoggerFactory.getLogger(MyCsvFileUtil.class);

    public static final String FILE_SUFFIX = ".csv";
    public static final String CSV_DELIMITER = ",";
    public static final String CSV_TAIL = "\r\n";
    protected static final String DATE_STR_FILE_NAME = "yyyyMMddHHmmssSSS";
 
    /**
     * 将字符串转成csv文件
     */
    public static void createCsvFile(String savePath, String contextStr) throws IOException {
 
        File file = new File(savePath);
        // 创建文件
        file.createNewFile();
        // 创建文件输出流
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        // 将指定字节写入此文件输出流
        fileOutputStream.write(contextStr.getBytes("gbk"));
        fileOutputStream.flush();
        fileOutputStream.close();
    }
 
    /**
     * 写文件
     *
     * @param fileName
     * @param content
     */
    public static void writeFile(String fileName, String content) {
        FileOutputStream fos = null;
        OutputStreamWriter writer = null;
        try {
            fos = new FileOutputStream(fileName, true);
            writer = new OutputStreamWriter(fos, "GBK");
            writer.write(content);
            writer.flush();
        } catch (Exception e) {
            log.error("写文件异常|{}", e);
        } finally {
            if (fos != null) {
                IOUtils.closeQuietly(fos);
            }
            if (writer != null) {
                IOUtils.closeQuietly(writer);
            }
        }
    }
 
    /**
     * 构建文件名称
     * @param dataList
     * @return
     */
    public static String buildCsvFileFileName(List dataList) {
        return dataList.get(0).getClass().getSimpleName() + new SimpleDateFormat(DATE_STR_FILE_NAME).format(new Date()) + FILE_SUFFIX;
    }
 
    /**
     * 构建excel标题行名
     * @param dataList
     * @return
     */
    public static String buildCsvFileTableNames(List dataList) {
        Map<String, Object> map = toMap(dataList.get(0));
        StringBuilder tableNames = new StringBuilder();
        for (String key : map.keySet()) {
            tableNames.append(key).append(MyCsvFileUtil.CSV_DELIMITER);
        }
        return tableNames.append(MyCsvFileUtil.CSV_TAIL).toString();
    }
 
    /**
     * 构建excel内容
     * @param dataLists
     * @return
     */
    public static String buildCsvFileBodyMap(List dataLists) {
        // 不管你传什么玩意，我都给你反射一手，搞成Map
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (Object o : dataLists) {
            mapList.add(toMap(o));
        }
        // 然后利用csv格式，对着map嘎嘎一顿拼接数据
        StringBuilder lineBuilder = new StringBuilder();
        for (Map<String, Object> rowData : mapList) {
            for (String key : rowData.keySet()) {
                Object value = rowData.get(key);
                if (Objects.nonNull(value)) {
                    lineBuilder.append(value).append(MyCsvFileUtil.CSV_DELIMITER);
                } else {
                    lineBuilder.append("--").append(MyCsvFileUtil.CSV_DELIMITER);
                }
            }
            lineBuilder.append(MyCsvFileUtil.CSV_TAIL);
        }
        return lineBuilder.toString();
    }
 
    /**
     * 类转map
     * @param entity
     * @param <T>
     * @return
     */
    public static<T> Map<String, Object> toMap(T entity){
        Class<? extends Object> bean = entity.getClass();
        Field[] fields = bean.getDeclaredFields();
        Map<String, Object> map = new HashMap<>(fields.length);
        for(Field field:fields){
            try {
                if(!"serialVersionUID".equals(field.getName())){
                    String methodName = "get"+field.getName().substring(0, 1).toUpperCase()+field.getName().substring(1);
                    Method method = bean.getDeclaredMethod(methodName);
                    Object fieldValue = method.invoke(entity);
                    map.put(field.getName(),fieldValue);
                }
            } catch (Exception e) {
                log.warn("toMap() Exception={}",e.getMessage());
            }
        }
        return map;
    }

    /**
     * 测试代码
     */
    public void createCsvFileJcTest() {
        // 类不确定随便怎么传都行
        List<Student> districts = List.of(new Student(18,"tom"), new Student(18,"jerry"));
        // 存放地址&文件名
        String fileName = "C:\\Users\\xmxe\\Desktop\\"+MyCsvFileUtil.buildCsvFileFileName(districts);
        // 创建表格行标题
        String tableNames = MyCsvFileUtil.buildCsvFileTableNames(districts);
        // 创建文件
        MyCsvFileUtil.writeFile(fileName, tableNames);
        // 写入数据
        String contentBody = MyCsvFileUtil.buildCsvFileBodyMap(districts);
        // 调用方法生成
        MyCsvFileUtil.writeFile(fileName, contentBody);
    }

    /**上面的示例中导出的表头是属性名，如果正式的导出通常需要自定义表头名称，我们这里可以使用自定义注解(@JcExcelName)来完成。*/
    /**
     * 新的反射解析拿字段属性注解值函数
     * @param <T>
     * @param entity
     * @return
     */
    public static <T> List<String> resolveExcelTableName(T entity) {
        List<String> tableNamesList = new ArrayList<>();
        Class<? extends Object> bean = entity.getClass();
        Field[] fields = bean.getDeclaredFields();
        Map<String, Object> map = new HashMap<>(fields.length);
        for (Field field : fields) {
            try {
                if (!"serialVersionUID".equals(field.getName())) {
                    String tableTitleName = field.getName();
                    JcExcelName myFieldAnn = field.getAnnotation(JcExcelName.class);
                    String annName = myFieldAnn.name();
                    if (StringUtils.hasLength(annName)) {
                        tableTitleName = annName;
                    }
                    tableNamesList.add(tableTitleName);
                }
            } catch (Exception e) {
                log.warn("toMap() Exception={}", e.getMessage());
            }
        }
        return tableNamesList;
    }

    /**
     * 根据解析出来的注解值列名拼接表格标题名格式
     * @param dataList
     * @return
     */
    public static String buildCsvFileTableNamesNew(List<String> dataList) {
        StringBuilder tableNames = new StringBuilder();
        for (String name : dataList) {
            tableNames.append(name).append(MyCsvFileUtil.CSV_DELIMITER);
        }
        return tableNames.append(MyCsvFileUtil.CSV_TAIL).toString();
    }


    /**
     * 测试根据注解属性生成excel表格头效果
     */
    public static void test(){
        Student user = new Student();
        List<String> nameList = resolveExcelTableName(user);
        System.out.println(nameList.toString());
        String tableNames = buildCsvFileTableNamesNew(nameList);
        System.out.println(tableNames);

        // 类不确定随便怎么传都行
        List<Student> districts = List.of(new Student(18,"tom"), new Student(18,"jerry"));
        // 存放地址&文件名
        String fileName = "C:\\Users\\xmxe\\Desktop\\"+MyCsvFileUtil.buildCsvFileFileName(districts);
        // 创建表格行标题
        // String tableNames = MyCsvFileUtil.buildCsvFileTableNames(districts);
        // 创建文件
        MyCsvFileUtil.writeFile(fileName, tableNames);
        // 写入数据
        String contentBody = MyCsvFileUtil.buildCsvFileBodyMap(districts);
        // 调用方法生成
        MyCsvFileUtil.writeFile(fileName, contentBody);
    }
}
