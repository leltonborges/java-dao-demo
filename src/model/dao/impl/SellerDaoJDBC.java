package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connections.DB;
import exceptions.DbException;
import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao {
	private Connection cnn;

	public SellerDaoJDBC(Connection cnn) {
		this.cnn = cnn;
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
		ResultSet rs = null;
		try {
			ps = cnn.prepareStatement(
					"SELECT seller.*,department.Name as DepName " + "FROM seller INNER JOIN department "
							+ "ON seller.DepartmentId = department.Id " + "WHERE seller.Id = ?");
			ps.setInt(1, id);

			rs = ps.executeQuery();
			if (rs.next()) {
				Department d = instantiateDepartment(rs);
				Seller s = instantiateSeller(rs, d);
				return s;
			}
			return null;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.CloseStatement(ps);
			DB.CloseResultSet(rs);
		}

	}

	private Seller instantiateSeller(ResultSet rs, Department d) throws SQLException {
		Seller s = new Seller();
		s.setId(rs.getInt("Id"));
		s.setName(rs.getString("Name"));
		s.setEmail(rs.getString("Email"));
		s.setBaseSalary(rs.getDouble("BaseSalary"));
		s.setBirthDate(rs.getDate("BirthDate"));
		s.setDepartment(d);
		return s;
	}

	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department d = new Department();
		d.setId(rs.getInt("DepartmentId"));
		d.setName(rs.getString("DepName"));
		return d;
	}

	@Override
	public List<Seller> findAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Seller> fildByDepartment(Department department) {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = cnn.prepareStatement(
					"SELECT seller.*, department.Name as DepName " 
			+ "FROM seller INNER JOIN department "
			+ "ON seller.DepartmentId = department.Id " 
			+ "WHERE DepartmentId = ? " 
			+ "ORDER BY Name");
			ps.setInt(1, department.getId());
			rs = ps.executeQuery();
			
			List<Seller> listSeller = new ArrayList<>();
			Map<Integer, Department> mapDepartament = new HashMap<Integer, Department>();
			
			while (rs.next()) {
				Department d = mapDepartament.get(rs.getInt("DepartmentID"));
				if(d == null) {
					d = instantiateDepartment(rs);
					mapDepartament.put(rs.getInt("DepartmentId"), d);
				}
				
				Seller s = instantiateSeller(rs, d);
				listSeller.add(s);

			}
			return listSeller;
		} catch (SQLException e) {
			throw new DbException(e.getMessage());
		} finally {
			DB.CloseStatement(ps);
			DB.CloseResultSet(rs);
			DB.closeConnection();
		}
	}

}
