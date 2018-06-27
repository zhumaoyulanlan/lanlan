package com.lanlan.base;

import java.io.Serializable;
import java.sql.ResultSet;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

public class BaseServiceImpl<T> implements BaseService<T> {
	
	private BaseDao<T> dao;
	
	public BaseServiceImpl() {}
	
	public BaseServiceImpl(BaseDao<T> dao) {
		this.dao=dao;
	}

	public BaseDao<T> getDao() {
		return dao;
	}

	public void setDao(BaseDao<T> dao) {
		this.dao = dao;
	}
	
	@Override
	public boolean insert(@SuppressWarnings("unchecked") T... model) {
		return dao.insert(model)>0;
	}

	@Override
	public int deleteById(Object... idsOrModels) {
		return dao.deleteById(idsOrModels);
	}

	@Override
	public boolean update(@SuppressWarnings("unchecked") T... model) {
		return dao.update(model)>0;
	}


	
	@Override
	public T selectById(Serializable id) {
		
		return dao.selectById(id);
		
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
	public int deleteById(Serializable... id) {
		return dao.deleteById(id);
	}

	@Override
	public T requestToModel(HttpServletRequest request) {
		return dao.requestToModel(request);
	}

	@Override
	public List<T> resultSetToModelList(ResultSet rs) {
		return dao.resultSetToModelList(rs);
	}



	
	
}
