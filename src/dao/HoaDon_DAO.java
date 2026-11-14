package dao;

import connectDB.ConnectDB;
import entity.*;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HoaDon_DAO {
	
	// PHẦN DÀNH CHO TRA CỨU HOÁ ĐƠN
	public List<HoaDon> timKiemHoaDon(String maHDCantim, String trangThaiFilter, String mocThoiGian) {
	    List<HoaDon> dsHD = new ArrayList<>();
	    StringBuilder sql = new StringBuilder(
	        "SELECT h.*, n.tenNV, b.tenBan, k.tenKH, km.tenKM " +
	        "FROM HoaDon h " +
	        "JOIN NhanVien n ON h.maNV = n.maNV " +
	        "LEFT JOIN Ban b ON h.maBan = b.maBan " +
	        "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
	        "LEFT JOIN KhuyenMai km ON h.maKM = km.maKM " +
	        "WHERE 1=1"
	    );
	    
	    // Điều kiện lọc theo mã hóa đơn
	    if (maHDCantim != null && !maHDCantim.trim().isEmpty()) {
	        sql.append(" AND h.maHD LIKE ?");
	    }
	    // Điều kiện lọc theo trạng thái
	    if (trangThaiFilter != null && !trangThaiFilter.equals("Tất cả")) {
	        sql.append(" AND h.trangThai = ?");
	    }
	    
	    // Điều kiện lọc theo mốc thời gian (Sử dụng hàm ngày tháng của SQL Server)
	    if (mocThoiGian != null) {
	        if (mocThoiGian.equals("Hôm nay")) {
	            sql.append(" AND CAST(h.ngayLap AS DATE) = CAST(GETDATE() AS DATE)");
	        } else if (mocThoiGian.equals("Tuần này")) {
	            sql.append(" AND DATEPART(week, h.ngayLap) = DATEPART(week, GETDATE()) AND YEAR(h.ngayLap) = YEAR(GETDATE())");
	        } else if (mocThoiGian.equals("Tháng này")) {
	            sql.append(" AND MONTH(h.ngayLap) = MONTH(GETDATE()) AND YEAR(h.ngayLap) = YEAR(GETDATE())");
	        }
	    }
	    
	    sql.append(" ORDER BY h.ngayLap DESC");

	    try (Connection con = ConnectDB.getConnection();
	         PreparedStatement stmt = con.prepareStatement(sql.toString())) {
	        
	        int index = 1;
	        if (maHDCantim != null && !maHDCantim.trim().isEmpty()) {
	            stmt.setString(index++, "%" + maHDCantim.trim() + "%");
	        }
	        if (trangThaiFilter != null && !trangThaiFilter.equals("Tất cả")) {
	            stmt.setString(index++, trangThaiFilter);
	        }

	        try (ResultSet rs = stmt.executeQuery()) {
	            while (rs.next()) {
	                String maHD = rs.getString("maHD");
	                LocalDateTime ngayLap = rs.getTimestamp("ngayLap").toLocalDateTime();
	                String ptThanhToan = rs.getString("phuongThucThanhToan");
	                String trangThai = rs.getString("trangThai");
	                BigDecimal tienGiam = rs.getBigDecimal("tienGiam");
	                BigDecimal thueVAT = rs.getBigDecimal("thueVAT");

	                NhanVien nv = new NhanVien();
	                nv.setMaNV(rs.getString("maNV"));
	                nv.setTenNV(rs.getString("tenNV"));

	                Ban ban = null;
	                if (rs.getString("maBan") != null) {
	                    ban = new Ban();
	                    ban.setMaBan(rs.getString("maBan"));
	                    ban.setTenBan(rs.getString("tenBan"));
	                }

	                KhachHang kh = null;
	                if (rs.getString("maKH") != null) {
	                    kh = new KhachHang();
	                    kh.setMaKH(rs.getString("maKH"));
	                    kh.setTenKH(rs.getString("tenKH"));
	                }

	                KhuyenMai km = null;
	                if (rs.getString("maKM") != null) {
	                    km = new KhuyenMai();
	                    km.setMaKM(rs.getString("maKM"));
	                    km.setTenKM(rs.getString("tenKM"));
	                }

	                HoaDon hd = new HoaDon(maHD, ngayLap, ptThanhToan, trangThai, tienGiam, thueVAT, nv, ban, kh, km);
	                hd.setDanhSachChiTiet(new ArrayList<>()); 
	                
	                dsHD.add(hd);
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return dsHD;
	}
	
	//===================================================

	/**
     * Phát sinh mã hóa đơn tự động
     */
    public String phatSinhMaHD() {
        String maHDMoi = "";
        LocalDate now = LocalDate.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "HD" + datePart;

        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";
        
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            
            int stt = 1;
            if (rs.next()) {
                String maCu = rs.getString("maHD");
                String sttStr = maCu.substring(prefix.length());
                stt = Integer.parseInt(sttStr) + 1;
            }
            maHDMoi = prefix + String.format("%03d", stt);
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maHDMoi;
    }

    /**
     * Tạo hóa đơn mới kèm theo danh sách chi tiết món và cập nhật trạng thái bàn
     */
    public boolean taoHoaDon(HoaDon hd) {
        String sqlHD = "INSERT INTO HoaDon (maHD, ngayLap, phuongThucThanhToan, trangThai, tienGiam, thueVAT, maNV, maBan, maKH, maKM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlCTHD = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, ghiChu) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateBan = "UPDATE Ban SET trangThai = N'Đang phục vụ' WHERE maBan = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Lưu hóa đơn
            try (PreparedStatement stmtHD = con.prepareStatement(sqlHD)) {
                stmtHD.setString(1, hd.getMaHD());
                stmtHD.setTimestamp(2, Timestamp.valueOf(hd.getNgayLap() != null ? hd.getNgayLap() : LocalDateTime.now()));
                stmtHD.setString(3, hd.getPhuongThucThanhToan() != null ? hd.getPhuongThucThanhToan() : "Tiền mặt");
                stmtHD.setString(4, hd.getTrangThai() != null ? hd.getTrangThai() : "Chưa thanh toán");
                stmtHD.setBigDecimal(5, hd.getTienGiam() != null ? hd.getTienGiam() : BigDecimal.ZERO);
                stmtHD.setBigDecimal(6, hd.getThueVAT() != null ? hd.getThueVAT() : BigDecimal.valueOf(0.10));
                stmtHD.setString(7, hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : null);
                stmtHD.setString(8, hd.getBan() != null ? hd.getBan().getMaBan() : null);
                stmtHD.setString(9, hd.getKhachHang() != null ? hd.getKhachHang().getMaKH() : null);
                stmtHD.setString(10, hd.getKhuyenMai() != null ? hd.getKhuyenMai().getMaKM() : null);
                stmtHD.executeUpdate();
            }

            // 2. Lưu chi tiết hóa đơn (nếu có)
            if (hd.getDanhSachChiTiet() != null && !hd.getDanhSachChiTiet().isEmpty()) {
                try (PreparedStatement stmtCTHD = con.prepareStatement(sqlCTHD)) {
                    for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                        stmtCTHD.setString(1, hd.getMaHD());
                        stmtCTHD.setString(2, cthd.getMon().getMaMon());
                        stmtCTHD.setInt(3, cthd.getSoLuong());
                        stmtCTHD.setBigDecimal(4, cthd.getDonGia());
                        stmtCTHD.setString(5, cthd.getGhiChu());
                        stmtCTHD.addBatch();
                    }
                    stmtCTHD.executeBatch();
                }
            }

            // 3. Cập nhật trạng thái bàn thành "Đang phục vụ"
            if (hd.getBan() != null && hd.getBan().getMaBan() != null) {
                try (PreparedStatement stmtBan = con.prepareStatement(sqlUpdateBan)) {
                    stmtBan.setString(1, hd.getBan().getMaBan());
                    stmtBan.executeUpdate();
                }
            }

            con.commit(); // Xác nhận thành công
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tìm mã hóa đơn chưa thanh toán của một bàn cụ thể
     */
    public String layMaHDChuaThanhToanTheoBan(String maBan) {
        String maHD = null;
        // Thêm sắp xếp ORDER BY ngayLap DESC để nếu có nhiều cái cũ, nó chỉ lấy hóa đơn mới nhất trong ngày
        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maBan = ? AND trangThai = N'Chưa thanh toán' ORDER BY ngayLap DESC";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maBan);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                maHD = rs.getString("maHD");
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maHD;
    }

    /**
     * Lấy thông tin một hóa đơn theo mã
     */
    public HoaDon layHoaDonTheoMa(String maHD) {
        HoaDon hd = null;
        String sql = "SELECT h.*, n.tenNV, b.tenBan, k.tenKH, km.tenKM " +
                     "FROM HoaDon h " +
                     "JOIN NhanVien n ON h.maNV = n.maNV " +
                     "LEFT JOIN Ban b ON h.maBan = b.maBan " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
                     "LEFT JOIN KhuyenMai km ON h.maKM = km.maKM " +
                     "WHERE h.maHD = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                LocalDateTime ngayLap = rs.getTimestamp("ngayLap").toLocalDateTime();
                String ptThanhToan = rs.getString("phuongThucThanhToan");
                String trangThai = rs.getString("trangThai");
                BigDecimal tienGiam = rs.getBigDecimal("tienGiam");
                BigDecimal thueVAT = rs.getBigDecimal("thueVAT");

                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));

                Ban ban = null;
                if (rs.getString("maBan") != null) {
                    ban = new Ban();
                    ban.setMaBan(rs.getString("maBan"));
                    ban.setTenBan(rs.getString("tenBan"));
                }

                KhachHang kh = null;
                if (rs.getString("maKH") != null) {
                    kh = new KhachHang();
                    kh.setMaKH(rs.getString("maKH"));
                    kh.setTenKH(rs.getString("tenKH"));
                }

                KhuyenMai km = null;
                if (rs.getString("maKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("maKM"));
                    km.setTenKM(rs.getString("tenKM"));
                }

                hd = new HoaDon(maHD, ngayLap, ptThanhToan, trangThai, tienGiam, thueVAT, nv, ban, kh, km);
                hd.setDanhSachChiTiet(layChiTietHoaDon(maHD));
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return hd;
    }

    /**
     * Lấy chi tiết món ăn của một hóa đơn
     */
    public List<ChiTietHoaDon> layChiTietHoaDon(String maHD) {
        List<ChiTietHoaDon> danhSachCT = new ArrayList<>();
        String sql = "SELECT c.*, m.tenMon, m.donViTinh FROM ChiTietHoaDon c " +
                     "JOIN Mon m ON c.maMon = m.maMon " +
                     "WHERE c.maHD = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maHD);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Mon mon = new Mon();
                mon.setMaMon(rs.getString("maMon"));
                mon.setTenMon(rs.getString("tenMon"));
                mon.setDonViTinh(rs.getString("donViTinh"));

                ChiTietHoaDon cthd = new ChiTietHoaDon(null, mon, rs.getInt("soLuong"), rs.getBigDecimal("donGia"), rs.getString("ghiChu"));
                danhSachCT.add(cthd);
            }
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachCT;
    }

    /**
     * THANH TOÁN HÓA ĐƠN VÀ CẬP NHẬT BÀN VỀ TRỐNG
     */
    public boolean thanhToanHoaDon(String maHD, String phuongThuc, String maBan, BigDecimal tienGiam, String maKM) {
        String sqlHD = "UPDATE HoaDon SET trangThai = N'Đã thanh toán', phuongThucThanhToan = ?, tienGiam = ?, maKM = ? WHERE maHD = ?";
        String sqlBan = "UPDATE Ban SET trangThai = N'Trống' WHERE maBan = ?";

        Connection con = null;
        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); // Bắt đầu Transaction

            // 1. Cập nhật trạng thái, phương thức thanh toán, tiền giảm và mã khuyến mãi
            try (PreparedStatement stmtHD = con.prepareStatement(sqlHD)) {
                stmtHD.setString(1, phuongThuc);
                stmtHD.setBigDecimal(2, tienGiam != null ? tienGiam : BigDecimal.ZERO);
                if (maKM != null && !maKM.trim().isEmpty()) {
                    stmtHD.setString(3, maKM);
                } else {
                    stmtHD.setNull(3, java.sql.Types.VARCHAR);
                }
                stmtHD.setString(4, maHD);
                stmtHD.executeUpdate();
            }

            // 2. Cập nhật trạng thái bàn về "Trống" trong CSDL
            if (maBan != null && !maBan.trim().isEmpty()) {
                String cleanMaBan = maBan.trim();
                try (PreparedStatement stmtBan = con.prepareStatement(sqlBan)) {
                    stmtBan.setString(1, cleanMaBan);
                    stmtBan.executeUpdate();
                }
            }

            con.commit(); // Xác nhận lưu thay đổi xuống CSDL
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try { 
                    con.rollback(); 
                } catch (SQLException ex) { 
                    ex.printStackTrace(); 
                }
            }
            return false;
        } finally {
            try {
                if (con != null) {
                    con.setAutoCommit(true);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * Lấy danh sách hóa đơn đã thanh toán theo khoảng ngày
     */
    public List<HoaDon> layDanhSachHoaDonTheoNgay(LocalDate tuNgay, LocalDate denNgay) {
        List<HoaDon> dsHD = new ArrayList<>();

        String sql = "SELECT h.*, n.tenNV " +
                     "FROM HoaDon h " +
                     "JOIN NhanVien n ON h.maNV = n.maNV " +
                     "WHERE h.trangThai = N'Đã thanh toán' " +
                     "AND h.ngayLap >= ? " +
                     "AND h.ngayLap < DATEADD(DAY, 1, ?) " +
                     "ORDER BY h.ngayLap DESC";

        Connection con = ConnectDB.getConnection();

        try (PreparedStatement stmt = con.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(tuNgay));
            stmt.setDate(2, java.sql.Date.valueOf(denNgay));

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {

                    String maHD = rs.getString("maHD");
                    LocalDateTime ngayLap = rs.getTimestamp("ngayLap").toLocalDateTime();

                    String ptThanhToan = rs.getString("phuongThucThanhToan");
                    String trangThai = rs.getString("trangThai");

                    BigDecimal tienGiam = rs.getBigDecimal("tienGiam");
                    BigDecimal thueVAT = rs.getBigDecimal("thueVAT");

                    NhanVien nv = new NhanVien();
                    nv.setMaNV(rs.getString("maNV"));
                    nv.setTenNV(rs.getString("tenNV"));

                    Ban ban = null;
                    KhachHang kh = null;
                    KhuyenMai km = null;

                    HoaDon hd = new HoaDon(
                        maHD, ngayLap, ptThanhToan, trangThai,
                        tienGiam, thueVAT, nv, ban, kh, km
                    );

                    // Lấy danh sách món của hóa đơn
                    hd.setDanhSachChiTiet(layChiTietHoaDon(maHD));

                    dsHD.add(hd);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return dsHD;
    }
}