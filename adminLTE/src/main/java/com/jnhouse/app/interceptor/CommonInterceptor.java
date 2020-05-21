package com.jnhouse.app.interceptor;

import com.jnhouse.app.bean.User;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CommonInterceptor extends HandlerInterceptorAdapter{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		if (request.getRequestURI().contains("login/login_in")) {
			return true;
		}else{
			User user = (User)request.getSession().getAttribute("user");
			if (null == user) {
				 request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
				 return  false;
			}
			return true;
		}
	}

	

}
