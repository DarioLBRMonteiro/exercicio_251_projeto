package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection conn;

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}
	
	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"INSERT INTO course.seller " +
				"(Name, Email, BirthDate, BaseSalary, DepartmentId) " +
				"VALUES " +
				"(?, ?, ?, ?, ?) "	
			,  Statement.RETURN_GENERATED_KEYS );
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
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
	public void update(Seller obj) {
		PreparedStatement st = null;
		try {
			st = conn.prepareStatement(
				"UPDATE course.seller " +
				"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " +
				"WHERE Id = ?"	
			);
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
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
			st = conn.prepareStatement("DELETE FROM course.seller WHERE Id = ?");

			st.setInt(1, id);
			
			int rows = st.executeUpdate();
			
			conn.commit();
			
			if (rows == 0) {
				throw new DbException("Seller informado não encontrado.");
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
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT seller.*,department.Name as DepName " +
				"FROM course.seller " +  
				"INNER JOIN course.department ON seller.DepartmentId = department.Id " +
				"WHERE Seller.Id = ? " 					
			);
			st.setInt(1, id);
			
			rs = st.executeQuery();
			if (rs.next()) {
				
				Department dep = Instantiate.department(rs);
				
				Seller seller = Instantiate.seller(rs,dep);
				
				return seller;				
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
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT seller.*,department.Name as DepName " +
				"FROM course.seller " +
				"INNER JOIN course.department ON seller.DepartmentId = department.Id " +
				"ORDER BY Name "
			);
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = Instantiate.department(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller seller = Instantiate.seller(rs, dep);
				
				list.add(seller);
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
	
	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			st = conn.prepareStatement(
				"SELECT seller.*,department.Name as DepName " +
				"FROM course.seller " +
				"INNER JOIN course.department ON seller.DepartmentId = department.Id " +
				"WHERE DepartmentId = ? " +
				"ORDER BY Name "
			);
			st.setInt(1,department.getId());
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while(rs.next()) {
				Department dep = map.get(rs.getInt("DepartmentId"));
				
				if (dep == null) {
					dep = Instantiate.department(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
				
				Seller seller = Instantiate.seller(rs, dep);
				
				list.add(seller);
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
