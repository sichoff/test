import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;

public class ConnectionPool {
	private static ConnectionPool instance;
	public static final String DRIVER_NAME = "oracle.jdbc.driver.OracleDriver";
	public static final String URL = "jdbc:oracle:thin:@localhost:1521/xe";
	public static final String USER = "news_db";
	public static final String PASSWORD = "123456";
	public static final int MAX_CONNECTION = 20;

	private ArrayList<Connection> freeConnections = new ArrayList<Connection>();

	private ConnectionPool() {
		loadDriver();
	}

	private void loadDriver() {
		Driver driver;
		try {
			driver = (Driver) Class.forName(DRIVER_NAME).newInstance();
			DriverManager.registerDriver(driver);
		} catch (InstantiationException | IllegalAccessException
				| ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static synchronized ConnectionPool getInstance() {
		if (null == instance) {
			instance = new ConnectionPool();
		}
		return instance;
	}

	public synchronized Connection getConnection() {
		Connection connect = null;
		if (!freeConnections.isEmpty()) {
			connect = (Connection) freeConnections
					.get(freeConnections.size() - 1);
			freeConnections.remove(connect);

			try {
				if (connect.isClosed()) {
					connect = getConnection();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				connect = getConnection();
			} catch (Exception e) {
				// TODO: handle exception
				connect = getConnection();
			}
		} else {
			connect = newConnection();
		}
		return connect;
	}

	private Connection newConnection() {
		Connection connect = null;
		try {
			if (USER == null) {

				connect = DriverManager.getConnection(URL);

			} else {
				connect = DriverManager.getConnection(URL, USER, PASSWORD);
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return connect;
	}

	public synchronized void freeConnection(Connection con) {
		if ((con != null) && (freeConnections.size() <= MAX_CONNECTION)) {
			freeConnections.add(con);
		}
	}

	public synchronized void release() {
		Iterator<Connection> iter = freeConnections.iterator();
		while (iter.hasNext()) {
			Connection con = (Connection) iter.next();
			try {
				con.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		freeConnections.clear();
	}
}
