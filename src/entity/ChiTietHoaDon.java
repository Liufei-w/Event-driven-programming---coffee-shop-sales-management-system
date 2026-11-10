package entity;

import java.math.BigDecimal;

public class ChiTietHoaDon {
    private HoaDon hoaDon;
    private Mon mon;
    private int soLuong;
    private BigDecimal donGia;
    private String ghiChu;

    public ChiTietHoaDon() {
    }

    public ChiTietHoaDon(HoaDon hoaDon, Mon mon, int soLuong, BigDecimal donGia, String ghiChu) {
        this.hoaDon = hoaDon;
        this.mon = mon;
        this.soLuong = soLuong;
        this.donGia = donGia;
        this.ghiChu = ghiChu;
    }

    // Thuộc tính dẫn xuất thanhTien = soLuong * donGia
    public BigDecimal getThanhTien() {
        if (donGia != null) {
            return donGia.multiply(BigDecimal.valueOf(soLuong));
        }
        return BigDecimal.ZERO;
    }

    public HoaDon getHoaDon() {
        return hoaDon;
    }

    public void setHoaDon(HoaDon hoaDon) {
        this.hoaDon = hoaDon;
    }

    public Mon getMon() {
        return mon;
    }

    public void setMon(Mon mon) {
        this.mon = mon;
    }

    public int getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(int soLuong) {
        this.soLuong = soLuong;
    }

    public BigDecimal getDonGia() {
        return donGia;
    }

    public void setDonGia(BigDecimal donGia) {
        this.donGia = donGia;
    }

    public String getGhiChu() {
        return ghiChu;
    }

    public void setGhiChu(String ghiChu) {
        this.ghiChu = ghiChu;
    }
}