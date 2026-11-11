package view;

import dao.Ban_DAO;
import dao.HoaDon_DAO;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.Ban;
import entity.LoaiMon;
import entity.Mon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    private Ban_DAO banDAO;
    private Mon_DAO monDAO;
    private LoaiMon_DAO loaiMonDAO;
    private HoaDon_DAO hoaDonDAO;

    private Ban banDangChon = null;
    private Mon monDangChon = null; // Lưu tạm món đang được chọn để thêm

    public GiaoDienBanHang() {
        banDAO = new Ban_DAO();
        monDAO = new Mon_DAO();
        loaiMonDAO = new LoaiMon_DAO();
        hoaDonDAO = new HoaDon_DAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // ==========================================
        // 1. BÊN TRÁI: SƠ ĐỒ BÀN (WEST)
        // ==========================================
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setBorder(BorderFactory.createTitledBorder("1. Sơ đồ bàn"));
        pnlTrai.setPreferredSize(new Dimension(260, 0));

        pnlSoDoBan = new JPanel(new GridLayout(0, 2, 5, 5)); // Đổi thành 2 cột cho nút bàn to rõ hơn
        JScrollPane scrSoDoBan = new JScrollPane(pnlSoDoBan);
        pnlTrai.add(scrSoDoBan, BorderLayout.CENTER);

        add(pnlTrai, BorderLayout.WEST);

        // ==========================================
        // 2. Ở GIỮA: THỰC ĐƠN QUÁN (CENTER)
        // ==========================================
        JPanel pnlGiua = new JPanel(new BorderLayout(5, 5));
        pnlGiua.setBorder(BorderFactory.createTitledBorder("2. Thực đơn món nước"));

        JPanel pnlLocLoai = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlLocLoai.add(new JLabel("Chọn danh mục:"));
        cboLoaiMon = new JComboBox<>();
        pnlLocLoai.add(cboLoaiMon);
        pnlGiua.add(pnlLocLoai, BorderLayout.NORTH);

        pnlDanhSachMon = new JPanel(new GridLayout(0, 3, 5, 5)); // 3 cột thực đơn cân đối
        JScrollPane scrMon = new JScrollPane(pnlDanhSachMon);
        pnlGiua.add(scrMon, BorderLayout.CENTER);

        add(pnlGiua, BorderLayout.CENTER);

        // ==========================================
        // 3. BÊN PHẢI: HÓA ĐƠN & THANH TOÁN (EAST)
        // ==========================================
        JPanel pnlPhai = new JPanel(new BorderLayout(5, 5));
        pnlPhai.setBorder(BorderFactory.createTitledBorder("3. Chi tiết hóa đơn"));
        pnlPhai.setPreferredSize(new Dimension(420, 0));

        // Thông tin chung phía trên
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

        // Bảng danh sách món đã gọi ở giữa
        String[] columnNames = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        modelTableCTHD = new DefaultTableModel(columnNames, 0);
        tblChiTietHoaDon = new JTable(modelTableCTHD);
        JScrollPane scrTable = new JScrollPane(tblChiTietHoaDon);
        pnlPhai.add(scrTable, BorderLayout.CENTER);

        // Khu vực thao tác và tổng tiền ở phía dưới (Dùng BoxLayout dọc cho ngăn nắp)
        JPanel pnlSouthPhai = new JPanel();
        pnlSouthPhai.setLayout(new BoxLayout(pnlSouthPhai, BoxLayout.Y_AXIS));
        pnlSouthPhai.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Dòng nhập Số lượng, Ghi chú và Nút Thêm món
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

        // Dòng nút Xóa món
        JPanel pnlDongXoa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        btnXoaMon = new JButton("Xóa dòng chọn");
        pnlDongXoa.add(btnXoaMon);
        pnlSouthPhai.add(pnlDongXoa);

        // Dòng hiển thị Tổng tiền
        JPanel pnlDongTongTien = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        pnlDongTongTien.add(new JLabel("Tổng thanh toán (gồm VAT 10%): "));
        lblTongTien = new JLabel("0 VNĐ");
        lblTongTien.setFont(new Font("Arial", Font.BOLD, 15));
        lblTongTien.setForeground(Color.BLUE);
        pnlDongTongTien.add(lblTongTien);
        pnlSouthPhai.add(pnlDongTongTien);

        // Dòng chứa nút Thanh toán và In hóa đơn
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

        // Tải dữ liệu ban đầu
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
        List<Ban> dsBan = banDAO.layTatCaBan();
        for (Ban b : dsBan) {
            JButton btnBan = new JButton("<html><center><b>" + b.getTenBan() + "</b><br>(" + b.getTrangThai() + ")</center></html>");

            if (b.getTrangThai().equalsIgnoreCase("Trống")) {
                btnBan.setBackground(new Color(220, 245, 220));
            } else {
                btnBan.setBackground(new Color(255, 200, 200));
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
        List<LoaiMon> dsLoai = loaiMonDAO.layTatCaLoaiMon();
        for (LoaiMon lm : dsLoai) {
            cboLoaiMon.addItem(lm.getTenLoai());
        }
    }

    private void napDanhSachMon(String maLoai) {
        pnlDanhSachMon.removeAll();
        List<Mon> dsMon;
        if (maLoai == null || maLoai.equals("Tất cả")) {
            dsMon = monDAO.layTatCaMon();
        } else {
            dsMon = monDAO.layMonTheoLoai(maLoai);
        }

        for (Mon m : dsMon) {
            JButton btnMon = new JButton("<html><center><b>" + m.getTenMon() + "</b><br>" + m.getDonGia() + " đ</center></html>");

            // Truyền thông tin món qua ActionCommand
            btnMon.setActionCommand("MON_" + m.getMaMon() + "_" + m.getTenMon() + "_" + m.getDonGia());
            btnMon.addActionListener(this);
            pnlDanhSachMon.add(btnMon);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }

    private void tinhTongTien() {
        double tong = 0;
        for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
            tong += (double) modelTableCTHD.getValueAt(i, 3);
        }
        tong = tong * 1.10; // VAT 10%
        lblTongTien.setText(String.format("%,.0f VNĐ", tong));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(cboLoaiMon)) {
            String tenLoaiChon = (String) cboLoaiMon.getSelectedItem();
            // Lấy mã loại tương ứng nếu cần, ở đây truyền tên tương đối
            napDanhSachMon(tenLoaiChon);
        } 
        else if (o.equals(btnThemMon)) {
            if (banDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn trước khi thêm món!");
                return;
            }
            if (monDangChon == null) {
                JOptionPane.showMessageDialog(this, "Vui lòng click chọn món ở thực đơn trước!");
                return;
            }
            int soLuong = (int) spnSoLuong.getValue();
            double donGia = monDangChon.getDonGia().doubleValue();
            double thanhTien = soLuong * donGia;

            modelTableCTHD.addRow(new Object[]{monDangChon.getTenMon(), soLuong, donGia, thanhTien});
            tinhTongTien();
        }
        else if (o.equals(btnXoaMon)) {
            int row = tblChiTietHoaDon.getSelectedRow();
            if (row != -1) {
                modelTableCTHD.removeRow(row);
                tinhTongTien();
            } else {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng món cần xóa trong bảng!");
            }
        } 
        else if (o.equals(btnThanhToan)) {
            if (banDangChon == null) {
                JOptionPane.showMessageDialog(this, "Chưa chọn bàn để thanh toán!");
                return;
            }
            if (modelTableCTHD.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Hóa đơn chưa có món nào!");
                return;
            }

            boolean kq = hoaDonDAO.thanhToanHoaDon(lblMaHD.getText(), "Tiền mặt", banDangChon.getMaBan());
            if (kq) {
                JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
                modelTableCTHD.setRowCount(0);
                lblTongTien.setText("0 VNĐ");
                lblTenBan.setText("Chưa chọn bàn");
                lblMaHD.setText("---");
                banDangChon = null;
                monDangChon = null;
                napDanhSachBan();
            }
        } 
        else if (o.equals(btnInHoaDon)) {
            if (modelTableCTHD.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Không có dữ liệu hóa đơn để in!");
                return;
            }
            JOptionPane.showMessageDialog(this, "Đang mở cửa sổ in hóa đơn...");
        } 
        else if (e.getSource() instanceof JButton) {
            JButton btn = (JButton) e.getSource();
            String cmd = btn.getActionCommand();

            if (cmd != null && cmd.startsWith("BAN_")) {
                String maBan = cmd.substring(4);
                List<Ban> dsBan = banDAO.layTatCaBan();
                for (Ban b : dsBan) {
                    if (b.getMaBan().equals(maBan)) {
                        banDangChon = b;
                        lblTenBan.setText(b.getTenBan());
                        lblMaHD.setText(hoaDonDAO.phatSinhMaHD());
                        break;
                    }
                }
            } 
            else if (cmd != null && cmd.startsWith("MON_")) {
                String[] parts = cmd.split("_");
                String maMon = parts[1];
                String tenMon = parts[2];
                double donGia = Double.parseDouble(parts[3]);

                // Lưu tạm thông tin món vừa click
                monDangChon = new Mon();
                monDangChon.setMaMon(maMon);
                monDangChon.setTenMon(tenMon);
                monDangChon.setGiaNhap(java.math.BigDecimal.valueOf(donGia)); 
                monDangChon.setLoiNhuan(java.math.BigDecimal.ZERO); // Đơn giá đã gồm giá nhập + lợi nhuận

                JOptionPane.showMessageDialog(this, "Đã chọn món: " + tenMon + ". Bấm 'Thêm' để đưa vào hóa đơn.");
            }
        }
    }
}