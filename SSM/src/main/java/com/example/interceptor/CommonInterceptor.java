package com.example.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerInterceptor; 

public class CommonInterceptor implements HandlerInterceptor{

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
	// 	Object user = request.getSession().getAttribute("user");
	// 	if (null == user) {
	// 		request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
	// 		return false;
	// 	}
		
		return true;
	}	

}
