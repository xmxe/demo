package com.xmxe.study_demo.util;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/** 
 * 数据库连接类 
 * 说明:封装了无参，有参，存储过程的调用 
 */  
public class JDBCUtil {  
  
    /** 
     * 数据库驱动类名称 
     */  
    private static final String DRIVER = "com.microsoft.sqlserver.jdbc.SQLServerDriver";  
  
    /** 
     * 连接字符串 
     */  
    private static  String url = null;
  
    /** 
     * 用户名 
     */  
    private static  String name = null;  
  
    /** 
     * 密码 
     */  
    private static  String passw = null; 
  
    /** 
     * 创建数据库连接对象 
     */  
    private Connection connnection = null;  
  
    /** 
     * 创建PreparedStatement对象 
     */  
    private PreparedStatement preparedStatement = null;  
      
    /** 
     * 创建CallableStatement对象 
     */  
    private CallableStatement callableStatement = null;  
  
    /** 
     * 创建结果集对象 
     */  
    private ResultSet resultSet = null;  
  
    static {  
        try {  
        	url = PropertiesUtil.readValue("jdbc.url");
        	name = PropertiesUtil.readValue("jdbc.name");
        	passw = PropertiesUtil.readValue("jdbc.passw");
            Class.forName(DRIVER);  
        } catch (ClassNotFoundException e) {  
            System.out.println("加载驱动错误");  
            System.out.println(e.getMessage());  
        }  
    }  
  
    public JDBCUtil() {
    	connnection = getConnection();
    }
    /** 
     * 建立数据库连接 
     * @return 数据库连接 
     */  
    public Connection getConnection() {  
    	Connection connn = null;
        try {  
            // 获取连接  
        	connn = DriverManager.getConnection(url, name,  
                    passw);  
        } catch (SQLException e) {  
           e.printStackTrace();
        }  
        return connn;  
    }  
  
    /** 
     * insert update delete SQL语句的执行的统一方法 
     * @param sql SQL语句 
     * @param params 参数数组，若没有参数则为null 
     * @return 受影响的行数 
     */  
    public int executeUpdate(String sql, Object[] params) {  
        // 受影响的行数  
        int affectedLine = 0;  
          
        try {  
            // 获得连接  
            connnection = this.getConnection();  
            // 调用SQL   
            preparedStatement = connnection.prepareStatement(sql);  
              
            // 参数赋值  
            if (params != null) {  
            	System.err.println(params.length);
                for (int i = 0; i < params.length; i++) {  
                    preparedStatement.setObject(i + 1, params[i]);  
                }  
            }  
              
            // 执行  
           // affectedLine = preparedStatement.executeUpdate();  
  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            // 释放资源  
            closeAll();  
        }  
        return affectedLine;  
    }  
  
    /** 
     * SQL 查询将查询结果直接放入ResultSet中 
     * @param sql SQL语句 
     * @param params 参数数组，若没有参数则为null 
     * @return 结果集 
     */  
    private ResultSet executeQueryRS(String sql, Object[] params) {  
        try {  
            // 获得连接  
            connnection = this.getConnection();  
              
            // 调用SQL  
            preparedStatement = connnection.prepareStatement(sql);  
            // 参数赋值  
            if (params != null) {  
                for (int i = 0; i < params.length; i++) {  
                    preparedStatement.setObject(i + 1, params[i]);  
                }  
            }  
              
            // 执行  
            resultSet = preparedStatement.executeQuery();  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        }   
  
        return resultSet;  
    }  
      
    /** 
     * SQL 查询将查询结果：一行一列 
     * @param sql SQL语句 
     * @param params 参数数组，若没有参数则为null 
     * @return 结果集 
     */  
    public Object executeQuerySingle(String sql, Object[] params) {  
        Object object = null;  
        try {  
            // 获得连接  
            connnection = this.getConnection();  
              
            // 调用SQL  
            preparedStatement = connnection.prepareStatement(sql);  
              
            // 参数赋值  
            if (params != null) {  
                for (int i = 0; i < params.length; i++) {  
                    preparedStatement.setObject(i + 1, params[i]);  
                }  
            }  
              
            // 执行  
            resultSet = preparedStatement.executeQuery();  
  
            if(resultSet.next()) {  
                object = resultSet.getObject(1);  
            }  
              
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            closeAll();  
        }  
  
        return object;  
    }  
  
