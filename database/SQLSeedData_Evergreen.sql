USE EvergreenDB;
GO

-- =============================================
-- 1. SEED DATA CHO BẢNG TaiKhoan
-- =============================================
INSERT INTO TaiKhoan (tenDangNhap, matKhau, vaiTro, trangThai) VALUES
('admin', 'admin123', N'Quản lý', N'Hoạt động'),
('thungan01', 'tn123456', N'Thu ngân', N'Hoạt động'),
('thungan02', 'tn123456', N'Thu ngân', N'Hoạt động'),
('nhanvien01', '123456', N'Thu ngân', N'Khóa');
GO

-- =============================================
-- 2. SEED DATA CHO BẢNG NhanVien
-- =============================================
INSERT INTO NhanVien (maNV, tenNV, gioiTinh, ngaySinh, soDienThoai, diaChi, ngayVaoLam, trangThai, tenDangNhap) VALUES
('NV001', N'Trương Công Duy', N'Nam', '2004-05-15', '0901234567', N'12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM', '2025-01-10', N'Đang làm việc', 'admin'),
('NV002', N'Phan Xuân Phụng', N'Nữ', '2004-08-20', '0912345678', N'45 Lê Đức Thọ, Gò Vấp, TP.HCM', '2025-03-15', N'Đang làm việc', 'thungan01'),
('NV003', N'Nguyễn Đỗ Anh Tuấn', N'Nam', '2004-11-02', '0923456789', N'78 Quang Trung, Gò Vấp, TP.HCM', '2025-06-01', N'Đang làm việc', 'thungan02'),
('NV004', N'Nguyễn Văn A', N'Nam', '2002-03-10', '0934567890', N'100 Phạm Văn Đồng, Bình Thạnh, TP.HCM', '2025-02-01', N'Đã nghỉ việc', 'nhanvien01');
GO

-- =============================================
-- 3. SEED DATA CHO BẢNG Ban
-- =============================================
INSERT INTO Ban (maBan, tenBan, soChoNgoi, trangThai) VALUES
('B01', N'Bàn 01 (Tầng trệt)', 2, N'Trống'),
('B02', N'Bàn 02 (Tầng trệt)', 4, N'Đang phục vụ'),
('B03', N'Bàn 03 (Tầng trệt)', 4, N'Đang phục vụ'),
('B04', N'Bàn 04 (Tầng trệt)', 6, N'Trống'),
('B05', N'Bàn VIP 01 (Lầu 1)', 8, N'Đã đặt trước'),
('B06', N'Bàn Sân vườn 01', 4, N'Trống');
GO

-- =============================================
-- 4. SEED DATA CHO BẢNG KhachHang
-- =============================================
INSERT INTO KhachHang (maKH, tenKH, soDienThoai) VALUES
('KH001', N'Trần Thị Mai', '0988111222'),
('KH002', N'Lê Hoàng Nam', '0977222333'),
('KH003', N'Pham Văn Khánh', '0966333444'),
('KH004', N'Đặng Thu Hà', '0955444555');
GO

-- =============================================
-- 5. SEED DATA CHO BẢNG LoaiMon
-- =============================================
INSERT INTO LoaiMon (maLoai, tenLoai, moTa) VALUES
('LM01', N'Cà Phê Truyền Thống', N'Cà phê pha phin và pha máy đậm đà chuẩn vị'),
('LM02', N'Trà & Trà Trái Cây', N'Các dòng trà thanh mát kết hợp trái cây tươi'),
('LM03', N'Đá Xay & Smoothie', N'Đồ uống đá xay mát lạnh phủ kem tươi'),
('LM04', N'Bánh Ngọt & Snack', N'Các loại bánh ăn kèm thích hợp với cà phê');
GO

