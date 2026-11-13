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
    
    // --- CODE BỔ SUNG CHO QUẢN LÝ MÓN ---
    /**
     * Tự động phát sinh mã Món
     */
    public String phatSinhMaMon() {
        String maMoi = "M001";
        String sql = "SELECT TOP 1 maMon FROM Mon ORDER BY maMon DESC";
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String maCu = rs.getString("maMon"); // VD: M002
                int stt = Integer.parseInt(maCu.substring(1)) + 1;
                maMoi = String.format("M%03d", stt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maMoi;
    }

    /**
     * Thêm 1 món mới vào bảng
     */
    public boolean themMon(Mon m) {
        String sql = "INSERT INTO Mon(maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, maLoai) VALUES(?, ?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, m.getMaMon());
            stmt.setString(2, m.getTenMon());
            stmt.setString(3, m.getDonViTinh());
            stmt.setString(4, m.getTrangThai());
            stmt.setBigDecimal(5, m.getGiaNhap());
            stmt.setBigDecimal(6, m.getLoiNhuan());
            stmt.setString(7, m.getLoaiMon().getMaLoai());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cập nhật lại món từ bảng
     */
    public boolean suaMon(Mon m) {
        String sql = "UPDATE Mon SET tenMon=?, donViTinh=?, trangThai=?, giaNhap=?, loiNhuan=?, maLoai=? WHERE maMon=?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, m.getTenMon());
            stmt.setString(2, m.getDonViTinh());
            stmt.setString(3, m.getTrangThai());
            stmt.setBigDecimal(4, m.getGiaNhap());
            stmt.setBigDecimal(5, m.getLoiNhuan());
            stmt.setString(6, m.getLoaiMon().getMaLoai());
            stmt.setString(7, m.getMaMon());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Tìm kiếm món theo tên tương đối từ bảng (Nhập từ khoá)
     */
    public List<Mon> timMonTheoTenTuongDoi(String tuKhoa) {
        List<Mon> danhSachMon = new ArrayList<>();
        String sql = "SELECT m.*, l.tenLoai, l.moTa FROM Mon m "
        		+ "JOIN LoaiMon l ON m.maLoai = l.maLoai WHERE m.tenMon LIKE ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, "%" + tuKhoa + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                LoaiMon loaiMon = new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTa"));
                Mon mon = new Mon(rs.getString("maMon"), rs.getString("tenMon"), rs.getString("donViTinh"), 
                                  rs.getString("trangThai"), rs.getBigDecimal("giaNhap"), rs.getBigDecimal("loiNhuan"), loaiMon);
                danhSachMon.add(mon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachMon;
    }
    
    /**
     * Xoá món theo Mã 
     */
    public boolean xoaMon(String maMon) {
        String sql = "DELETE FROM Mon WHERE maMon = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maMon);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}