package application;


import java.util.Date;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {

	public static void main(String[] args) {
		
		
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("=== TEST 1: seller findById ======");
		Seller seller = sellerDao.findById(3);
		System.out.println(seller);
		
		System.out.println("\n=== TEST 2: seller findByDepartment ======");
		Department dep = new Department(2, null);
		List<Seller> lista = sellerDao.findByDepartment(dep);
		for(Seller obj : lista) {
		System.out.println(obj);
		}
	
		System.out.println("\n=== TEST 3: seller findAll ======");
		lista = sellerDao.findAll();
		for(Seller obj : lista) {
		System.out.println(obj);
		}
		
		System.out.println("\n=== TEST 3: seller findAll ======");
		Seller obj = new Seller(null, "Douglas", "douglas_rnmeriano@gmail.com", new Date(), 4000.00, dep);
		sellerDao.insert(obj);
		System.out.println("Id adicionado: " + obj.getId());
	}

}
