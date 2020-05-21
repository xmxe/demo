package com.jnhouse.app.controller;

import com.jnhouse.app.bean.Menu;
import com.jnhouse.app.bean.User;
import com.jnhouse.app.utils.MD5Util;
import net.sf.json.JSONObject;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 登录
 * @author lou
 *
 */
@Controller
public class LoginController extends BaseController{
	
	@RequestMapping(value="/index")
	public ModelAndView index(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		/*deptAuthorityService.save(deptAuthority);*/
		modelAndView.setViewName("login");
		return modelAndView;
	}
	
	@RequestMapping(value="/role")
	public ModelAndView role(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("sys/dept");
		return modelAndView;
	}
	
	/**
	 * iframe跳转主页
	 * @param request
	 * @return
	 */
	@RequestMapping(value="login/main")
	public ModelAndView go_main(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("sys/main");
		return modelAndView;
	}
	
	/**
	 * 登录验证用户
	 * @param request
	 * @author lou
	 * @return
	 */
	@RequestMapping(value="login/login_in",method = RequestMethod.POST)
	@ResponseBody
	public  JSONObject login_in(HttpServletRequest request) {
		String userName = request.getParameter("login");
		String pwd = request.getParameter("pwd");
		User user = new User();
		user.setUsername(userName);
		pwd = MD5Util.getMD5(pwd);
		user.setPasswd(pwd);
		User user2 = userService.findUser(user);
		int userId = 0;
		if (null != user2) {
			request.getSession().setAttribute("user", user2);
			userId = user2.getId();
			//shiro加入身份验证
			Subject subject = SecurityUtils.getSubject();
			UsernamePasswordToken token = new UsernamePasswordToken(userName, pwd);
			try{
				subject.login(token);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
		Map<String,Object> map = new HashMap<String,Object>();  
		map.put("userId", userId);
		JSONObject jsonObject = JSONObject.fromObject(map);
		return jsonObject;
	}
	/**
	 * 登录成功跳转主页
	 * @param request
	 * @author lou
	 * @return
	 */
	@RequestMapping(value="login/home")
	public ModelAndView home(HttpServletRequest request) {
		ModelAndView modelAndView = new ModelAndView();
		User user = (User)request.getSession().getAttribute("user"); //取得session中的user
		if (null != user && null != user.getId()) {
			List<Integer> menu_ids = new ArrayList<>();
			List<Menu> menuList = new ArrayList<>();
			List<Menu> menus = menuService.getMenuByUserId(user.getId());
			for (Menu menu : menus) {
				if (!menu_ids.contains(menu.getId())) {
					menuList.add(menu);
					menu_ids.add(menu.getId());
				}
			}
			request.getSession().setAttribute("menuList", menuList);
			modelAndView.setViewName("home");
			modelAndView.addObject(user);
			modelAndView.addObject(menuList);
		}else{
			modelAndView.setViewName("login");
		}
		return modelAndView;
	}
}
