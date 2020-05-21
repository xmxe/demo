package com.ssh.service;

import java.util.Map;

import com.ssh.bean.Book;
import com.ssh.core.Page;
import com.ssh.dao.MainDao;


public class MainServiceImpl implements MainService{
	
	private MainDao maindao;
	
	public MainDao getMaindao() {
		return maindao;
	}
	public void setMaindao(MainDao maindao) {
		this.maindao = maindao;
	}

	public void timeTask(){
		System.out.println("定时执行了timeTask方法");
	}
	@Override
	public Page<Book> queryBook(int offset, int limit, Map<String, Object> paramMap) throws Exception {
		Page<Book> page = new Page<Book>();
		page.setRows(maindao.queryBook( offset, limit, paramMap));
		int total = maindao.queryBookCount( paramMap);
		page.setTotal(total);
		int totalpage = total/limit==0 ? total/limit : total/limit+1;
		page.setTotalPage(totalpage);
		int pagenum = (offset/limit)+1;
		if(pagenum<1) 
			pagenum = 1;
		if(pagenum>totalpage)
			pagenum = totalpage;
		page.setPageNum(pagenum);
		
		return page;
	}
	@Override
	public void deleteBook(int id) throws Exception {
		maindao.delete(id);
	}
	@Override
	public Book findById(int id) throws Exception {
		return maindao.findById(id);
	}
	@Override
	public void updateBook(Book book) throws Exception {		
		maindao.update(book);		
	}
}
