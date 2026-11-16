CREATE DATABASE EvergreenDB;
GO

USE EvergreenDB;
GO

-- 2. Bảng Tài khoản
CREATE TABLE TaiKhoan (
    tenDangNhap VARCHAR(50) PRIMARY KEY,
    matKhau VARCHAR(255) NOT NULL,
    vaiTro VARCHAR(20) NOT NULL, -- Quản lý, Thu ngân
    trangThai VARCHAR(20) DEFAULT 'Hoạt động'
);
GO

-- 3. Bảng Nhân viên ({id}: NV + [XXX], tự động sinh từ NV001)
CREATE TABLE NhanVien (
    maNV VARCHAR(10) PRIMARY KEY,
    tenNV NVARCHAR(100) NOT NULL,
    gioiTinh NVARCHAR(10),
    ngaySinh DATE,
    soDienThoai VARCHAR(15),
    diaChi NVARCHAR(255),
    ngayVaoLam DATE DEFAULT GETDATE(),
    trangThai NVARCHAR(20) DEFAULT N'Đang làm việc',
    tenDangNhap VARCHAR(50) NULL,
    CONSTRAINT FK_NhanVien_TaiKhoan FOREIGN KEY (tenDangNhap) REFERENCES TaiKhoan(tenDangNhap)
);
GO

-- 4. Bảng Bàn ({id}: B + [XX], tự động sinh tăng dần)
CREATE TABLE Ban (
    maBan VARCHAR(10) PRIMARY KEY,
    tenBan NVARCHAR(50) NOT NULL,
    soChoNgoi INT CHECK (soChoNgoi > 0),
    trangThai NVARCHAR(20) DEFAULT N'Trống' -- Trống, Đang phục vụ, Đã đặt trước
);
GO

-- 5. Bảng Khách hàng ({id}: KH + [XXX], tự động sinh tăng dần khi đăng ký)
CREATE TABLE KhachHang (
    maKH VARCHAR(10) PRIMARY KEY,
    tenKH NVARCHAR(100) NOT NULL,
    soDienThoai VARCHAR(15) UNIQUE
);
GO

-- 6. Bảng Loại món ({id}: LM + [XX], tự động sinh tăng dần)
CREATE TABLE LoaiMon (
    maLoai VARCHAR(10) PRIMARY KEY,
    tenLoai NVARCHAR(50) NOT NULL,
    moTa NVARCHAR(255)
);
GO

-- 7. Bảng Món ({id}: M + [XXX], tự động sinh tăng dần)
CREATE TABLE Mon (
    maMon VARCHAR(10) PRIMARY KEY,
    tenMon NVARCHAR(100) NOT NULL,
    donViTinh NVARCHAR(20),
    trangThai NVARCHAR(20) DEFAULT N'Đang bán',
    giaNhap DECIMAL(12,2) CHECK (giaNhap >= 0),
    loiNhuan DECIMAL(12,2) CHECK (loiNhuan >= 0),
    -- donGia (Giá bán) là thuộc tính dẫn xuất tự động: donGia = giaNhap + loiNhuan
    donGia AS (giaNhap + loiNhuan),
    maLoai VARCHAR(10) NOT NULL,
    CONSTRAINT FK_Mon_LoaiMon FOREIGN KEY (maLoai) REFERENCES LoaiMon(maLoai)
);
GO

-- 8. Bảng Khuyến mãi ({id}: KM + [YYMMDD] + [XX], tự động sinh)
CREATE TABLE KhuyenMai (
    maKM VARCHAR(15) PRIMARY KEY,
    tenKM NVARCHAR(100) NOT NULL,
    phanTrangGiam DECIMAL(5,2) CHECK (phanTrangGiam >= 0 AND phanTrangGiam <= 1), -- Từ 0 đến 1 (VD: 0.10 tương ứng 10%)
    ngayBatDau DATE NOT NULL,
    ngayKetThuc DATE NOT NULL,
    donToiThieu DECIMAL(12,2) DEFAULT 0
);
GO

-- 9. Bảng Hóa đơn ({id}: HD + [YYMMDD] + [XXX], tự động sinh tăng dần trong ngày)
CREATE TABLE HoaDon (
    maHD VARCHAR(15) PRIMARY KEY,
    ngayLap DATETIME DEFAULT GETDATE(),
    phuongThucThanhToan NVARCHAR(30) DEFAULT N'Tiền mặt',
    trangThai NVARCHAR(20) DEFAULT N'Chưa thanh toán', -- Chưa thanh toán, Đã thanh toán, Đã hủy
    tienGiam DECIMAL(12,2) DEFAULT 0,
    thueVAT DECIMAL(5,2) DEFAULT 0.10, -- Mặc định 10%
    maNV VARCHAR(10) NOT NULL,
    maBan VARCHAR(10) NULL, -- Cho phép NULL nếu khách mua mang đi
    maKH VARCHAR(10) NULL,  -- Cho phép NULL nếu khách vãng lai
    maKM VARCHAR(15) NULL,  -- Cho phép NULL nếu không áp dụng mã giảm giá
    CONSTRAINT FK_HoaDon_NhanVien FOREIGN KEY (maNV) REFERENCES NhanVien(maNV),
    CONSTRAINT FK_HoaDon_Ban FOREIGN KEY (maBan) REFERENCES Ban(maBan),
    CONSTRAINT FK_HoaDon_KhachHang FOREIGN KEY (maKH) REFERENCES KhachHang(maKH),
    CONSTRAINT FK_HoaDon_KhuyenMai FOREIGN KEY (maKM) REFERENCES KhuyenMai(maKM)
);
GO

-- 10. Bảng Chi tiết hóa đơn (Bảng trung gian n-n giữa HoaDon và Mon)
CREATE TABLE ChiTietHoaDon (
    maHD VARCHAR(15),
    maMon VARCHAR(10),
    soLuong INT CHECK (soLuong > 0),
    donGia DECIMAL(12,2) NOT NULL, -- Lưu lại đơn giá thời điểm mua
    ghiChu NVARCHAR(255),
    -- thanhTien là thuộc tính dẫn xuất tự động: thanhTien = soLuong * donGia
    thanhTien AS (soLuong * donGia),
    CONSTRAINT PK_ChiTietHoaDon PRIMARY KEY (maHD, maMon),
    CONSTRAINT FK_CTHD_HoaDon FOREIGN KEY (maHD) REFERENCES HoaDon(maHD) ON DELETE CASCADE,
    CONSTRAINT FK_CTHD_Mon FOREIGN KEY (maMon) REFERENCES Mon(maMon)
);
GO