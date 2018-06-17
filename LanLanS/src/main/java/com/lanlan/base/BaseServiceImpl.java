package com.lanlan.base;

import java.sql.ResultSet;
import java.util.List;

public class BaseServiceImpl<T> implements BaseService<T> {
	
	private BaseDao<T> dao;
	
	public BaseServiceImpl() {
		
	}
	
	public BaseServiceImpl(BaseDao<T> dao) {
		this.dao=dao;
	}
	
	@Override
	public boolean insert(T model) {
		return dao.insert(model)>0;
	}

	@Override
	public boolean deleteById(Object... ids) {
		return dao.deleteById(ids)>0;
	}

	@Override
	public boolean update(T model) {
		
		return dao.update(model)>0;
	}

	@Override
	public T selectById(T model) {
	
		return dao.selectById(model);
	}

	@Override
	public List<T> selectAll() {

		return dao.selectAll();
	}

	@Override
	public List<T> selectByPage(int pageindex, int pagesize) {
		return dao.selectByPage(pageindex, pagesize);
	}

	@Override
	public List<T> selectByPage(int pageindex, int pagesize, String order) {

		return dao.selectByPage(pageindex, pagesize,order);
	}

	@Override
	public String getTableName() {
		return dao.getTableName();
	}

	@Override
	public T resultSetToModel(ResultSet rs) {
		return dao.resultSetToModel(rs);
	}

	@Override
	public int getCount() {
		return dao.getCount();
	}

	@Override
	public int getCount(T model, String... fields) {
		return dao.getCount(model, fields);
	}

	public BaseDao<T> getDao() {
		return dao;
	}

	public void setDao(BaseDao<T> dao) {
		this.dao = dao;
	}
	
}
