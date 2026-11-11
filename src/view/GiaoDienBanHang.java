package view;

import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.LoaiMon;
import entity.Mon;
import service.BanHangService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.List;

public class GiaoDienBanHang extends JPanel implements ActionListener {
    private JPanel pnlSoDoBan;
    private JComboBox<String> cboLoaiMon;
    private JPanel pnlDanhSachMon;

    private JTable tblChiTietHoaDon;
    private DefaultTableModel modelTableCTHD;
    private JLabel lblMaHD, lblTenBan, lblTongTien;
    private JTextField txtGhiChu;
    private JSpinner spnSoLuong;
    private JButton btnThemMon, btnXoaMon, btnThanhToan, btnInHoaDon;

    private BanHangService banHangService;
    private Ban banDangChon = null;
    private Mon monDangChon = null; 

    public GiaoDienBanHang() {
        banHangService = new BanHangService();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. BÊN TRÁI: SƠ ĐỒ BÀN
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setBorder(BorderFactory.createTitledBorder("1. Sơ đồ bàn"));
        pnlTrai.setPreferredSize(new Dimension(260, 0));

        pnlSoDoBan = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlTrai.add(new JScrollPane(pnlSoDoBan), BorderLayout.CENTER);
        add(pnlTrai, BorderLayout.WEST);

        // 2. Ở GIỮA: THỰC ĐƠN MÓN NƯỚC
        JPanel pnlGiua = new JPanel(new BorderLayout(5, 5));
        pnlGiua.setBorder(BorderFactory.createTitledBorder("2. Thực đơn món nước"));

        JPanel pnlLocLoai = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLocLoai.add(new JLabel("Chọn danh mục:"));
        cboLoaiMon = new JComboBox<>();
        pnlLocLoai.add(cboLoaiMon);
        pnlGiua.add(pnlLocLoai, BorderLayout.NORTH);

        pnlDanhSachMon = new JPanel(new GridLayout(0, 3, 5, 5));
        pnlGiua.add(new JScrollPane(pnlDanhSachMon), BorderLayout.CENTER);
        add(pnlGiua, BorderLayout.CENTER);

        // 3. BÊN PHẢI: CHI TIẾT HÓA ĐƠN & THANH TOÁN
        JPanel pnlPhai = new JPanel(new BorderLayout(5, 5));
        pnlPhai.setBorder(BorderFactory.createTitledBorder("3. Chi tiết hóa đơn"));
        pnlPhai.setPreferredSize(new Dimension(420, 0));

        JPanel pnlThongTinHD = new JPanel(new GridLayout(2, 1, 3, 3));
        pnlThongTinHD.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JPanel pnlDong1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlDong1.add(new JLabel("Mã hóa đơn: "));
        lblMaHD = new JLabel("---");
        lblMaHD.setFont(new Font("Arial", Font.BOLD, 13));
        lblMaHD.setForeground(new Color(0, 102, 204));
        pnlDong1.add(lblMaHD);

        JPanel pnlDong2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlDong2.add(new JLabel("Bàn đang chọn: "));
        lblTenBan = new JLabel("Chưa chọn bàn");
        lblTenBan.setFont(new Font("Arial", Font.BOLD, 13));
        lblTenBan.setForeground(Color.RED);
        pnlDong2.add(lblTenBan);

        pnlThongTinHD.add(pnlDong1);
        pnlThongTinHD.add(pnlDong2);
        pnlPhai.add(pnlThongTinHD, BorderLayout.NORTH);

        String[] columnNames = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        modelTableCTHD = new DefaultTableModel(columnNames, 0);
        tblChiTietHoaDon = new JTable(modelTableCTHD);
        pnlPhai.add(new JScrollPane(tblChiTietHoaDon), BorderLayout.CENTER);

        JPanel pnlSouthPhai = new JPanel();
        pnlSouthPhai.setLayout(new BoxLayout(pnlSouthPhai, BoxLayout.Y_AXIS));
        pnlSouthPhai.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel pnlDongNhap = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlDongNhap.add(new JLabel("SL:"));
        spnSoLuong = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        pnlDongNhap.add(spnSoLuong);
        pnlDongNhap.add(new JLabel("Ghi chú:"));
        txtGhiChu = new JTextField(10);
        pnlDongNhap.add(txtGhiChu);
        
        btnThemMon = new JButton("Thêm");
        btnThemMon.setBackground(new Color(70, 130, 180));
        btnThemMon.setForeground(Color.WHITE);
        pnlDongNhap.add(btnThemMon);
        pnlSouthPhai.add(pnlDongNhap);

        JPanel pnlDongXoa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        btnXoaMon = new JButton("Xóa dòng chọn");
        pnlDongXoa.add(btnXoaMon);
        pnlSouthPhai.add(pnlDongXoa);

        JPanel pnlDongTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        pnlDongTongTien.add(new JLabel("Tổng thanh toán (gồm VAT 10%): "));
        lblTongTien = new JLabel("0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongTien.setForeground(Color.BLUE);
        pnlDongTongTien.add(lblTongTien);
        pnlSouthPhai.add(pnlDongTongTien);

        JPanel pnlDongThanhToan = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
        btnThanhToan = new JButton("THANH TOÁN");
        btnThanhToan.setFont(new Font("Arial", Font.BOLD, 13));
        btnThanhToan.setBackground(new Color(46, 139, 87));
        btnThanhToan.setForeground(Color.WHITE);
        btnThanhToan.setPreferredSize(new Dimension(140, 35));

        btnInHoaDon = new JButton("In Hóa Đơn");
        btnInHoaDon.setPreferredSize(new Dimension(110, 35));

        pnlDongThanhToan.add(btnThanhToan);
        pnlDongThanhToan.add(btnInHoaDon);
        pnlSouthPhai.add(pnlDongThanhToan);

        pnlPhai.add(pnlSouthPhai, BorderLayout.SOUTH);
        add(pnlPhai, BorderLayout.EAST);

        // Nạp dữ liệu ban đầu
        napDanhSachBan();
        napDanhMucLoaiMon();
        napDanhSachMon(null);

        // Đăng ký sự kiện
        cboLoaiMon.addActionListener(this);
        btnThemMon.addActionListener(this);
        btnXoaMon.addActionListener(this);
        btnThanhToan.addActionListener(this);
        btnInHoaDon.addActionListener(this);
    }

    private void napDanhSachBan() {
        pnlSoDoBan.removeAll();
        List<Ban> dsBan = banHangService.layDanhSachBan();
        for (Ban b : dsBan) {
            JButton btnBan = new JButton("<html><center><b>" + b.getTenBan() + "</b><br>(" + b.getTrangThai() + ")</center></html>");
            String trangThai = b.getTrangThai() != null ? b.getTrangThai().trim() : "";
            
            if (trangThai.equalsIgnoreCase("Trống")) {
                btnBan.setBackground(new Color(220, 245, 220));
            } else if (trangThai.equalsIgnoreCase("Đang phục vụ")) {
                btnBan.setBackground(new Color(255, 200, 200));
            } else {
                btnBan.setBackground(new Color(255, 240, 200));
            }
            
            btnBan.setActionCommand("BAN_" + b.getMaBan());
            btnBan.addActionListener(this);
            pnlSoDoBan.add(btnBan);
        }
        pnlSoDoBan.revalidate();
        pnlSoDoBan.repaint();
    }

    private void napDanhMucLoaiMon() {
        cboLoaiMon.removeAllItems();
        cboLoaiMon.addItem("Tất cả");
        List<LoaiMon> dsLoai = banHangService.layDanhSachLoaiMon();
        for (LoaiMon lm : dsLoai) {
            cboLoaiMon.addItem(lm.getTenLoai());
        }
    }

    private void napDanhSachMon(String tenLoai) {
        pnlDanhSachMon.removeAll();
        List<Mon> dsMon = banHangService.layDanhSachMonTheoLoai(tenLoai);

        for (Mon m : dsMon) {
            JButton btnMon = new JButton("<html><center><b>" + m.getTenMon() + "</b><br>" + m.getDonGia() + " đ</center></html>");
            btnMon.setName(m.getMaMon()); 
            btnMon.addActionListener(this);
            pnlDanhSachMon.add(btnMon);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }

    private void tinhTongTien() {
        double tongChuaVAT = 0;
        for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
            tongChuaVAT += Double.parseDouble(modelTableCTHD.getValueAt(i, 3).toString());
        }
        double tongThanhToan = banHangService.tinhTongTien(tongChuaVAT);
        lblTongTien.setText(String.format("%,.0f VNĐ", tongThanhToan));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(cboLoaiMon)) {
            napDanhSachMon((String) cboLoaiMon.getSelectedItem());
        } else if (o.equals(btnThemMon)) {
            xuLyThemMon();
        } else if (o.equals(btnXoaMon)) {
            xuLyXoaMon();
        } else if (o.equals(btnThanhToan)) {
            xuLyThanhToan();
        } else if (o.equals(btnInHoaDon)) {
            xuLyInHoaDon();
        } else if (o instanceof JButton) {
            JButton btn = (JButton) o;
            String cmd = btn.getActionCommand();

            if (cmd != null && cmd.startsWith("BAN_")) {
                xuLyChonBan(cmd.substring(4));
            } else if (btn.getName() != null && !btn.getName().isEmpty()) {
                xuLyChonMon(btn.getName());
            }
        }
    }