    /** 
     * 获取结果集，并将结果放在List中 
     *  
     * @param sql  SQL语句 
     * @return List  结果集 
     */  
    public List<Object> excuteQueryObj(String sql, Object[] params) {  
        // 执行SQL获得结果集  
        ResultSet rs = executeQueryRS(sql, params);  
          
        // 创建ResultSetMetaData对象  
        ResultSetMetaData rsmd = null;  
          
        // 结果集列数  
        int columnCount = 0;  
        try {  
            rsmd = rs.getMetaData();  
            // 获得结果集列数  
            columnCount = rsmd.getColumnCount();  
        } catch (SQLException e1) {  
            System.out.println(e1.getMessage());  
        }  
  
        // 创建List  
        List<Object> list = new ArrayList<Object>();  
  
        try {  
            // 将ResultSet的结果保存到List中  
            while (rs.next()) {  
                Map<String, Object> map = new HashMap<String, Object>();  
                for (int i = 1; i <= columnCount; i++) {
                    if(rsmd != null)
                        map.put(rsmd.getColumnLabel(i), rs.getObject(i));  
                }  
                list.add(map);  
            }  
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            // 关闭所有资源  
            closeAll();  
        }  
        return list;  
    }  
     
    @SuppressWarnings("unchecked")
    public List<Map<String,Object>> excuteQueryMap(String sql, Object[] params){
    	List<Object> objs = excuteQueryObj(sql, params);
    	List<Map<String,Object>> res = new ArrayList<Map<String,Object>>(objs.size());
    	for(Object o:objs){
    		res.add((Map<String,Object>)o);
    	}
    	objs = null;
    	return res;
    }
    
    /** 
     * 存储过程带有一个输出参数的方法 
     * @param sql 存储过程语句 
     * @param params 参数数组 
     * @param outParamPos 输出参数位置 
     * @param SqlType 输出参数类型 
     * @return 输出参数的值 
     */  
    public Object excuteQuery(String sql, Object[] params,int outParamPos, int SqlType) {  
        Object object = null;  
        connnection = this.getConnection();  
        try {  
            // 调用存储过程  
            callableStatement = connnection.prepareCall(sql);  
              
            // 给参数赋值  
            if(params != null) {  
                for(int i = 0; i < params.length; i++) {  
                    callableStatement.setObject(i + 1, params[i]);  
                }  
            }  
              
            // 注册输出参数  
            callableStatement.registerOutParameter(outParamPos, SqlType);  
              
            // 执行  
            callableStatement.execute();  
              
            // 得到输出参数  
            object = callableStatement.getObject(outParamPos);  
              
        } catch (SQLException e) {  
            System.out.println(e.getMessage());  
        } finally {  
            // 释放资源  
            closeAll();  
        }  
          
        return object;  
    }  
  
    /**
     * 
     * @description 更新数据
     * @param sql
     * @param params
     * @return
     */
    public int excuteUpdate(String sql,Object[] params){
    	int num = 0;
    	 // 获得连接  
        connnection = this.getConnection();  
        // 调用SQL  
        try {
			preparedStatement = connnection.prepareStatement(sql);
			// 参数赋值  
            if (params != null) {  
                for (int i = 0; i < params.length; i++) {  
                    preparedStatement.setObject(i + 1, params[i]);  
                }  
            } 
            num = preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return num;  
    }
    /** 
     * 关闭所有资源 
     */  
    private void closeAll() {  
        // 关闭结果集对象  
        if (resultSet != null) {  
            try {  
                resultSet.close();  
            } catch (SQLException e) {  
                System.out.println(e.getMessage());  
            }  
        }  
  
        // 关闭PreparedStatement对象  
        if (preparedStatement != null) {  
            try {  
                preparedStatement.close();  
            } catch (SQLException e) {  
                System.out.println(e.getMessage());  
            }  
        }  
          
        // 关闭CallableStatement 对象  
        if (callableStatement != null) {  
            try {  
                callableStatement.close();  
            } catch (SQLException e) {  
                System.out.println(e.getMessage());  
            }  
        }  
  
        // 关闭Connection 对象  
        if (connnection != null) {  
            try {  
                connnection.close();  
            } catch (SQLException e) {  
                System.out.println(e.getMessage());  
            }  
        }     
    }  
    
    public static void main(String[] args) {
    	JDBCUtil db = new JDBCUtil();
		String find_dc_sql = "select w.dc_code,w.city,t.real_tag " +
				"from gs_job_index ji,gs_groups g,gs_gu_index_tag git,weather_dcCity w,gs_tags t " +
				"where ji.index_id = git.index_id and git.gu_id = g.id " +
				"and git.type = 1 and g.pid != 0 and g.code = w.dc_code " +
				"and git.tag_id = t.id and git.system_id = 1 " +
				"and ji.job_code = 'Hot_ForecastUploadJob' " +
				"and ji.note = 'gr'" +
				"and git.gu_id in (2078,2067,2056)";//
		//,2080,2056
		List<Map<String,Object>> dcs = db.excuteQueryMap(find_dc_sql, new Object[]{});
		System.out.println(dcs);
	}
} 