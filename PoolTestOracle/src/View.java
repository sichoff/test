import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class View {
	public static void main(String[] args) throws SQLException {
		ConnectionPool pool = ConnectionPool.getInstance();
		Connection connect = pool.getConnection();
		Statement stat = connect.createStatement();
		ResultSet rs = stat.executeQuery("SELECT * FROM NEWS");

		while (rs.next()) {
			System.out.print(rs.getString(2));
		}
		connect.close();
		pool.freeConnection(connect);
		pool.release();
	}

}
