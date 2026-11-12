package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;

import connectDB.ConnectDB;
import entity.NhanVien;

public class NhanVien_DAO {
	protected Connection layKetNoi() throws SQLException {
        Connection con = ConnectDB.getConnection();
        if (con == null || con.isClosed()) {
            throw new SQLException("Không lấy được kết nối. Kiểm tra cấu hình ConnectDB và SQL Server.");
        }
        return con;
    }

    public ArrayList<NhanVien> layDanhSachNhanVien() throws SQLException {
        ArrayList<NhanVien> danhSach = new ArrayList<NhanVien>();
        String sql = "SELECT maNV, tenNV, gioiTinh, ngaySinh, soDienThoai, "
                + "diaChi, ngayVaoLam, trangThai FROM NhanVien ORDER BY maNV";

       
        Connection con = layKetNoi();
        try (PreparedStatement ps = con.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));
                nv.setGioiTinh(rs.getString("gioiTinh"));
                nv.setNgaySinh(docNgay(rs, "ngaySinh"));
                nv.setSoDienThoai(rs.getString("soDienThoai"));
                nv.setDiaChi(rs.getString("diaChi"));
                nv.setNgayVaoLam(docNgay(rs, "ngayVaoLam"));
                nv.setTrangThai(rs.getString("trangThai"));
                danhSach.add(nv);
            }
        }
        return danhSach;
    }

    public boolean kiemTraTrungMa(String maNV) throws SQLException {
        String sql = "SELECT 1 FROM NhanVien WHERE maNV = ?";
        Connection con = layKetNoi();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    
    public boolean themNhanVien(NhanVien nv) throws SQLException {
        String sql = "INSERT INTO NhanVien "
                + "(maNV, tenNV, gioiTinh, ngaySinh, soDienThoai, diaChi, ngayVaoLam, trangThai) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = layKetNoi();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, nv.getMaNV());
            ps.setNString(2, nv.getTenNV());
            ps.setNString(3, nv.getGioiTinh());
            ganNgay(ps, 4, nv.getNgaySinh());
            ps.setString(5, nv.getSoDienThoai());
            ps.setNString(6, nv.getDiaChi());
            ganNgay(ps, 7, nv.getNgayVaoLam());
            ps.setNString(8, nv.getTrangThai());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean suaNhanVien(NhanVien nv) throws SQLException {
        
        String sql = "UPDATE NhanVien SET tenNV = ?, gioiTinh = ?, ngaySinh = ?, "
                + "soDienThoai = ?, diaChi = ?, ngayVaoLam = ?, trangThai = ? "
                + "WHERE maNV = ?";

        Connection con = layKetNoi();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setNString(1, nv.getTenNV());
            ps.setNString(2, nv.getGioiTinh());
            ganNgay(ps, 3, nv.getNgaySinh());
            ps.setString(4, nv.getSoDienThoai());
            ps.setNString(5, nv.getDiaChi());
            ganNgay(ps, 6, nv.getNgayVaoLam());
            ps.setNString(7, nv.getTrangThai());
            ps.setString(8, nv.getMaNV());
            return ps.executeUpdate() == 1;
        }
    }

    public boolean xoaNhanVien(String maNV) throws SQLException {
        
        String sql = "DELETE FROM NhanVien WHERE maNV = ?";
        Connection con = layKetNoi();
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, maNV);
            return ps.executeUpdate() == 1;
        }
    }

    private LocalDate docNgay(ResultSet rs, String tenCot) throws SQLException {
        Date ngay = rs.getDate(tenCot);
        if (ngay == null) {
            return null;
        }
        return ngay.toLocalDate();
    }

    private void ganNgay(PreparedStatement ps, int viTri, LocalDate ngay)
            throws SQLException {
        if (ngay == null) {
            ps.setNull(viTri, Types.DATE);
        } else {
            ps.setDate(viTri, Date.valueOf(ngay));
        }
    }
}
