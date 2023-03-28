package com.xmxe.study_demo.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.read.metadata.ReadSheet;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.xmxe.study_demo.util.excel.EasyExcelUtil;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 测试类
 */
public class EasyExcelTest {

    /**
     * 读取少于1000行的excle
     */
    @org.junit.Test
    public void readLessThan1000Row() {
        String filePath = "C:\\E\\file123\\日报.xlsx";
        List<Object> objects = EasyExcelUtil.readLessThan1000Row(filePath, 0);
        objects.forEach(System.out::println);
    }

    /**
     * 读取少于1000行的excle，可以指定sheet和从几行读起
     */
    @org.junit.Test
    public void readLessThan1000RowBySheet() {
        String filePath = "C:\\E\\file123\\日报.xlsx";
        ReadSheet sheet = new ReadSheet();
        List<Object> objects = EasyExcelUtil.readLessThan1000RowBySheet(filePath, sheet, 0);
        objects.forEach(System.out::println);
    }

    /**
     * 读取大于1000行的excle
     * 带sheet参数的方法可参照测试方法readLessThan1000RowBySheet()
     */
    @org.junit.Test
    public void readMoreThan1000Row() {
        String filePath = "/home/chenmingjian/Downloads/测试.xlsx";
        List<Object> objects = EasyExcelUtil.readMoreThan1000Row(filePath, 0);
        objects.forEach(System.out::println);
    }

    /**
     * 生成excle
     * 带sheet参数的方法可参照测试方法readLessThan1000RowBySheet()
     */
    @org.junit.Test
    public void writeBySimple() {
        String filePath = "C:\\Users\\wangx\\Desktop\\测试.xlsx";
        List<List<Object>> data = new ArrayList<>();
        data.add(Arrays.asList("111", "222", "333"));
        data.add(Arrays.asList("111", "222", "333"));
        data.add(Arrays.asList("111", "222", "333"));
        List<String> head = Arrays.asList("表头1", "表头2", "表头3");
        EasyExcelUtil.writeBySimple(filePath, data, head);
    }

    /**
     * 生成excle,带用模型
     * 带sheet参数的方法可参照测试方法readLessThan1000RowBySheet()
     */
    @org.junit.Test
    public void writeWithTemplate() {
        String filePath = "C:\\Users\\wangx\\Desktop\\测试2.xlsx";
        ArrayList<TableHeaderExcelProperty> data = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            TableHeaderExcelProperty tableHeaderExcelProperty = new TableHeaderExcelProperty();
            tableHeaderExcelProperty.setName("cmj" + i);
            tableHeaderExcelProperty.setAge(22 + i);
            tableHeaderExcelProperty.setSchool("清华大学" + i);
            data.add(tableHeaderExcelProperty);
        }
        EasyExcelUtil.writeWithTemplate(filePath, data);
    }

    /**
     * 生成excle,带用模型,带多个sheet
     */
    @org.junit.Test
    public void writeWithMultipleSheel() {
        ArrayList<EasyExcelUtil.MultipleSheelPropety> list1 = new ArrayList<>();
        for (int j = 1; j < 4; j++) {
            ArrayList<TableHeaderExcelProperty> list = new ArrayList<>();
            for (int i = 0; i < 4; i++) {
                TableHeaderExcelProperty tableHeaderExcelProperty = new TableHeaderExcelProperty();
                tableHeaderExcelProperty.setName("cmj" + i);
                tableHeaderExcelProperty.setAge(22 + i);
                tableHeaderExcelProperty.setSchool("清华大学" + i);
                list.add(tableHeaderExcelProperty);
            }

            WriteSheet sheet = new WriteSheet();
            sheet.setSheetName("sheet" + j);

            EasyExcelUtil.MultipleSheelPropety multipleSheelPropety = new EasyExcelUtil.MultipleSheelPropety();
            multipleSheelPropety.setData(list);
            multipleSheelPropety.setWriteSheet(sheet);

            list1.add(multipleSheelPropety);

        }

        EasyExcelUtil.writeWithMultipleSheel("C:\\Users\\wangx\\Desktop\\test.xlsx", list1);

    }

    /******************* 匿名内部类，实际开发中该对象要提取出去 **********************/

    @EqualsAndHashCode
    @Data
    public static class TableHeaderExcelProperty {

        /**
         * value: 表头名称
         * index: 列的号, 0表示第一列
         */
        @ExcelProperty(value = "姓名", index = 0)
        private String name;

        @ExcelProperty(value = "年龄", index = 1)
        private int age;

        @ExcelProperty(value = "学校", index = 2)
        private String school;
    }


}
