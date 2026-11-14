package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import connectDB.ConnectDB;
import entity.KhachHang;

public class KhachHang_DAO {
	public List<KhachHang> layTatCaKhachHang() throws SQLException {
        List<KhachHang> danhSach = new ArrayList<KhachHang>();
        String sql = "SELECT maKH, tenKH, soDienThoai FROM KhachHang ORDER BY LEN(maKH), maKH";
        Connection con = ConnectDB.getConnection();
        try (Statement stmt = con.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                danhSach.add(new KhachHang(rs.getString("maKH"), rs.getString("tenKH"),
                        rs.getString("soDienThoai")));
            }
        }
        return danhSach;
    }

    public String phatSinhMaKhachHang() throws SQLException {
        int soLonNhat = 0;
        Connection con = ConnectDB.getConnection();
        try (Statement stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery("SELECT maKH FROM KhachHang")) {
            while (rs.next()) {
                String maKH = rs.getString("maKH");
                if (maKH != null && maKH.matches("(?i)KH[0-9]{1,8}")) {
                    int soThuTu = Integer.parseInt(maKH.substring(2));
                    if (soThuTu > soLonNhat) {
                        soLonNhat = soThuTu;
                    }
                }
            }
        }
        if (soLonNhat == 99999999) {
            throw new SQLException("Đã hết mã khách hàng trong giới hạn 10 ký tự của database.");
        }
        return String.format(Locale.ROOT, "KH%03d", soLonNhat + 1);
    }
    
    public boolean kiemTraTrungMaKhachHang(String maKH) throws SQLException {
        String sql = "SELECT 1 FROM KhachHang WHERE maKH = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }

    private String chuanHoaSoDienThoai(String soDienThoai) {
        if (soDienThoai == null || soDienThoai.trim().isEmpty()) {
            return null;
        }
        return soDienThoai.trim();
    }

    public boolean kiemTraTrungSoDienThoai(String soDienThoai, String maKHBoQua)
            throws SQLException {
        String sdt = chuanHoaSoDienThoai(soDienThoai);
        String sql;
        if (sdt == null) {
            sql = "SELECT 1 FROM KhachHang WHERE soDienThoai IS NULL";
        } else {
            sql = "SELECT 1 FROM KhachHang WHERE soDienThoai = ?";
        }
        if (maKHBoQua != null) {
            sql += " AND maKH <> ?";
        }
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            int viTri = 1;
            if (sdt != null) {
                stmt.setString(viTri++, sdt);
            }
            if (maKHBoQua != null) {
                stmt.setString(viTri, maKHBoQua);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        }
    }
    
    private String thongBaoTrungSoDienThoai(String soDienThoai) {
        if (soDienThoai == null) {
            return "Database đã có một khách hàng để trống số điện thoại. "
                    + "Vui lòng nhập số điện thoại để đăng ký khách hàng khác.";
        }
        return "Số điện thoại đã được đăng ký cho khách hàng khác!";
    }

    private boolean laLoiTrung(SQLException e) {
        return e.getErrorCode() == 2627 || e.getErrorCode() == 2601;
    }

    public boolean themKhachHang(KhachHang khachHang) throws SQLException {
        String sdt = chuanHoaSoDienThoai(khachHang.getSoDienThoai());
        if (kiemTraTrungSoDienThoai(sdt, null)) {
            throw new SQLException(thongBaoTrungSoDienThoai(sdt));
        }
        String sql = "INSERT INTO KhachHang (maKH, tenKH, soDienThoai) VALUES (?, ?, ?)";
        Connection con = ConnectDB.getConnection();
        for (int lanThu = 1; lanThu <= 3; lanThu++) {
            String maMoi = phatSinhMaKhachHang();
            if (kiemTraTrungMaKhachHang(maMoi)) {
                continue;
            }
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                stmt.setString(1, maMoi);
                stmt.setNString(2, khachHang.getTenKH().trim());
                if (sdt == null) {
                    stmt.setNull(3, Types.VARCHAR);
                } else {
                    stmt.setString(3, sdt);
                }
                boolean thanhCong = stmt.executeUpdate() == 1;
                if (thanhCong) {
                    khachHang.setMaKH(maMoi);
                }
                return thanhCong;
            } catch (SQLException e) {
                if (!laLoiTrung(e)) {
                    throw e;
                }
                if (kiemTraTrungSoDienThoai(sdt, null)) {
                    throw new SQLException(thongBaoTrungSoDienThoai(sdt),
                            e.getSQLState(), e.getErrorCode(), e);
                }
                if (lanThu == 3) {
                    throw new SQLException("Chưa tạo được mã khách hàng mới vì mã vừa bị sử dụng. "
                            + "Vui lòng nhấn Thêm để thử lại.", e.getSQLState(), e.getErrorCode(), e);
                }
            }
        }
        throw new SQLException("Chưa tạo được mã khách hàng mới. Vui lòng nhấn Thêm để thử lại.");
    }
    
    public boolean suaKhachHang(KhachHang khachHang) throws SQLException {
        String sdt = chuanHoaSoDienThoai(khachHang.getSoDienThoai());
        if (kiemTraTrungSoDienThoai(sdt, khachHang.getMaKH())) {
            throw new SQLException(thongBaoTrungSoDienThoai(sdt));
        }
        String sql = "UPDATE KhachHang SET tenKH = ?, soDienThoai = ? WHERE maKH = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setNString(1, khachHang.getTenKH().trim());
            if (sdt == null) {
                stmt.setNull(2, Types.VARCHAR);
            } else {
                stmt.setString(2, sdt);
            }
            stmt.setString(3, khachHang.getMaKH());
            return stmt.executeUpdate() == 1;
        } catch (SQLException e) {
            if (laLoiTrung(e)) {
                throw new SQLException(thongBaoTrungSoDienThoai(sdt),
                        e.getSQLState(), e.getErrorCode(), e);
            }
            throw e;
        }
    }

    public boolean xoaKhachHang(String maKH) throws SQLException {
        String sql = "DELETE FROM KhachHang WHERE maKH = ?";
        Connection con = ConnectDB.getConnection();
        try (PreparedStatement stmt = con.prepareStatement(sql)) {
            stmt.setString(1, maKH);
            return stmt.executeUpdate() == 1;
        }
    }
}
