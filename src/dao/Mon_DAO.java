package dao;

import connectDB.ConnectDB;
import entity.LoaiMon;
import entity.Mon;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class Mon_DAO {

    /**
     * Lấy danh sách toàn bộ các món ăn đang có
     */
    public List<Mon> layTatCaMon() {
        List<Mon> danhSachMon = new ArrayList<>();
        String sql = "SELECT m.*, l.tenLoai, l.moTa FROM Mon m " +
                     "JOIN LoaiMon l ON m.maLoai = l.maLoai";
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maMon = rs.getString("maMon");
                String tenMon = rs.getString("tenMon");
                String donViTinh = rs.getString("donViTinh");
                String trangThai = rs.getString("trangThai");
                BigDecimal giaNhap = rs.getBigDecimal("giaNhap");
                BigDecimal loiNhuan = rs.getBigDecimal("loiNhuan");

                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                String moTa = rs.getString("moTa");
                LoaiMon loaiMon = new LoaiMon(maLoai, tenLoai, moTa);

                Mon mon = new Mon(maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, loaiMon);
                danhSachMon.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachMon;
    }
    /**
     * Lấy danh sách món ăn theo TÊN loại món
     */
    public List<Mon> layMonTheoLoai(String tenLoaiTim) {
        List<Mon> danhSachMon = new ArrayList<>();
        // Sửa điều kiện WHERE thành l.tenLoai = ? thay vì m.maLoai = ?
        String sql = "SELECT m.*, l.tenLoai, l.moTa FROM Mon m " +
                     "JOIN LoaiMon l ON m.maLoai = l.maLoai " +
                     "WHERE l.tenLoai = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, tenLoaiTim);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String maMon = rs.getString("maMon");
                String tenMon = rs.getString("tenMon");
                String donViTinh = rs.getString("donViTinh");
                String trangThai = rs.getString("trangThai");
                BigDecimal giaNhap = rs.getBigDecimal("giaNhap");
                BigDecimal loiNhuan = rs.getBigDecimal("loiNhuan");

                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                String moTa = rs.getString("moTa");
                LoaiMon loaiMon = new LoaiMon(maLoai, tenLoai, moTa);

                Mon mon = new Mon(maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, loaiMon);
                danhSachMon.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachMon;
    }
    
    /**
     * Lấy thông tin chi tiết một món ăn theo mã món
     */
    public Mon layMonTheoMa(String maMonTim) {
        String sql = "SELECT m.*, l.tenLoai, l.moTa FROM Mon m " +
                     "JOIN LoaiMon l ON m.maLoai = l.maLoai " +
                     "WHERE m.maMon = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMonTim);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String maMon = rs.getString("maMon");
                String tenMon = rs.getString("tenMon");
                String donViTinh = rs.getString("donViTinh");
                String trangThai = rs.getString("trangThai");
                BigDecimal giaNhap = rs.getBigDecimal("giaNhap");
                BigDecimal loiNhuan = rs.getBigDecimal("loiNhuan");

                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                String moTa = rs.getString("moTa");
                LoaiMon loaiMon = new LoaiMon(maLoai, tenLoai, moTa);

                return new Mon(maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, loaiMon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}