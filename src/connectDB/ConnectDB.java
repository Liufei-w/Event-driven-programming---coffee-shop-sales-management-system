package connectDB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectDB {
	public static Connection con = null;
	public static ConnectDB instance = new ConnectDB();

	public static ConnectDB getInstance() {
		return instance;
	}

	public void connect() {
		String url = "jdbc:sqlserver://localhost:1433;databaseName=EvergreenDB;encrypt=true;trustServerCertificate=true";
		String user = "sa";
		String password = "sapassword";

		try {
			// Đảm bảo đóng kết nối cũ nếu nó không còn hợp lệ trước khi mở mới
			if (con != null && !con.isClosed()) {
				con.close();
			}
			con = DriverManager.getConnection(url, user, password);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Connection getConnection() {
		try {
			if (con == null || con.isClosed()) {
				instance.connect();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			instance.connect(); // Thử kết nối lại nếu check bị lỗi
		}
		return con;
	}
}