package view;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhuyenMai_DAO;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhuyenMai;
import entity.LoaiMon;
import entity.Mon;
import entity.NhanVien;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GiaoDienBanHang extends JPanel implements ActionListener {
    private JPanel pnlSoDoBan;
    private JComboBox<String> cboLoaiMon;
    private JPanel pnlDanhSachMon;

    private JTable tblChiTietHoaDon;
    private DefaultTableModel modelTableCTHD;
    private JLabel lblMaHD, lblTenBan, lblTongTien, lblMonDangChon;
    private JTextField txtGhiChu;
    private JSpinner spnSoLuong;
    private JButton btnThemMon, btnXoaMon, btnThanhToan, btnInHoaDon;

    private Ban_DAO banDAO;
    private Mon_DAO monDAO;
    private LoaiMon_DAO loaiMonDAO;
    private HoaDon_DAO hoaDonDAO;
    private ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    
    private JComboBox<KhuyenMai> cboKhuyenMai;
    
    private Ban banDangChon = null;
    private Mon monDangChon = null; 

    public GiaoDienBanHang() {
        try {
            ConnectDB.getInstance().connect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        banDAO = new Ban_DAO();
        monDAO = new Mon_DAO();
        loaiMonDAO = new LoaiMon_DAO();
        hoaDonDAO = new HoaDon_DAO();
        chiTietHoaDonDAO = new ChiTietHoaDon_DAO();
        khuyenMaiDAO = new KhuyenMai_DAO();

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
        txtGhiChu = new JTextField(8);
        pnlDongNhap.add(txtGhiChu);
        
        btnThemMon = new JButton("Thêm Món");
        btnThemMon.setFont(new Font("Arial", Font.BOLD, 12));
        btnThemMon.setBackground(new Color(70, 130, 180));
        btnThemMon.setForeground(Color.WHITE);
        btnThemMon.setPreferredSize(new Dimension(105, 32));
        pnlDongNhap.add(btnThemMon);
        pnlSouthPhai.add(pnlDongNhap);

        JPanel pnlDongThongBaoChonMon = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        pnlDongThongBaoChonMon.add(new JLabel("Đang chọn: "));
        lblMonDangChon = new JLabel("Chưa chọn món nào");
        lblMonDangChon.setFont(new Font("Arial", Font.ITALIC | Font.BOLD, 12));
        lblMonDangChon.setForeground(new Color(180, 0, 0));
        pnlDongThongBaoChonMon.add(lblMonDangChon);
        pnlSouthPhai.add(pnlDongThongBaoChonMon);

        JPanel pnlDongXoa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        btnXoaMon = new JButton("Xóa dòng chọn");
        pnlDongXoa.add(btnXoaMon);
        pnlSouthPhai.add(pnlDongXoa);

        // Panel chọn Khuyến mãi
        JPanel pnlDongKM = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlDongKM.add(new JLabel("Khuyến mãi:"));
        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.setPreferredSize(new Dimension(300, 28));
        pnlDongKM.add(cboKhuyenMai);
        pnlSouthPhai.add(pnlDongKM); 

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
        napDanhSachKhuyenMai();

        // Đăng ký sự kiện
        cboLoaiMon.addActionListener(this);
        cboKhuyenMai.addActionListener(this);
        btnThemMon.addActionListener(this);
        btnXoaMon.addActionListener(this);
        btnThanhToan.addActionListener(this);
        btnInHoaDon.addActionListener(this);
    }

    // =============
    // CÁC HÀM XỬ LÝ 
    // =============

    private void napDanhSachBan() {
        pnlSoDoBan.removeAll();
        List<Ban> dsBan = banDAO.layTatCaBan();
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
        List<LoaiMon> dsLoai = loaiMonDAO.layTatCaLoaiMon();
        for (LoaiMon lm : dsLoai) {
            cboLoaiMon.addItem(lm.getTenLoai());
        }
    }

    private void napDanhSachMon(String tenLoai) {
        pnlDanhSachMon.removeAll();
        List<Mon> dsMon;
        if (tenLoai == null || tenLoai.equals("Tất cả")) {
            dsMon = monDAO.layTatCaMon();
        } else {
            dsMon = monDAO.layMonTheoLoai(tenLoai);
        }

        for (Mon m : dsMon) {
            JButton btnMon = new JButton("<html><center><b>" + m.getTenMon() + "</b><br>" + m.getDonGia() + " đ</center></html>");
            btnMon.setName(m.getMaMon()); 
            btnMon.addActionListener(this);
            pnlDanhSachMon.add(btnMon);
        }
        pnlDanhSachMon.revalidate();
        pnlDanhSachMon.repaint();
    }
    
    private void napDanhSachKhuyenMai() {
        if (cboKhuyenMai == null) return;
        
        cboKhuyenMai.removeActionListener(this); 
        cboKhuyenMai.removeAllItems();
        
        KhuyenMai kmMacDinh = new KhuyenMai(null, "-- Không áp dụng khuyến mãi --", BigDecimal.ZERO, null, null, BigDecimal.ZERO);
        cboKhuyenMai.addItem(kmMacDinh);

        BigDecimal tongChuaVAT = BigDecimal.ZERO;
        for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
            BigDecimal thanhTienRow = new BigDecimal(modelTableCTHD.getValueAt(i, 3).toString());
            tongChuaVAT = tongChuaVAT.add(thanhTienRow);
        }

        List<KhuyenMai> dsKM = khuyenMaiDAO.layKhuyenMaiHopLe(tongChuaVAT);
        for (KhuyenMai km : dsKM) {
            cboKhuyenMai.addItem(km);
        }

        cboKhuyenMai.addActionListener(this); 
    }

    private void tinhTongTien() {
        BigDecimal tongChuaVAT = BigDecimal.ZERO;
        for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
            tongChuaVAT = tongChuaVAT.add(new BigDecimal(modelTableCTHD.getValueAt(i, 3).toString()));
        }

        BigDecimal tienGiamGia = BigDecimal.ZERO;
        if (cboKhuyenMai != null && cboKhuyenMai.getSelectedItem() instanceof KhuyenMai) {
            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            if (km != null && km.getPhanTramGiam() != null) {
                tienGiamGia = tongChuaVAT.multiply(km.getPhanTramGiam());
            }
        }

        BigDecimal sauGiam = tongChuaVAT.subtract(tienGiamGia);
        if (sauGiam.compareTo(BigDecimal.ZERO) < 0) sauGiam = BigDecimal.ZERO;

        BigDecimal tongThanhToan = sauGiam.multiply(BigDecimal.valueOf(1.10));
        lblTongTien.setText(String.format("%,.0f VNĐ", tongThanhToan));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(cboLoaiMon)) {
            napDanhSachMon((String) cboLoaiMon.getSelectedItem());
        } else if (o.equals(cboKhuyenMai)) {
            tinhTongTien();
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
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món ở thực đơn trước!");
            return;
        }

        String maHD = lblMaHD.getText();
        if (maHD == null || maHD.equals("---")) {
            maHD = hoaDonDAO.layMaHDChuaThanhToanTheoBan(banDangChon.getMaBan());
            if (maHD == null) {
                maHD = hoaDonDAO.phatSinhMaHD();
                
                HoaDon hd = new HoaDon();
                hd.setMaHD(maHD);
                hd.setNgayLap(LocalDateTime.now());
                hd.setTrangThai("Chưa thanh toán");
                hd.setPhuongThucThanhToan("Tiền mặt");
                
                KhuyenMai selectedKM = (KhuyenMai) cboKhuyenMai.getSelectedItem();
                if (selectedKM != null && selectedKM.getMaKM() != null) {
                    hd.setKhuyenMai(selectedKM);
                    BigDecimal tongChuaVAT = BigDecimal.ZERO;
                    for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
                        tongChuaVAT = tongChuaVAT.add(new BigDecimal(modelTableCTHD.getValueAt(i, 3).toString()));
                    }
                    hd.setTienGiam(tongChuaVAT.multiply(selectedKM.getPhanTramGiam()));
                } else {
                    hd.setTienGiam(BigDecimal.ZERO);
                }

                hd.setThueVAT(BigDecimal.valueOf(0.10));
                
                Ban ban = new Ban();
                ban.setMaBan(banDangChon.getMaBan());
                hd.setBan(ban);

                NhanVien nv = new NhanVien();
                nv.setMaNV("NV001"); 
                hd.setNhanVien(nv);
                hd.setDanhSachChiTiet(new ArrayList<>());

                boolean kqTao = hoaDonDAO.taoHoaDon(hd);
                if (!kqTao) {
                    JOptionPane.showMessageDialog(this, "Không thể khởi tạo hóa đơn mới cho bàn này!");
                    return;
                }
            }
            lblMaHD.setText(maHD);
            napDanhSachBan(); 
        }

        int soLuongThem = (int) spnSoLuong.getValue();
        BigDecimal donGia = monDangChon.getDonGia();

        // 1. Lưu xuống CSDL (Đã có logic tự động cộng dồn trong DAO)
        boolean kqLuu = chiTietHoaDonDAO.themChiTietHoaDon(maHD, monDangChon.getMaMon(), soLuongThem, donGia);
        
        if (kqLuu) {
            // 2. Kiểm tra xem trên bảng JTable đã có món này chưa, nếu có rồi thì cộng dồn số lượng trực tiếp trên UI
            boolean daCoTrenBang = false;
            for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
                String tenMonInRow = modelTableCTHD.getValueAt(i, 0).toString();
                if (tenMonInRow.equals(monDangChon.getTenMon())) {
                    int slCu = Integer.parseInt(modelTableCTHD.getValueAt(i, 1).toString());
                    int slMoi = slCu + soLuongThem;
                    BigDecimal thanhTienMoi = donGia.multiply(BigDecimal.valueOf(slMoi));
                    
                    // Cập nhật lại giá trị trên dòng đó
                    modelTableCTHD.setValueAt(slMoi, i, 1);
                    modelTableCTHD.setValueAt(thanhTienMoi, i, 3);
                    daCoTrenBang = true;
                    break;
                }
            }

            // Nếu chưa có trên bảng thì thêm dòng mới bình thường
            if (!daCoTrenBang) {
                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuongThem));
                modelTableCTHD.addRow(new Object[]{monDangChon.getTenMon(), soLuongThem, donGia, thanhTien});
            }

            tinhTongTien();
            napDanhSachKhuyenMai(); 
        } else {
            JOptionPane.showMessageDialog(this, "Lưu chi tiết hóa đơn vào CSDL thất bại!");
        }
    }

    private void xuLyXoaMon() {
        int row = tblChiTietHoaDon.getSelectedRow();
        if (row != -1) {
            modelTableCTHD.removeRow(row);
            tinhTongTien();
            napDanhSachKhuyenMai(); 
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

        boolean kq = hoaDonDAO.thanhToanHoaDon(lblMaHD.getText(), "Tiền mặt", banDangChon.getMaBan());
        if (kq) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công!");
            modelTableCTHD.setRowCount(0);
            lblTongTien.setText("0 VNĐ");
            lblTenBan.setText("Chưa chọn bàn");
            lblMaHD.setText("---");
            lblMonDangChon.setText("Chưa chọn món nào");
            lblMonDangChon.setForeground(new Color(180, 0, 0));
            banDangChon = null;
            monDangChon = null;
            napDanhSachBan();
            napDanhSachKhuyenMai();
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

        HoaDon hd = hoaDonDAO.layHoaDonTheoMa(maHD);
        if (hd != null) {
            JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
            DialogInHoaDon dialogIn = new DialogInHoaDon(parentFrame, hd);
            dialogIn.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin hóa đơn trong CSDL!");
        }
    }

    private void xuLyChonBan(String maBan) {
        List<Ban> dsBan = banDAO.layTatCaBan();
        for (Ban b : dsBan) {
            if (b.getMaBan().equals(maBan)) {
                banDangChon = b;
                lblTenBan.setText(b.getTenBan());
                
                String maHD = hoaDonDAO.layMaHDChuaThanhToanTheoBan(b.getMaBan());
                
                if (maHD != null) {
                    lblMaHD.setText(maHD);
                    modelTableCTHD.setRowCount(0);
                    
                    List<ChiTietHoaDon> dsCT = hoaDonDAO.layChiTietHoaDon(maHD);
                    for (ChiTietHoaDon cthd : dsCT) {
                        BigDecimal thanhTien = cthd.getThanhTien();
                        modelTableCTHD.addRow(new Object[]{
                            cthd.getMon().getTenMon(), 
                            cthd.getSoLuong(), 
                            cthd.getDonGia(), 
                            thanhTien
                        });
                    }
                    napDanhSachKhuyenMai();
                    tinhTongTien();
                } else {
                    lblMaHD.setText("---");
                    modelTableCTHD.setRowCount(0);
                    lblTongTien.setText("0 VNĐ");
                    txtGhiChu.setText("");
                    spnSoLuong.setValue(1);
                    napDanhSachKhuyenMai();
                }
                break;
            }
        }
    }

    private void xuLyChonMon(String maMon) {
        monDangChon = monDAO.layMonTheoMa(maMon);
        if (monDangChon != null) {
            lblMonDangChon.setText(monDangChon.getTenMon() + " (" + String.format("%,.0f", monDangChon.getDonGia()) + "đ)");
            lblMonDangChon.setForeground(new Color(0, 102, 204));
        }
    }
}