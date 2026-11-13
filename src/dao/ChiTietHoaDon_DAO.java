package dao;

import connectDB.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.math.BigDecimal;

public class ChiTietHoaDon_DAO {

    /**
     * Thêm một chi tiết hóa đơn vào cơ sở dữ liệu khi khách gọi món
     */
	public boolean themChiTietHoaDon(String maHD, String maMon, int soLuongThem, BigDecimal donGia) {
	    // 1. Kiểm tra xem món này đã có trong hóa đơn chưa
	    String sqlCheck = "SELECT soLuong FROM ChiTietHoaDon WHERE maHD = ? AND maMon = ?";
	    String sqlUpdate = "UPDATE ChiTietHoaDon SET soLuong = soLuong + ? WHERE maHD = ? AND maMon = ?";
	    String sqlInsert = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, ghiChu) VALUES (?, ?, ?, ?, ?)";

	    Connection con = null;
	    try {
	        con = ConnectDB.getConnection();
	        
	        // Kiểm tra đã tồn tại chưa
	        try (PreparedStatement stmtCheck = con.prepareStatement(sqlCheck)) {
	            stmtCheck.setString(1, maHD);
	            stmtCheck.setString(2, maMon);
	            ResultSet rs = stmtCheck.executeQuery();

	            if (rs.next()) {
	                // ĐÃ CÓ MÓN NÀY -> Thực hiện UPDATE cộng dồn số lượng
	                try (PreparedStatement stmtUpdate = con.prepareStatement(sqlUpdate)) {
	                    stmtUpdate.setInt(1, soLuongThem);
	                    stmtUpdate.setString(2, maHD);
	                    stmtUpdate.setString(3, maMon);
	                    stmtUpdate.executeUpdate();
	                }
	            } else {
	                // CHƯA CÓ -> Thực hiện INSERT mới bình thường
	                try (PreparedStatement stmtInsert = con.prepareStatement(sqlInsert)) {
	                    stmtInsert.setString(1, maHD);
	                    stmtInsert.setString(2, maMon);
	                    stmtInsert.setInt(3, soLuongThem);
	                    stmtInsert.setBigDecimal(4, donGia);
	                    stmtInsert.setString(5, null); // Hoặc ghi chú nếu có
	                    stmtInsert.executeUpdate();
	                }
	            }
	            rs.close();
	        }
	        return true;
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }
	}
}