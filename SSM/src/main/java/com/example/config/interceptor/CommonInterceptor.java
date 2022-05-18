package com.example.config.interceptor;

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

	@Override
	public void postHandle(javax.servlet.http.HttpServletRequest request,
							javax.servlet.http.HttpServletResponse response,
							java.lang.Object handler,
							org.springframework.web.servlet.ModelAndView modelAndView)throws java.lang.Exception {

	}

	public void afterCompletion(javax.servlet.http.HttpServletRequest request,
								javax.servlet.http.HttpServletResponse response,
								java.lang.Object handler, java.lang.Exception ex) throws java.lang.Exception {
	}



}
