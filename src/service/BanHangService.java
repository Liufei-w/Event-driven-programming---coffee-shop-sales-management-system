package service;

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import dao.ChiTietHoaDon_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiMon;
import entity.Mon;
import entity.NhanVien;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class BanHangService {
    private Ban_DAO banDAO;
    private Mon_DAO monDAO;
    private LoaiMon_DAO loaiMonDAO;
    private HoaDon_DAO hoaDonDAO;
    private ChiTietHoaDon_DAO chiTietHoaDonDAO;

    public BanHangService() {
        banDAO = new Ban_DAO();
        monDAO = new Mon_DAO();
        loaiMonDAO = new LoaiMon_DAO();
        hoaDonDAO = new HoaDon_DAO();
        chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
    }

    public List<Ban> layDanhSachBan() {
        return banDAO.layTatCaBan();
    }

    public List<LoaiMon> layDanhSachLoaiMon() {
        return loaiMonDAO.layTatCaLoaiMon();
    }

    public List<Mon> layDanhSachMonTheoLoai(String tenLoai) {
        if (tenLoai == null || tenLoai.equals("Tất cả")) {
            return monDAO.layTatCaMon();
        } else {
            return monDAO.layMonTheoLoai(tenLoai);
        }
    }

    public Mon layMonTheoMa(String maMon) {
        return monDAO.layMonTheoMa(maMon);
    }

    public BigDecimal tinhThanhTien(BigDecimal donGia, int soLuong) {
        if (soLuong <= 0 || donGia == null || donGia.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Dữ liệu số lượng hoặc đơn giá không hợp lệ");
        }
        return donGia.multiply(BigDecimal.valueOf(soLuong));
    }

    public String taoMaHoaDonMoi() {
        return hoaDonDAO.phatSinhMaHD();
    }

    public double tinhTongTien(double tongTienChuaVAT) {
        return tongTienChuaVAT * 1.10; // VAT 10%
    }

    public String layHoacTaoMaHoaDonChoBan(String maBan) {
        // Chỉ lấy hóa đơn chưa thanh toán hiện có của bàn
        String maHD = hoaDonDAO.layMaHDChuaThanhToanTheoBan(maBan);
        
        // NẾU BÀN TRỐNG (chưa có hóa đơn chưa thanh toán), THÌ KHÔNG TỰ ĐỘNG TẠO HÓA ĐƠN VỘI.
        // Chỉ trả về null để giao diện biết là bàn này đang trống, 
        // khi nào khách gọi món bấm nút "Thêm" thì mới chính thức tạo hóa đơn và đổi trạng thái bàn thành "Đang phục vụ".
        if (maHD == null) {
            return null; 
        }
        return maHD;
    }
    
    public String taoMoiHoaDonChoBanNeuChuaCo(String maBan) {
        String maHD = hoaDonDAO.layMaHDChuaThanhToanTheoBan(maBan);
        if (maHD == null) {
            maHD = hoaDonDAO.phatSinhMaHD();
            boolean kq = khoiTaoHoaDonMoi(maHD, maBan);
            if (!kq) {
                return null;
            }
        }
        return maHD;
    }
    
    public boolean khoiTaoHoaDonMoi(String maHD, String maBan) {
        HoaDon hd = new HoaDon();
        hd.setMaHD(maHD);
        hd.setNgayLap(LocalDateTime.now());
        hd.setTrangThai("Chưa thanh toán");
        hd.setPhuongThucThanhToan("Tiền mặt");
        hd.setTienGiam(BigDecimal.ZERO);
        hd.setThueVAT(BigDecimal.valueOf(0.10));
        
        Ban ban = new Ban();
        ban.setMaBan(maBan);
        hd.setBan(ban);

        NhanVien nv = new NhanVien();
        nv.setMaNV("NV001"); // Nhân viên mặc định khi lập hóa đơn từ POS
        hd.setNhanVien(nv);

        hd.setDanhSachChiTiet(new ArrayList<>());

        return hoaDonDAO.taoHoaDon(hd);
    }
    
    public HoaDon layHoaDonTheoMa(String maHD) {
        return hoaDonDAO.layHoaDonTheoMa(maHD); // Gọi trực tiếp DAO tối ưu bằng SQL
    }

    public List<ChiTietHoaDon> layChiTietHoaDonTheoMaHD(String maHD) {
        return hoaDonDAO.layChiTietHoaDon(maHD);
    }
    
    public boolean luuChiTietHoaDon(String maHD, String maMon, int soLuong, BigDecimal donGia) {
        return chiTietHoaDonDAO.themChiTietHoaDon(maHD, maMon, soLuong, donGia);
    }

    public boolean thucHienThanhToan(String maHD, String phuongThuc, String maBan) {
        return hoaDonDAO.thanhToanHoaDon(maHD, phuongThuc, maBan);
    }
}