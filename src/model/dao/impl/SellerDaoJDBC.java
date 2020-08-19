package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void update(Seller obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteById(Integer id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Seller findById(Integer id) {
		
		PreparedStatement ps = null;
		ResultSet rs= null;
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.departmentid = department.id "
					+ "WHERE seller.id = ? ");
			ps.setInt(1, id);
			rs = ps.executeQuery();
			
			//Posi��o zero n�o tem, colocando next() vai verirficar as outras posi�oes e ver se tem algum conte�do
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);
				Seller seller = instantiateSeller(rs, dep);
				return seller;
			}
			return null;
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
		
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller seller = new  Seller();
		seller.setId(rs.getInt("id"));
		seller.setName(rs.getString("name"));
		seller.setEmail(rs.getString("email"));
		seller.setBirthDate(rs.getDate("birthdate"));
		seller.setBaseSalary(rs.getDouble("basesalary"));
		seller.setDepartment(dep);
		return seller;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("departmentid"));
		dep.setName(rs.getString("depname"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement ps = null;
		ResultSet rs= null;
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*, department.name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.departmentid = department.id "
					+ "ORDER BY id, name");
		
			rs = ps.executeQuery();
			
			List<Seller> lista = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			//Posi��o zero n�o tem, colocando next() vai veririficar as outras posi�oes e ver se tem algum conte�do
			while(rs.next()) {
				
				//Precisamos de apenas umas instancia do Department
				// Se ainda n�o existir o Map vai retornar NUll
				Department dep = map.get(rs.getInt("departmentid"));
				
				if(dep == null) {
					
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("departmentid"), dep);
				}
				
				Seller seller = instantiateSeller(rs, dep);
				lista.add(seller);
				
			}
			
			return lista;
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement ps = null;
		ResultSet rs= null;
		try {
			ps = conn.prepareStatement(
					"SELECT seller.*,department.name as DepName "
					+ "FROM seller INNER JOIN department "
					+ "ON seller.departmentid = department.id "
					+ "WHERE departmentid = ? "
					+ "ORDER BY name");
			ps.setInt(1, department.getId());
			rs = ps.executeQuery();
			
			List<Seller> lista = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			//Posi��o zero n�o tem, colocando next() vai verirficar as outras posi�oes e ver se tem algum conte�do
			while(rs.next()) {
				
				//Precisamos de apenas umas instancia do Department
				// Se ainda n�o existir o Map vai retornar NUll
				Department dep = map.get(rs.getInt("departmentid"));
				
				if(dep == null) {
					
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("departmentid"), dep);
				}
				
				Seller seller = instantiateSeller(rs, dep);
				lista.add(seller);				
			}
			
			return lista;
			
		}catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
			DB.closeResultSet(rs);
		}
	}

}
