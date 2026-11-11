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

    /**
     * Quy tắc: HD + [YYMMDD] + [XXX]
     */
    public String phatSinhMaHD() {
        String maHDMoi = "";
        LocalDate now = LocalDate.now();
        String datePart = now.format(DateTimeFormatter.ofPattern("yyMMdd"));
        String prefix = "HD" + datePart;

        // Câu lệnh SQL tìm mã hóa đơn lớn nhất trong ngày hiện tại
        String sql = "SELECT TOP 1 maHD FROM HoaDon WHERE maHD LIKE ? ORDER BY maHD DESC";
        
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, prefix + "%");
            ResultSet rs = stmt.executeQuery();
            
            int stt = 1;
            if (rs.next()) {
                String maCu = rs.getString("maHD");
                // Cắt phần số thứ tự phía sau (3 ký tự cuối) rồi cộng lên 1
                String sttStr = maCu.substring(prefix.length());
                stt = Integer.parseInt(sttStr) + 1;
            }
            // Ghép lại thành mã mới (Ví dụ: HD261110 + 001 -> HD261110001)
            maHDMoi = prefix + String.format("%03d", stt);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maHDMoi;
    }

    /**
     * TẠO HÓA ĐƠN MỚI
     */
    public boolean taoHoaDon(HoaDon hd) {
        Connection con = null;
        PreparedStatement stmtHD = null;
        PreparedStatement stmtCTHD = null;
        PreparedStatement stmtBan = null;

        String sqlHD = "INSERT INTO HoaDon (maHD, ngayLap, phuongThucThanhToan, trangThai, tienGiam, thueVAT, maNV, maBan, maKH, maKM) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        String sqlCTHD = "INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, ghiChu) VALUES (?, ?, ?, ?, ?)";
        String sqlUpdateBan = "UPDATE Ban SET trangThai = N'Đang phục vụ' WHERE maBan = ?";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false);

         // Lưu thông tin chung của Hóa đơn
            stmtHD = con.prepareStatement(sqlHD);
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
            stmtHD.executeUpdate();

            //Lưu từng món ăn trong danh sách chi tiết hóa đơn
            stmtCTHD = con.prepareStatement(sqlCTHD);
            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                stmtCTHD.setString(1, hd.getMaHD());
                stmtCTHD.setString(2, cthd.getMon().getMaMon());
                stmtCTHD.setInt(3, cthd.getSoLuong());
                stmtCTHD.setBigDecimal(4, cthd.getDonGia());
                stmtCTHD.setString(5, cthd.getGhiChu());
                stmtCTHD.addBatch(); // Gom nhóm lệnh để chạy nhanh hơn
            }
            stmtCTHD.executeBatch();

            //Cập nhật trạng thái bàn thành "Đang phục vụ" (nếu khách ngồi tại bàn)
            if (hd.getBan() != null && hd.getBan().getMaBan() != null) {
                stmtBan = con.prepareStatement(sqlUpdateBan);
                stmtBan.setString(1, hd.getBan().getMaBan());
                stmtBan.executeUpdate();
            }

            con.commit(); // Xác nhận mọi thứ thành công
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
            if (con != null) {
                try {
                    con.rollback(); // Nếu có lỗi thì hoàn tác lại CSDL
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            try {
                if (stmtHD != null) stmtHD.close();
                if (stmtCTHD != null) stmtCTHD.close();
                if (stmtBan != null) stmtBan.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * LẤY DANH SÁCH TẤT CẢ HÓA ĐƠN
     * Phục vụ cho màn hình "Tra cứu hóa đơn" và "Quản lý hóa đơn".
     */
    public List<HoaDon> layTatCaHoaDon() {
        List<HoaDon> danhSachHD = new ArrayList<>();
        String sql = "SELECT h.*, n.tenNV, b.tenBan, k.tenKH, km.tenKM " +
                     "FROM HoaDon h " +
                     "JOIN NhanVien n ON h.maNV = n.maNV " +
                     "LEFT JOIN Ban b ON h.maBan = b.maBan " +
                     "LEFT JOIN KhachHang k ON h.maKH = k.maKH " +
                     "LEFT JOIN KhuyenMai km ON h.maKM = km.maKM " +
                     "ORDER BY h.ngayLap DESC";
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maHD = rs.getString("maHD");
                LocalDateTime ngayLap = rs.getTimestamp("ngayLap").toLocalDateTime();
                String ptThanhToan = rs.getString("phuongThucThanhToan");
                String trangThai = rs.getString("trangThai");
                BigDecimal tienGiam = rs.getBigDecimal("tienGiam");
                BigDecimal thueVAT = rs.getBigDecimal("thueVAT");

                // Đóng gói thông tin Nhân viên
                NhanVien nv = new NhanVien();
                nv.setMaNV(rs.getString("maNV"));
                nv.setTenNV(rs.getString("tenNV"));

                // Đóng gói thông tin Bàn
                Ban ban = null;
                if (rs.getString("maBan") != null) {
                    ban = new Ban();
                    ban.setMaBan(rs.getString("maBan"));
                    ban.setTenBan(rs.getString("tenBan"));
                }

                // Đóng gói thông tin Khách hàng
                KhachHang kh = null;
                if (rs.getString("maKH") != null) {
                    kh = new KhachHang();
                    kh.setMaKH(rs.getString("maKH"));
                    kh.setTenKH(rs.getString("tenKH"));
                }

                // Đóng gói thông tin Khuyến mãi
                KhuyenMai km = null;
                if (rs.getString("maKM") != null) {
                    km = new KhuyenMai();
                    km.setMaKM(rs.getString("maKM"));
                    km.setTenKM(rs.getString("tenKM"));
                }

                // Tạo đối tượng HoaDon hoàn chỉnh
                HoaDon hd = new HoaDon(maHD, ngayLap, ptThanhToan, trangThai, tienGiam, thueVAT, nv, ban, kh, km);
                
                // Lấy thêm danh sách món ăn thuộc hóa đơn này gán vào
                hd.setDanhSachChiTiet(layChiTietHoaDon(maHD));
                
                danhSachHD.add(hd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachHD;
    }

    /**
     * LẤY CHI TIẾT MÓN ĂN CỦA MỘT HÓA ĐƠN
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
                String maMon = rs.getString("maMon");
                String tenMon = rs.getString("tenMon");
                String donViTinh = rs.getString("donViTinh");
                int soLuong = rs.getInt("soLuong");
                BigDecimal donGia = rs.getBigDecimal("donGia");
                String ghiChu = rs.getString("ghiChu");

                // Tạo đối tượng Mon
                Mon mon = new Mon();
                mon.setMaMon(maMon);
                mon.setTenMon(tenMon);
                mon.setDonViTinh(donViTinh);

                // Tạo đối tượng ChiTietHoaDon
                ChiTietHoaDon cthd = new ChiTietHoaDon(null, mon, soLuong, donGia, ghiChu);
                danhSachCT.add(cthd);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachCT;
    }

    /**
     * THANH TOÁN HÓA ĐƠN
     */
    public boolean thanhToanHoaDon(String maHD, String phuongThuc, String maBan) {
        Connection con = null;
        PreparedStatement stmtHD = null;
        PreparedStatement stmtBan = null;

        String sqlHD = "UPDATE HoaDon SET trangThai = N'Đã thanh toán', phuongThucThanhToan = ? WHERE maHD = ?";
        String sqlBan = "UPDATE Ban SET trangThai = N'Trống' WHERE maBan = ?";

        try {
            con = ConnectDB.getConnection();
            con.setAutoCommit(false); 

            // Cập nhật trạng thái hóa đơn
            stmtHD = con.prepareStatement(sqlHD);
            stmtHD.setString(1, phuongThuc);
            stmtHD.setString(2, maHD);
            stmtHD.executeUpdate();

            // Cập nhật bàn về "Trống"
            if (maBan != null && !maBan.isEmpty()) {
                stmtBan = con.prepareStatement(sqlBan);
                stmtBan.setString(1, maBan);
                stmtBan.executeUpdate();
            }

            con.commit();
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
                if (stmtHD != null) stmtHD.close();
                if (stmtBan != null) stmtBan.close();
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}