-- =============================================
-- 6. SEED DATA CHO BẢNG Mon
-- =============================================
INSERT INTO Mon (maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, maLoai) VALUES
('M001', N'Cà Phê Đen Đá', N'Ly', N'Đang bán', 10000.00, 15000.00, 'LM01'),   -- Giá bán: 25,000
('M002', N'Cà Phê Sữa Đá', N'Ly', N'Đang bán', 12000.00, 17000.00, 'LM01'),   -- Giá bán: 29,000
('M003', N'Bạc Xỉu Evergreen', N'Ly', N'Đang bán', 14000.00, 21000.00, 'LM01'),-- Giá bán: 35,000
('M004', N'Trà Đào Cam Sả', N'Ly', N'Đang bán', 15000.00, 24000.00, 'LM02'),  -- Giá bán: 39,000
('M005', N'Trà Vải Dưa Hấu', N'Ly', N'Đang bán', 16000.00, 26000.00, 'LM02'),  -- Giá bán: 42,000
('M006', N'Matcha Ice Blended', N'Ly', N'Đang bán', 20000.00, 29000.00, 'LM03'),-- Giá bán: 49,000
('M007', N'Bánh Tiramisu', N'Cái', N'Đang bán', 18000.00, 27000.00, 'LM04'),  -- Giá bán: 45,000
('M008', N'Bánh Croissant Bơ', N'Cái', N'Ngừng bán', 15000.00, 20000.00, 'LM04');-- Giá bán: 35,000
GO

-- =============================================
-- 7. SEED DATA CHO BẢNG KhuyenMai
-- =============================================
INSERT INTO KhuyenMai (maKM, tenKM, phanTrangGiam, ngayBatDau, ngayKetThuc, donToiThieu) VALUES
('KM26111001', N'Ưu đãi Khai Trương Evergreen', 0.20, '2026-11-01', '2026-11-30', 50000.00), -- Giảm 20%
('KM26111002', N'Giảm Giá Thành Viên', 0.10, '2026-01-01', '2026-12-31', 0.00),        -- Giảm 10%
('KM26111003', N'Tri ân Khách Hàng', 0.15, '2026-11-10', '2026-11-20', 100000.00);    -- Giảm 15%
GO

-- =============================================
-- 8. SEED DATA CHO BẢNG HoaDon
-- =============================================
INSERT INTO HoaDon (maHD, ngayLap, phuongThucThanhToan, trangThai, tienGiam, thueVAT, maNV, maBan, maKH, maKM) VALUES
('HD261110001', '2026-11-10 08:30:00', N'Tiền mặt', N'Đã thanh toán', 0.00, 0.10, 'NV002', 'B01', NULL, NULL),                    -- Mua mang đi/Khách lẻ
('HD261110002', '2026-11-10 09:15:00', N'Chuyển khoản', N'Đã thanh toán', 12800.00, 0.10, 'NV002', 'B02', 'KH001', 'KM26111001'), -- Dùng mã KM01
('HD261110003', '2026-11-10 10:00:00', N'Thẻ', N'Chưa thanh toán', 0.00, 0.10, 'NV003', 'B03', 'KH002', NULL),                    -- Đang phục vụ
('HD261110004', '2026-11-10 10:45:00', N'Tiền mặt', N'Đã hủy', 0.00, 0.10, 'NV003', NULL, NULL, NULL);                            -- Hóa đơn hủy

GO

-- =============================================
-- 9. SEED DATA CHO BẢNG ChiTietHoaDon
-- =============================================
INSERT INTO ChiTietHoaDon (maHD, maMon, soLuong, donGia, ghiChu) VALUES
-- Cho hóa đơn HD261110001
('HD261110001', 'M001', 2, 25000.00, N'Ít đường'),
('HD261110001', 'M007', 1, 45000.00, NULL),

-- Cho hóa đơn HD261110002
('HD261110002', 'M004', 2, 39000.00, N'Nhiều đá'),
('HD261110002', 'M005', 1, 42000.00, NULL),

-- Cho hóa đơn HD261110003
('HD261110003', 'M002', 3, 29000.00, N'Nhiều sữa'),
('HD261110003', 'M006', 1, 49000.00, N'Thêm kem tươi'),

-- Cho hóa đơn HD261110004 (Đã hủy)
('HD261110004', 'M003', 1, 35000.00, NULL);
GO