package entity;

public class Ban {
    private String maBan;
    private String tenBan;
    private int soChoNgoi;
    private String trangThai;

    public Ban() {
    }

    public Ban(String maBan, String tenBan, int soChoNgoi, String trangThai) {
        this.maBan = maBan;
        this.tenBan = tenBan;
        this.soChoNgoi = soChoNgoi;
        this.trangThai = trangThai;
    }

    public String getMaBan() {
        return maBan;
    }

    public void setMaBan(String maBan) {
        this.maBan = maBan;
    }

    public String getTenBan() {
        return tenBan;
    }

    public void setTenBan(String tenBan) {
        this.tenBan = tenBan;
    }

    public int getSoChoNgoi() {
        return soChoNgoi;
    }

    public void setSoChoNgoi(int soChoNgoi) {
        this.soChoNgoi = soChoNgoi;
    }

    public String getTrangThai() {
        return trangThai;
    }

    public void setTrangThai(String trangThai) {
        this.trangThai = trangThai;
    }

    @Override
    public String toString() {
        return "Ban{" + "maBan='" + maBan + '\'' + ", tenBan='" + tenBan + '\'' + ", trangThai='" + trangThai + '\'' + '}';
    }
}