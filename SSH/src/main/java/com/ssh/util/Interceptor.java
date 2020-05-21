package com.ssh.util;

import java.util.Map;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;

import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;


public class Interceptor extends MethodFilterInterceptor{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    
	protected String doIntercept(ActionInvocation invocation) throws Exception {
		//取得请求相关的ActionContext实例
		ActionContext ctx = invocation.getInvocationContext();
		Map<String,Object> session = ctx.getSession();
		//取出名为user的Session属性
		Map<String,String> user = (Map<String, String>) session.get("user");
		//如果用户为null 返回重新登陆
		if (user != null ){
			return invocation.invoke();
		}
		//没有登陆，将服务器提示设置成一个HttpServletRequest属性
		ctx.put("tip" , "您还没有登陆");
		//直接返回login的逻辑视图
		return "login";
	}
}