    private void xuLyThemMon() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước khi thêm món!");
            return;
        }
        if (monDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng click chọn món ở thực đơn trước!");
            return;
        }

        // Đảm bảo nếu bàn đang trống mà bấm thêm món thì mới tạo hóa đơn mới và đổi trạng thái bàn thành Đang phục vụ
        String maHD = lblMaHD.getText();
        if (maHD == null || maHD.equals("---")) {
            maHD = banHangService.taoMoiHoaDonChoBanNeuChuaCo(banDangChon.getMaBan());
            if (maHD != null) {
                lblMaHD.setText(maHD);
                napDanhSachBan(); // Cập nhật lại màu sắc bàn trên giao diện thành đỏ (Đang phục vụ)
            } else {
                JOptionPane.showMessageDialog(this, "Không thể khởi tạo hóa đơn mới cho bàn này!");
                return;
            }
        }

        int soLuong = (int) spnSoLuong.getValue();
        BigDecimal donGia = monDangChon.getDonGia();
        BigDecimal thanhTien = banHangService.tinhThanhTien(donGia, soLuong);

        boolean kqLuu = banHangService.luuChiTietHoaDon(maHD, monDangChon.getMaMon(), soLuong, donGia);
        if (kqLuu) {
            modelTableCTHD.addRow(new Object[]{monDangChon.getTenMon(), soLuong, donGia, thanhTien});
            tinhTongTien();
        } else {
            JOptionPane.showMessageDialog(this, "Lưu chi tiết hóa đơn vào CSDL thất bại!");
        }
    }

    private void xuLyXoaMon() {
        int row = tblChiTietHoaDon.getSelectedRow();
        if (row != -1) {
            modelTableCTHD.removeRow(row);
            tinhTongTien();
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng món cần xóa trong bảng!");
        }
    }

    private void xuLyThanhToan() {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Chưa chọn bàn để thanh toán!");
            return;
        }
        if (modelTableCTHD.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Hóa đơn chưa có món nào!");
            return;
        }

        boolean kq = banHangService.thucHienThanhToan(lblMaHD.getText(), "Tiền mặt", banDangChon.getMaBan());
        if (kq) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            modelTableCTHD.setRowCount(0);
            lblTongTien.setText("0 VNĐ");
            lblTenBan.setText("Chưa chọn bàn");
            lblMaHD.setText("---");
            banDangChon = null;
            monDangChon = null;
            napDanhSachBan();
        } else {
            JOptionPane.showMessageDialog(this, "Thanh toán thất bại, vui lòng thử lại!");
        }
    }

    private void xuLyInHoaDon() {
        String maHD = lblMaHD.getText();
        if (maHD == null || maHD.equals("---") || modelTableCTHD.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "Không có dữ liệu hóa đơn để in!");
            return;
        }

        HoaDon hd = banHangService.layHoaDonTheoMa(maHD);
        if (hd != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogInHoaDon dialogIn = new DialogInHoaDon(parentFrame, hd);
            dialogIn.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin hóa đơn trong CSDL!");
        }
    }

    private void xuLyChonBan(String maBan) {
        List<Ban> dsBan = banHangService.layDanhSachBan();
        for (Ban b : dsBan) {
            if (b.getMaBan().equals(maBan)) {
                banDangChon = b;
                lblTenBan.setText(b.getTenBan());
                
                // Lấy hóa đơn chưa thanh toán của bàn (nếu có)
                String maHD = banHangService.layHoacTaoMaHoaDonChoBan(b.getMaBan());
                
                if (maHD != null) {
                    // TRƯỜNG HỢP 1: Bàn đang có khách (đang phục vụ) -> Tải thông tin hóa đơn cũ lên
                    lblMaHD.setText(maHD);
                    modelTableCTHD.setRowCount(0);
                    
                    List<ChiTietHoaDon> dsCT = banHangService.layChiTietHoaDonTheoMaHD(maHD);
                    for (ChiTietHoaDon cthd : dsCT) {
                        BigDecimal thanhTien = cthd.getThanhTien();
                        modelTableCTHD.addRow(new Object[]{
                            cthd.getMon().getTenMon(), 
                            cthd.getSoLuong(), 
                            cthd.getDonGia(), 
                            thanhTien
                        });
                    }
                    tinhTongTien();
                } else {
                    // TRƯỜNG HỢP 2: Bàn đang trống -> Reset lại bảng chi tiết, chưa cần tạo hóa đơn vội
                    lblMaHD.setText("---");
                    modelTableCTHD.setRowCount(0);
                    lblTongTien.setText("0 VNĐ");
                    txtGhiChu.setText("");
                    spnSoLuong.setValue(1);
                }
                break;
            }
        }
    }

    private void xuLyChonMon(String maMon) {
        monDangChon = banHangService.layMonTheoMa(maMon);
        if (monDangChon != null) {
            JOptionPane.showMessageDialog(this, "Đã chọn món: " + monDangChon.getTenMon() + ". Bấm 'Thêm' để đưa vào hóa đơn.");
        }
    }
}