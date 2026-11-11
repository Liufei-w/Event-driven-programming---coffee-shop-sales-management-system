package dao;

import connectDB.ConnectDB;
import entity.Ban;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Ban_DAO {

    /**
     * PHÁT SINH MÃ BÀN TỰ ĐỘNG: B + [XX]
     */
    public String phatSinhMaBan() {
        String maBanMoi = "B01";
        String sql = "SELECT TOP 1 maBan FROM Ban WHERE maBan LIKE 'B%' ORDER BY maBan DESC";
        
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            if (rs.next()) {
                String maCu = rs.getString("maBan");
                int stt = Integer.parseInt(maCu.substring(1)) + 1;
                maBanMoi = String.format("B%02d", stt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maBanMoi;
    }
    /**
     * LẤY TẤT CẢ DANH SÁCH BÀN
     */
    public List<Ban> layTatCaBan() {
        List<Ban> danhSachBan = new ArrayList<>();
        String sql = "SELECT maBan, tenBan, soChoNgoi, trangThai FROM Ban";
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maBan = rs.getString("maBan");
                String tenBan = rs.getString("tenBan");
                int soChoNgoi = rs.getInt("soChoNgoi");
                String trangThai = rs.getString("trangThai");
                
                Ban b = new Ban(maBan, tenBan, soChoNgoi, trangThai);
                danhSachBan.add(b);
            }
            
            // CHỈ ĐÓNG Statement và ResultSet, TUYỆT ĐỐI KHÔNG ĐÓNG Connection con ở đây
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachBan;
    }
    
    /**
     * LẤY DANH SÁCH BÀN THEO TRẠNG THÁI
     */
    public List<Ban> layDanhSachBanTheoTrangThai(String trangThaiTim) {
        List<Ban> danhSachBan = new ArrayList<>();
        String sql = "SELECT * FROM Ban WHERE trangThai = ? ORDER BY maBan ASC";
        
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThaiTim);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maBan = rs.getString("maBan");
                String tenBan = rs.getString("tenBan");
                int soChoNgoi = rs.getInt("soChoNgoi");
                String trangThai = rs.getString("trangThai");

                Ban ban = new Ban(maBan, tenBan, soChoNgoi, trangThai);
                danhSachBan.add(ban);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachBan;
    }

    /**
     * CẬP NHẬT TRẠNG THÁI BÀN
     */
    public boolean capNhatTrangThaiBan(String maBan, String trangThaiMoi) {
        String sql = "UPDATE Ban SET trangThai = ? WHERE maBan = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, trangThaiMoi);
            stmt.setString(2, maBan);
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}