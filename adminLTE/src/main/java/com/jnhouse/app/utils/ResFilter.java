package com.jnhouse.app.utils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

public class ResFilter implements Filter{  
  
       
    private final static double DEFAULT_USERID= Math.random()*100000.0;    
  
    public void destroy() {  
    }  
  
    public void doFilter(ServletRequest request, ServletResponse response,  
           FilterChain chain) throws IOException, ServletException {  
       HttpServletRequest req=(HttpServletRequest)request;  
        HttpSession session= req.getSession();  
        /*if (session==null){
            MDC.put("userId",DEFAULT_USERID);
        }  
        else{  
            User customer=(User)session.getAttribute("user");  
            if (customer==null){  
                MDC.put("userId",DEFAULT_USERID);  
                MDC.put("userName",DEFAULT_USERID);  
            }  
            else  
            {  
                MDC.put("userId",customer.getUsername());  
                MDC.put("userName",customer.getUsername());  
            }
        }  */
        //logger.info("test for MDC.");  
  
       chain.doFilter(request, response);  
    }  
    public void init(FilterConfig Config) throws ServletException {  
//     this.filterConfig = Config;  
//     String ccc = Config.getServletContext().getInitParameter("cherset");  
//     this.targetEncoding = Config.getInitParameter("cherset");  
  
    }  
}  