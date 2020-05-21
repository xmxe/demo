package com.jnhouse.app.controller;


import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.jnhouse.app.bean.Dept;
import com.jnhouse.app.bean.SupAnswerHeader;
import com.jnhouse.app.bean.SupTemplate;
import com.jnhouse.app.bean.User;
import com.jnhouse.app.dto.TemAnswer;
import com.jnhouse.app.service.AnswerService;
import com.jnhouse.app.service.DeptService;
import com.jnhouse.app.service.DeptUserService;
import com.jnhouse.app.service.SupTemplateService;

import net.sf.json.JSONObject;


@Controller
@RequestMapping("/answer")
public class AnswerController {
	@Autowired
	AnswerService answerService;
	
	@Autowired
	SupTemplateService templateService;
	
	@Autowired
	DeptUserService deptUserService;
	
	@Autowired
	DeptService deptService;
	
	@RequestMapping(value="/answer")
	public ModelAndView to_answer(HttpServletRequest request,HttpServletResponse response) {
		return new ModelAndView("inspection/answer");
	}
	
	//获取答案主表数据
    @RequestMapping(value="/selectAnswer")
    @ResponseBody
    public List<SupAnswerHeader> selectAnswer(HttpServletRequest request,HttpServletResponse response) {
    	Map<String,Object> map = new HashMap<>();
        List<SupAnswerHeader> answer = answerService.selectAnswer(map);
        return answer;
    }
    //获取模板下的子节点
    @RequestMapping(value="/answerList")
    @ResponseBody
    public List<SupTemplate> answerList(HttpServletRequest request,HttpServletResponse response) {
    	String id = request.getParameter("template_id");
    	Map<String,Object> map = new HashMap<>();
    	map.put("parent_id", id);
        List<SupTemplate> temp = templateService.fke_template(map);
        return temp;
    }
    //获取答案明细
    @RequestMapping(value="/temAnswer")
    @ResponseBody   
    public List<TemAnswer> temAnswer(HttpServletRequest request,HttpServletResponse response) {
    	String id = request.getParameter("template_id");
    	String header_id = request.getParameter("header_id");
    	Map<String,Object> map = new HashMap<>();
    	map.put("parent_id", id);
    	map.put("header_id", header_id);
    	
    	//判断是否有子模板
    	List<SupTemplate> template = templateService.isHaveLevelCount(id);
    	for(SupTemplate ty : template) {
    		int templId = ty.getId();
    		List<SupTemplate> late = templateService.isHaveLevelCount(String.valueOf(templId));
    		if(late.size() > 0) {
    			map.put("levelCount", "level");  
    			break;
    		}   		
    	}  	
    	return answerService.temAnswer(map);
    }
    //共享
    @RequestMapping(value="/share")
    @ResponseBody
    public void share(HttpServletRequest request,HttpServletResponse response) {
    	String header_id = request.getParameter("header_id");//获取要分享的header_id
    	String dept_id = request.getParameter("dept_id");//获取部门id
    	Map<String,Object> par = new HashMap<>();
    	JSONObject json = new JSONObject();
    	par.put("header_id", header_id);
    	//获取该部门及下属部门id
    	List<Integer> depts = deptService.getDeptById(dept_id);
    	try {
    		PrintWriter out = response.getWriter();		   		
    	   		par.put("dept_id", depts);
    	    	//判断有没有共享过，0为没有共享   
    	   		int isHave = answerService.getHeader_dept(par);
    	    	if(isHave == 0) {    	   			   	   			
	    	   		answerService.shareByheaderId(par);	  
	    	   		json.put("success","成功共享给此部门");
    	    	}  else {
    	    		json.put("success","部门已共享或部门下有已共享过的部门，请重新选择");
    	    	}  	   	
    		
   			out.println(json);
    		out.flush();
    		out.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    }
    @RequestMapping(value="/isHave")
    @ResponseBody
    public List isHave(HttpServletRequest request,HttpServletResponse response) {
    	String id = request.getParameter("id");
    	
    	//判断模板下是否还有子项，0位没有
    	List<SupTemplate> levelCount = templateService.isHaveLevelCount(id);
    	
    	return levelCount;
    }
    
    @RequestMapping(value="/deleteHeader")
	//@RequiresPermissions("user:add")
	@RequiresRoles("a")
    @ResponseBody
    public void deleteHeader(HttpServletRequest request,HttpServletResponse response) {
    	String header_id = request.getParameter("id");
    	JSONObject json = new JSONObject();
    	try {
    		PrintWriter out = response.getWriter();
    		answerService.deleteHeader(header_id);
    		json.put("success", true);
    		out.println(json);
    		out.flush();
    		out.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
    
    @RequestMapping(value="/deptshare")
	public ModelAndView to_deptshare(HttpServletRequest request,HttpServletResponse response) {
		return new ModelAndView("inspection/deptshare");
	}
    
  //获取分享后的答案主表数据
    @RequestMapping(value="/selectDeptShareAnswer")
    @ResponseBody
    public List<SupAnswerHeader> selectDeptShareAnswer(HttpServletRequest request,HttpServletResponse response) {
    	Map<String,Object> map = new HashMap<>(); 
        //查询当前用户所属部门
    	User user = (User)request.getSession().getAttribute("user");
        Integer userId = user.getId();
        List<Integer> dept_ids = deptUserService.getDeptIdByUserId(userId);  
        map.put("dept_id", dept_ids);
        List<SupAnswerHeader> answer = answerService.selectDeptShareAnswer(map);       
        return answer;
    	
    }
    
    @RequestMapping(value="/deleteDeptHeader")
    @ResponseBody
    public void deleteDeptHeader(HttpServletRequest request,HttpServletResponse response) {
    	String id = request.getParameter("id");
    	String dept_id = request.getParameter("dept_id");
    	String header_id = request.getParameter("header_id");
    	Map<String,Object> param = new HashMap<>();
    	param.put("id", id);
    	param.put("header_id", header_id);
    	param.put("dept_id", dept_id);
    	JSONObject json = new JSONObject();
    	try {
    		PrintWriter out = response.getWriter();
    		answerService.deleteDeptHeader(param);
    		json.put("success", true);
    		out.println(json);
    		out.flush();
    		out.close();
    	}catch(Exception e) {
    		e.printStackTrace();
    	}
    	
    }
}
