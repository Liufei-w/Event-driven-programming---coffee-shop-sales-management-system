package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import connectDB.ConnectDB;
import entity.TaiKhoan;

public class TaiKhoan_DAO {
	  public TaiKhoan dangNhap(String tenDangNhap, String matKhau) {
	        TaiKhoan tk = null;

	        String sql = "SELECT * FROM TaiKhoan WHERE tenDangNhap = ? AND matKhau = ?";

	        try {
	            Connection con = ConnectDB.getConnection();
	            PreparedStatement stmt = con.prepareStatement(sql);

	            stmt.setString(1, tenDangNhap);
	            stmt.setString(2, matKhau);

	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                String tenDN = rs.getString("tenDangNhap");
	                String mk = rs.getString("matKhau");
	                String vaiTro = rs.getString("vaiTro");
	                String trangThai = rs.getString("trangThai");

	                tk = new TaiKhoan(tenDN, mk, vaiTro, trangThai);
	            }

	            rs.close();
	            stmt.close();

	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return tk;
	    }
}
