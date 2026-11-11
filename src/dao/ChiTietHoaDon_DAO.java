package dao;

import connectDB.ConnectDB;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.math.BigDecimal;

public class ChiTietHoaDon_DAO {

    /**
     * Thêm một chi tiết hóa đơn vào cơ sở dữ liệu khi khách gọi món
     */
    public boolean themChiTietHoaDon(String maHD, String maMon, int soLuong, BigDecimal donGia) {
        String sql = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia) VALUES (?, ?, ?, ?)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            stmt.setString(2, maMon);
            stmt.setInt(3, soLuong);
            stmt.setBigDecimal(4, donGia);

            int n = stmt.executeUpdate();
            stmt.close();
            return n > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}