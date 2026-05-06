package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	private Connection conn;
	
	public DepartmentDaoJDBC(Connection conn) {
		this.conn = conn;
	}

	@Override
	public void insert(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
					"INSERT INTO course.department (Name) VALUES (?) ", Statement.RETURN_GENERATED_KEYS 
			);
			st.setString(1, obj.getName());
			
			int rowsAffected = st.executeUpdate();

			conn.commit();
						
			if (rowsAffected > 0) {
				ResultSet rs = st.getGeneratedKeys();
				if (rs.next()) {
					int id = rs.getInt(1);					
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}
			else {
				throw new DbException("Unexpected error! No rows affected !");
			}
		}
		catch(SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Transaction rolled back ! Cause by: " + e.getMessage());
			}
			catch(SQLException e1) {
				throw new DbException("Error trying to rollback ! Cause by: " + e1.getMessage());
			}
		}
		finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void update(Department obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement("UPDATE course.department SET Name = ? WHERE Id = ?");
			st.setString(1, obj.getName());
			st.setInt(2, obj.getId());
			
			st.executeUpdate();
			
			conn.commit();
		}
		catch(SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Transaction rolled back ! Cause by: " + e.getMessage());
			}
			catch(SQLException e1) {
				throw new DbException("Error trying to rollback ! Cause by: " + e1.getMessage());
			}
		}
		finally {
			DB.closeStatement(st);
		}		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement st = null; 
		try {
			st = conn.prepareStatement("DELETE FROM course.department WHERE Id = ?");

			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			
			conn.commit();
			
			if (rows == 0) {
				throw new DbException("The department mentioned could not be found.");
			}
		}
		catch(SQLException e) {
			try {
				conn.rollback();
				throw new DbException("Transaction rolled back ! Cause by: " + e.getMessage());
			}
			catch(SQLException e1) {
				throw new DbException("Error trying to rollback ! Cause by: " + e1.getMessage());
			}
		}
		finally {
			DB.closeStatement(st);
		}
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT department.* " +
				"FROM course.department " + 
				"WHERE Id = ? "			
			);
			st.setInt(1, id);
			
			rs = st.executeQuery();
			
			if (rs.next()) {
				Department dep = Instantiate.department(rs);				
				
				return dep;								
			}
			return null;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage()); 
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT department.* " +
				"FROM course.department " 
			);
			
			rs = st.executeQuery();
			
			List<Department> list = new ArrayList<>();
			
			while (rs.next()) {
				Department dep = Instantiate.department(rs);				
				
				list.add(dep);				
			}
			return list;
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage()); 
		}
		finally {
			DB.closeStatement(st);
			DB.closeResultSet(rs);
		}		
	}
}
