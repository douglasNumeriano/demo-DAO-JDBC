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
		
		PreparedStatement ps = null;
		
		try {
		ps = conn.prepareStatement("INSERT INTO seller " + 
				"(name, email, birthDate, baseSalary, departmentId) " + 
				"VALUES " + 
				"(?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
		
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			
			//Trás quantas linhas foram criadas
			int rows = ps.executeUpdate();
			
			if(rows > 0) {
				//Mostra os IDs que foram alterados
				ResultSet rs = ps.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
				DB.closeResultSet(rs);
			}else {
				throw new DbException("Erro inesoerado, nenhuma linha foi afetada");
			}
			
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
	}

	@Override
	public void update(Seller obj) {
PreparedStatement ps = null;
		
		try {
		ps = conn.prepareStatement(
				"UPDATE seller "  
				+"SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? " 
				+"WHERE Id = ? "); 
		
			ps.setString(1, obj.getName());
			ps.setString(2, obj.getEmail());
			ps.setDate(3, new java.sql.Date(obj.getBirthDate().getTime()));
			ps.setDouble(4, obj.getBaseSalary());
			ps.setInt(5, obj.getDepartment().getId());
			ps.setInt(6, obj.getId());
						
			ps.executeUpdate();			
	
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
		
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement ps = null;
		
		try {
			
			ps = conn.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
			ps.setInt(1, id);
			
			ps.executeUpdate();
						
		}
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(ps);
		}
		
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
			
			//Posição zero não tem, colocando next() vai verirficar as outras posiçoes e ver se tem algum conteúdo
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
			
			//Posição zero não tem, colocando next() vai veririficar as outras posiçoes e ver se tem algum conteúdo
			while(rs.next()) {
				
				//Precisamos de apenas umas instancia do Department
				// Se ainda não existir o Map vai retornar NUll
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
			
			//Posição zero não tem, colocando next() vai verirficar as outras posiçoes e ver se tem algum conteúdo
			while(rs.next()) {
				
				//Precisamos de apenas umas instancia do Department
				// Se ainda não existir o Map vai retornar NUll
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
