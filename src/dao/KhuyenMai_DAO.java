package dao;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.time.format.DateTimeFormatter;
import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {
    public List<KhuyenMai> layKhuyenMaiHopLe(BigDecimal tongTienMon) {
        List<KhuyenMai> danhSachKM = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE ngayBatDau <= ? AND ngayKetThuc >= ? AND donToiThieu <= ?";
        
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                LocalDate now = LocalDate.now();
                stmt.setDate(1, Date.valueOf(now));
                stmt.setDate(2, Date.valueOf(now));
                stmt.setBigDecimal(3, tongTienMon != null ? tongTienMon : BigDecimal.ZERO);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String maKM = rs.getString("maKM");
                        String tenKM = rs.getString("tenKM");
                        BigDecimal phanTramGiam = rs.getBigDecimal("phanTrangGiam"); 
                        BigDecimal donToiThieu = rs.getBigDecimal("donToiThieu");

                        KhuyenMai km = new KhuyenMai(maKM, tenKM, phanTramGiam, null, null, donToiThieu);
                        danhSachKM.add(km);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachKM;
    }
    
    // ========================================================
    // CODE BỔ SUNG CHO QUẢN LÝ KHUYẾN MÃI (CRUD)
    // ========================================================

    /**
     * 1. Lấy danh sách toàn bộ khuyến mãi
     */
    public List<KhuyenMai> layTatCaKhuyenMai() {
        List<KhuyenMai> dsKM = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai";
        
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maKM = rs.getString("maKM");
                String tenKM = rs.getString("tenKM");
                BigDecimal phanTramGiam = rs.getBigDecimal("phanTrangGiam");
                Date dBatDau = rs.getDate("ngayBatDau");
                Date dKetThuc = rs.getDate("ngayKetThuc");
                LocalDate ngayBatDau = (dBatDau != null) ? dBatDau.toLocalDate() : null;
                LocalDate ngayKetThuc = (dKetThuc != null) ? dKetThuc.toLocalDate() : null;
                BigDecimal donToiThieu = rs.getBigDecimal("donToiThieu");

                KhuyenMai km = new KhuyenMai(maKM, tenKM, phanTramGiam, ngayBatDau, ngayKetThuc, donToiThieu);
                dsKM.add(km);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return dsKM;
    }

    /**
     * 2. Tự động phát sinh mã khuyến mãi theo quy luật: KM + [YYMMDD] + [XX]
     */
    public String phatSinhMaKM() {
        String ngayHienTai = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "KM" + ngayHienTai;
        String maMoi = prefix + "01";
        String sql = "SELECT TOP 1 maKM FROM KhuyenMai WHERE maKM LIKE ? ORDER BY maKM DESC";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String maCu = rs.getString("maKM").trim();
                if (maCu.length() >= 10) {
                    int stt = Integer.parseInt(maCu.substring(8)) + 1;
                    maMoi = prefix + String.format("%02d", stt);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return maMoi;
    }

    /**
     * 3. Thêm 1 khuyến mãi mới
     */
    public boolean themKhuyenMai(KhuyenMai km) {
        String sql = "INSERT INTO KhuyenMai(maKM, tenKM, phanTrangGiam, ngayBatDau, ngayKetThuc, donToiThieu) "
                   + "VALUES(?, ?, ?, ?, ?, ?)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getMaKM());
            stmt.setString(2, km.getTenKM());
            stmt.setBigDecimal(3, km.getPhanTramGiam());
            stmt.setDate(4, Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(5, Date.valueOf(km.getNgayKetThuc()));
            stmt.setBigDecimal(6, km.getDonToiThieu());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 4. Cập nhật thông tin khuyến mãi
     */
    public boolean suaKhuyenMai(KhuyenMai km) {
        String sql = "UPDATE KhuyenMai SET tenKM = ?, phanTrangGiam = ?, ngayBatDau = ?, "
                   + "ngayKetThuc = ?, donToiThieu = ? WHERE maKM = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, km.getTenKM());
            stmt.setBigDecimal(2, km.getPhanTramGiam());
            stmt.setDate(3, Date.valueOf(km.getNgayBatDau()));
            stmt.setDate(4, Date.valueOf(km.getNgayKetThuc()));
            stmt.setBigDecimal(5, km.getDonToiThieu());
            stmt.setString(6, km.getMaKM());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 5. Xóa khuyến mãi theo mã KM
     */
    public boolean xoaKhuyenMai(String maKM) {
        String sql = "DELETE FROM KhuyenMai WHERE maKM = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKM);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 6. Tìm kiếm khuyến mãi theo mã KM
     */
    public KhuyenMai timKhuyenMaiTheoMa(String maKMTim) {
        String sql = "SELECT * FROM KhuyenMai WHERE maKM = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maKMTim);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String maKM = rs.getString("maKM");
                String tenKM = rs.getString("tenKM");
                BigDecimal phanTramGiam = rs.getBigDecimal("phanTrangGiam");
                Date dBatDau = rs.getDate("ngayBatDau");
                Date dKetThuc = rs.getDate("ngayKetThuc");
                LocalDate ngayBatDau = (dBatDau != null) ? dBatDau.toLocalDate() : null;
                LocalDate ngayKetThuc = (dKetThuc != null) ? dKetThuc.toLocalDate() : null;
                BigDecimal donToiThieu = rs.getBigDecimal("donToiThieu");

                return new KhuyenMai(maKM, tenKM, phanTramGiam, ngayBatDau, ngayKetThuc, donToiThieu);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}