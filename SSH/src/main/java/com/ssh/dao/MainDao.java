package com.ssh.dao;

import java.util.List;
import java.util.Map;

import com.ssh.bean.Book;
import com.ssh.core.dao.BaseHibernateDao;


public interface MainDao extends BaseHibernateDao<Book>{
	public List<Book> queryBook(int  offset,int limit,Map<String,Object> paramMap)throws Exception ;
	public int queryBookCount(Map<String,Object> paramMap)throws Exception ;
	public void delete(int  id)throws Exception ;
	public Book findById(int id)throws Exception ;
	public void update(Book book)throws Exception ;
}
