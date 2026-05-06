package application;

import java.util.Date;
import java.util.List;

//import java.util.Date;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class Program {
	public static void main(String[] args) {
//		Department obj = new Department(1, "books");
//		
//		Seller seller = new Seller(21,"Bob","bob@gmail.com", new Date(),3000.00,obj);		
			
		SellerDao sellerDao = DaoFactory.createSellerDao();
		
		System.out.println("==== TEST 1: seller findById =====");	
		Seller seller = sellerDao.findById(3);		
		System.out.println(seller);

		System.out.println("\n==== TEST 2: seller findByDepartment =====");
		Department department = new Department(2,null);
		List<Seller> list = sellerDao.findByDepartment(department);
		
		for (Seller obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n==== TEST 3: seller findAll =====");
		list = sellerDao.findAll();
		
		for (Seller obj : list) {
			System.out.println(obj);
		}
		
		System.out.println("\n==== TEST 4: seller Insert =====");
		Seller newSeller = new Seller(null, "João", "joao@gmail.com", new Date(), 4000.00, department);

		sellerDao.insert(newSeller);
		
		System.out.println("Inserted! New id = " + newSeller.getId());
		
		System.out.println("\n==== TEST 5: seller Update =====");
		seller = sellerDao.findById(15);
		seller.setName("Leticia");
		seller.setEmail("leticia@gmail.com");

		sellerDao.update(seller);
		
		System.out.println("Updated completed");		
	}	
}
