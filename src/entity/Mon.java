package entity;

import java.math.BigDecimal;

public class Mon {
    private String maMon;
    private String tenMon;
    private String donViTinh;
    private String trangThai;
    private BigDecimal giaNhap;
    private BigDecimal loiNhuan;
    
    private LoaiMon loaiMon;

    public Mon() {
    }

    public Mon(String maMon, String tenMon, String donViTinh, String trangThai, BigDecimal giaNhap, BigDecimal loiNhuan, LoaiMon loaiMon) {
        this.maMon = maMon;
        this.tenMon = tenMon;
        this.donViTinh = donViTinh;
        this.trangThai = trangThai;
        this.giaNhap = giaNhap;
        this.loiNhuan = loiNhuan;
        this.loaiMon = loaiMon;
    }

    // Thuộc tính dẫn xuất donGia = giaNhap + loiNhuan
    public BigDecimal getDonGia() {
        if (giaNhap != null && loiNhuan != null) {
            return giaNhap.add(loiNhuan);
        }
        return BigDecimal.ZERO;
    }

    public String getMaMon() {
        return maMon;
    }

    public void setMaMon(String maMon) {
        this.maMon = maMon;
    }

    public String getTenMon() {
        return tenMon;
    }

    public void setTenMon(String tenMon) {
        this.tenMon = tenMon;
    }

    public String getDonViTinh() {
        return donViTinh;
    }

    public void setDonViTinh(String donViTinh) {
        this.donViTinh = donViTinh;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    public BigDecimal getGiaNhap() {
        return giaNhap;
    }

    public void setGiaNhap(BigDecimal giaNhap) {
        this.giaNhap = giaNhap;
    }

    public BigDecimal getLoiNhuan() {
        return loiNhuan;
    }

    public void setLoiNhuan(BigDecimal loiNhuan) {
        this.loiNhuan = loiNhuan;
    }

    public LoaiMon getLoaiMon() {
        return loaiMon;
    }

    public void setLoaiMon(LoaiMon loaiMon) {
        this.loaiMon = loaiMon;
    }

    @Override
    public String toString() {
        return "Mon{" + "maMon='" + maMon + '\'' + ", tenMon='" + tenMon + '\'' + ", donGia=" + getDonGia() + '}';
    }
}