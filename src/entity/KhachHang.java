package entity;

public class KhachHang {
    private String maKH;
    private String tenKH;
    private String soDienThoai;

    public KhachHang() {
    }

    public KhachHang(String maKH, String tenKH, String soDienThoai) {
        this.maKH = maKH;
        this.tenKH = tenKH;
        this.soDienThoai = soDienThoai;
    }

    public String getMaKH() {
        return maKH;
    }

    public void setMaKH(String maKH) {
        this.maKH = maKH;
    }

    public String getTenKH() {
        return tenKH;
    }

    public void setTenKH(String tenKH) {
        this.tenKH = tenKH;
    }

    public String getSoDienThoai() {
        return soDienThoai;
    }

    public void setSoDienThoai(String soDienThoai) {
        this.soDienThoai = soDienThoai;
    }
    
    public void congDiem(int soDiemThem) {
        System.out.println("Đã cộng " + soDiemThem + " điểm cho khách hàng: " + tenKH);
    }

    @Override
    public String toString() {
        if (maKH == null) {
            return tenKH;
        }
        return tenKH + " (" + (soDienThoai != null ? soDienThoai : maKH) + ")";
    }
}