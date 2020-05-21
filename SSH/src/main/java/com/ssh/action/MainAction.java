package com.ssh.action;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.font.FontRenderContext;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONObject;
import com.ssh.bean.Book;
import com.ssh.core.Page;
import com.ssh.core.action.BaseAction;
import com.ssh.service.MainService;


public class MainAction extends BaseAction {
	private static final long serialVersionUID = 1L;
	
	private MainService mainService;
	
	private Page<Book> page;
	
	public void setMainService(MainService mainService) {
		this.mainService = mainService;
	}
	public Page<Book> getPage() {
		return page;
	}
	public void setPage(Page<Book> page) {
		this.page = page;
	}

	public String generate(){
		HttpServletRequest request = this.getRequest();
		HttpServletResponse response = this.getResponse();
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		String code = drawImg(output);
		request.getSession().setAttribute("code", code);	
		try {
			ServletOutputStream out = response.getOutputStream();
			response.reset();//解决getWriter() has already been called for this response报错
			output.writeTo(out);
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
			return "error";
		}
		return "success";
	}
	
	private String drawImg(ByteArrayOutputStream output){
		String code = "";
		for(int i=0; i < 4; i++){
			code += randomChar();
		}
		int width = 70;
		int height = 25;
		BufferedImage bi = new BufferedImage(width,height,BufferedImage.TYPE_3BYTE_BGR);
		Font font = new Font("Times New Roman",Font.PLAIN,20);
		Graphics2D g = bi.createGraphics();
		g.setFont(font);
		Color color = new Color(66,2,82);
		g.setColor(color);
		g.setBackground(new Color(226,226,240));
		g.clearRect(0, 0, width, height);
		FontRenderContext context = g.getFontRenderContext();
		Rectangle2D bounds = font.getStringBounds(code, context);
		double x = (width - bounds.getWidth()) / 2;
		double y = (height - bounds.getHeight()) / 2;
		double ascent = bounds.getY();
		double baseY = y - ascent;
		g.drawString(code, (int)x, (int)baseY);
		g.dispose();
		try {
			ImageIO.write(bi, "jpg", output);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return code;
	}
	
	private char randomChar(){
		Random r = new Random();
		String s = "ABCDEFGHJKLMNPRSTUVWXYZ0123456789";
		return s.charAt(r.nextInt(s.length()));
	}
	
	private JSONObject json;	
	public JSONObject getJson() {return json;}
	public void setJson(JSONObject json) {this.json = json;}
	
	public String check() {
		HttpServletRequest request = this.getRequest();
		String code = request.getParameter("code");
		String name = request.getParameter("username");
		String password = request.getParameter("password");
		String codeSession = (String) request.getSession().getAttribute("code");
		Map<String,String> map = new HashMap<>();
		Map<String,String> result = new HashMap<>();
		map.put("username", "1");map.put("password", "1");
		request.getSession().setAttribute("user",map);
		if(code.equalsIgnoreCase(codeSession)) {
			if(!("1").equals(name) || !("1").equals(password)) {
				result.put("message", "用户名或密码不正确");
			}else {
				result.put("message", "success");
			}		
		}else {
			result.put("message", "验证码不正确");
		}
		json = (JSONObject) JSONObject.toJSON(result);
		return "success";
	} 
	
	public String index() {
		return "success";
	}
	public String listBook() {
		HttpServletRequest request = this.getRequest();
		int offset = request.getParameter("offset") == null ?0:Integer.parseInt(request.getParameter("offset"));
		int limit = request.getParameter("limit") == null?10:Integer.parseInt(request.getParameter("limit"));
		String key = request.getParameter("key");
		Map<String,Object> map = new HashMap<String,Object>();
		if(!"".equals(key) && key !=null){
			map.put("key", "%"+key+"%");
		}
		try{
			page = mainService.queryBook(offset, limit, map);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	
		request.setAttribute("books", page.getRows());
		
		request.setAttribute("pagenum", page.getPageNum());
		
		request.setAttribute("totalcount", page.getTotal());
		
		request.setAttribute("totalPage", page.getTotalPage());
		
		return "success";
	}
	
	public String deleteBook() {
		HttpServletRequest request = this.getRequest();
		int id = Integer.parseInt(request.getParameter("id"));
		try
		{
			mainService.deleteBook(id);
			return SUCCESS;
		}
		catch (Exception e)
		{
			e.printStackTrace();
			String message = "删除失败!";
			request.setAttribute("message", message);
			return ERROR;
		}
	}
	public String queryBook() {
		HttpServletRequest request = this.getRequest();
		String id = request.getParameter("id");
		Book book = null;
		try{
			book = mainService.findById(Integer.parseInt(id));
		}
		catch (NumberFormatException e){
			e.printStackTrace();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
		request.setAttribute("book", book);
		
		return SUCCESS;
	}
	private Integer id; 
	private String bookname;
	private String bookauthor;
	private Float bookprice;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getBookname() {
		return bookname;
	}
	public void setBookname(String bookname) {
		this.bookname = bookname;
	}
	public String getBookauthor() {
		return bookauthor;
	}
	public void setBookauthor(String bookauthor) {
		this.bookauthor = bookauthor;
	}
	public Float getBookprice() {
		return bookprice;
	}
	public void setBookprice(Float bookprice) {
		this.bookprice = bookprice;
	}
	public MainService getMainService() {
		return mainService;
	}
	public String updateBook() {
		HttpServletRequest request = this.getRequest();
		Book book =new Book();
		book.setBookname(bookname);
		book.setBookauthor(bookauthor);
		book.setBookprice(bookprice);
		book.setId(id);
		try
		{
			mainService.updateBook(book);
			return SUCCESS;
		}
		catch (Exception e){
			e.printStackTrace();
			String message = "更新失败!";
			request.setAttribute("message", message);
			return ERROR;
		}
	}	
}
