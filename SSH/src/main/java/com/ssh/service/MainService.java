package com.ssh.service;

import java.util.Map;

import com.ssh.bean.Book;
import com.ssh.core.Page;


public interface MainService {

	public void timeTask();

	public Page<Book> queryBook(int  offset,int limit,Map<String,Object> paramMap)throws Exception;
	
	public void deleteBook(int id)throws Exception;
	
	public Book findById(int id)throws Exception;
	
	public void updateBook(Book book)throws Exception;
}
