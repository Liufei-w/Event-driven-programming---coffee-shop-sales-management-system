package entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class HoaDon {
    private String maHD;
    private LocalDateTime ngayLap;
    private String phuongThucThanhToan;
    private String trangThai;
    private BigDecimal tienGiam;
    private BigDecimal thueVAT;
    
    private NhanVien nhanVien;
    private Ban ban;
    private KhachHang khachHang;
    private KhuyenMai khuyenMai;
    private List<ChiTietHoaDon> danhSachChiTiet;

    public HoaDon() {
        this.danhSachChiTiet = new ArrayList<>();
    }

    public HoaDon(String maHD, LocalDateTime ngayLap, String phuongThucThanhToan, String trangThai, BigDecimal tienGiam, BigDecimal thueVAT, NhanVien nhanVien, Ban ban, KhachHang khachHang, KhuyenMai khuyenMai) {
        this.maHD = maHD;
        this.ngayLap = ngayLap;
        this.phuongThucThanhToan = phuongThucThanhToan;
        this.trangThai = trangThai;
        this.tienGiam = tienGiam;
        this.thueVAT = thueVAT;
        this.nhanVien = nhanVien;
        this.ban = ban;
        this.khachHang = khachHang;
        this.khuyenMai = khuyenMai;
        this.danhSachChiTiet = new ArrayList<>();
    }

    // Thuộc tính dẫn xuất: tính tổng tiền hóa đơn từ chi tiết + VAT - tiền giảm
    public BigDecimal getTongTien() {
        BigDecimal tongTienMon = BigDecimal.ZERO;
        for (ChiTietHoaDon cthd : danhSachChiTiet) {
            tongTienMon = tongTienMon.add(cthd.getThanhTien());
        }
        
        // Trừ tiền giảm khuyến mãi (nếu có)
        BigDecimal sauGiam = tongTienMon.subtract(tienGiam != null ? tienGiam : BigDecimal.ZERO);
        if (sauGiam.compareTo(BigDecimal.ZERO) < 0) {
            sauGiam = BigDecimal.ZERO;
        }
        
        // Cộng thuế VAT (mặc định 10% tương ứng 0.10)
        BigDecimal tienVAT = sauGiam.multiply(thueVAT != null ? thueVAT : BigDecimal.valueOf(0.10));
        
        return sauGiam.add(tienVAT);
    }

    
    public BigDecimal tinhTongTien() {
        return getTongTien();
    }

    public BigDecimal apDungKhuyenMai(KhuyenMai km) {
        this.khuyenMai = km;
        if (km != null && km.getPhanTramGiam() != null) {
            BigDecimal tongTienMon = BigDecimal.ZERO;
            for (ChiTietHoaDon cthd : danhSachChiTiet) {
                tongTienMon = tongTienMon.add(cthd.getThanhTien());
            }
            this.tienGiam = tongTienMon.multiply(km.getPhanTramGiam());
        } else {
            this.tienGiam = BigDecimal.ZERO;
        }
        return this.tienGiam;
    }

    public boolean thanhToan(String phuongThuc) {
        this.phuongThucThanhToan = phuongThuc;
        this.trangThai = "Đã thanh toán";
        return true;
    }

    public String getMaHD() {
        return maHD;
    }

    public void setMaHD(String maHD) {
        this.maHD = maHD;
    }

    public LocalDateTime getNgayLap() {
        return ngayLap;
    }

    public void setNgayLap(LocalDateTime ngayLap) {
        this.ngayLap = ngayLap;
    }

    public String getPhuongThucThanhToan() {
        return phuongThucThanhToan;
    }

    public void setPhuongThucThanhToan(String phuongThucThanhToan) {
        this.phuongThucThanhToan = phuongThucThanhToan;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getTienGiam() {
        return tienGiam;
    }

    public void setTienGiam(BigDecimal tienGiam) {
        this.tienGiam = tienGiam;
    }

    public BigDecimal getThueVAT() {
        return thueVAT;
    }

    public void setThueVAT(BigDecimal thueVAT) {
        this.thueVAT = thueVAT;
    }

    public NhanVien getNhanVien() {
        return nhanVien;
    }

    public void setNhanVien(NhanVien nhanVien) {
        this.nhanVien = nhanVien;
    }

    public Ban getBan() {
        return ban;
    }

    public void setBan(Ban ban) {
        this.ban = ban;
    }

    public KhachHang getKhachHang() {
        return khachHang;
    }

    public void setKhachHang(KhachHang khachHang) {
        this.khachHang = khachHang;
    }

    public KhuyenMai getKhuyenMai() {
        return khuyenMai;
    }

    public void setKhuyenMai(KhuyenMai khuyenMai) {
        this.khuyenMai = khuyenMai;
    }

    public List<ChiTietHoaDon> getDanhSachChiTiet() {
        return danhSachChiTiet;
    }

    public void setDanhSachChiTiet(List<ChiTietHoaDon> danhSachChiTiet) {
        this.danhSachChiTiet = danhSachChiTiet;
    }

    @Override
    public String toString() {
        return "HoaDon{" + "maHD='" + maHD + '\'' + ", tongTien=" + getTongTien() + ", trangThai='" + trangThai + '\'' + '}';
    }
}