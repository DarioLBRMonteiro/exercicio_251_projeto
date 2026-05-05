package db;

public class DBIntegrityExveption extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public DBIntegrityExveption(String msg) {
		super(msg);
	}
}
