package view;

import connectDB.ConnectDB;
import dao.Ban_DAO;
import dao.ChiTietHoaDon_DAO;
import dao.HoaDon_DAO;
import dao.KhachHang_DAO;
import dao.KhuyenMai_DAO;
import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.Ban;
import entity.ChiTietHoaDon;
import entity.HoaDon;
import entity.KhachHang;
import entity.KhuyenMai;
import entity.LoaiMon;
import entity.Mon;
import entity.NhanVien;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GiaoDienBanHang extends JPanel implements ActionListener, MouseListener {
    private JPanel pnlSoDoBan;
    private JComboBox<String> cboLoaiMon;
    
    private JTable tblThucDon;
    private DefaultTableModel modelTableThucDon;
    private JTextField txtTimKiemMon;
    private JButton btnTimKiemMon;
    private JTable tblChiTietHoaDon;
    private DefaultTableModel modelTableCTHD;
    private JLabel lblMaHD;
    private JLabel lblTenBan;
    private JLabel lblTongTien;
    private JComboBox<KhuyenMai> cboKhuyenMai;
    private JComboBox<KhachHang> cboKhachHang;
    private JButton btnXoaMon;
    private JButton btnThanhToan;
    private JButton btnInHoaDon;
    
    private Ban_DAO banDAO;
    private Mon_DAO monDAO;
    private LoaiMon_DAO loaiMonDAO;
    private HoaDon_DAO hoaDonDAO;
    private ChiTietHoaDon_DAO chiTietHoaDonDAO;
    private KhuyenMai_DAO khuyenMaiDAO;
    private KhachHang_DAO khachHangDAO;
    
    private Ban banDangChon = null;

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
        khachHangDAO = new KhachHang_DAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // SƠ ĐỒ BÀN
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setBorder(BorderFactory.createTitledBorder("1. Sơ đồ bàn"));
        pnlTrai.setPreferredSize(new Dimension(260, 0));

        pnlSoDoBan = new JPanel(new GridLayout(0, 2, 5, 5));
        pnlTrai.add(new JScrollPane(pnlSoDoBan), BorderLayout.CENTER);
        add(pnlTrai, BorderLayout.WEST);

        //THỰC ĐƠN MÓN NƯỚC
        JPanel pnlGiua = new JPanel(new BorderLayout(5, 5));
        pnlGiua.setBorder(BorderFactory.createTitledBorder("2. Thực đơn món nước"));

        JPanel pnlLocMon = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlLocMon.add(new JLabel("Danh mục:"));
        cboLoaiMon = new JComboBox<>();
        cboLoaiMon.setPreferredSize(new Dimension(120, 25));
        pnlLocMon.add(cboLoaiMon);

        pnlLocMon.add(new JLabel("Tìm món:"));
        txtTimKiemMon = new JTextField(9);
        pnlLocMon.add(txtTimKiemMon);
        
        btnTimKiemMon = new JButton("Tìm");
        pnlLocMon.add(btnTimKiemMon);

        pnlGiua.add(pnlLocMon, BorderLayout.NORTH);

        // Bảng danh sách thực đơn 
        String[] colThucDon = {"Mã món", "Tên món", "Đơn giá", "SL", "Thao tác"};
        modelTableThucDon = new DefaultTableModel(colThucDon, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3;
            }
        };
        tblThucDon = new JTable(modelTableThucDon);
        tblThucDon.setRowHeight(28);
        tblThucDon.addMouseListener(this);
        
        pnlGiua.add(new JScrollPane(tblThucDon), BorderLayout.CENTER);
        add(pnlGiua, BorderLayout.CENTER);

        //CHI TIẾT HÓA ĐƠN & THANH TOÁN
        JPanel pnlPhai = new JPanel(new BorderLayout(5, 5));
        pnlPhai.setBorder(BorderFactory.createTitledBorder("3. Chi tiết hóa đơn"));
        pnlPhai.setPreferredSize(new Dimension(440, 0));

        JPanel pnlThongTinHD = new JPanel(new GridLayout(3, 1, 3, 3));
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

        JPanel pnlDong3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        pnlDong3.add(new JLabel("Khách hàng: "));
        cboKhachHang = new JComboBox<>();
        cboKhachHang.setPreferredSize(new Dimension(280, 24));
        pnlDong3.add(cboKhachHang);

        pnlThongTinHD.add(pnlDong1);
        pnlThongTinHD.add(pnlDong2);
        pnlThongTinHD.add(pnlDong3);
        pnlPhai.add(pnlThongTinHD, BorderLayout.NORTH);

        String[] columnNames = {"Tên món", "SL", "Đơn giá", "Thành tiền"};
        modelTableCTHD = new DefaultTableModel(columnNames, 0);
        tblChiTietHoaDon = new JTable(modelTableCTHD);
        tblChiTietHoaDon.setRowHeight(24);
        pnlPhai.add(new JScrollPane(tblChiTietHoaDon), BorderLayout.CENTER);

        JPanel pnlSouthPhai = new JPanel();
        pnlSouthPhai.setLayout(new BoxLayout(pnlSouthPhai, BoxLayout.Y_AXIS));
        pnlSouthPhai.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        JPanel pnlDongXoa = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        btnXoaMon = new JButton("Xóa món đã chọn");
        pnlDongXoa.add(btnXoaMon);
        pnlSouthPhai.add(pnlDongXoa);

        JPanel pnlDongKM = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        pnlDongKM.add(new JLabel("Khuyến mãi:"));
        cboKhuyenMai = new JComboBox<>();
        cboKhuyenMai.setPreferredSize(new Dimension(320, 28));
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

        // Nạp dữ liệu
        napDanhSachBan();
        napDanhMucLoaiMon();
        napDanhSachThucDon(null);
        napDanhSachKhuyenMai();
        napDanhSachKhachHang();

        // Đăng ký sự kiện
        cboLoaiMon.addActionListener(this);
        cboKhuyenMai.addActionListener(this);
        btnTimKiemMon.addActionListener(this);
        txtTimKiemMon.addActionListener(this);
        btnXoaMon.addActionListener(this);
        btnThanhToan.addActionListener(this);
        btnInHoaDon.addActionListener(this);
    }

    // CÁC HÀM NẠP DỮ LIỆU

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

    private void napDanhSachThucDon(String tenLoai) {
        modelTableThucDon.setRowCount(0);
        List<Mon> dsMon;
        if (tenLoai == null || tenLoai.equals("Tất cả")) {
            dsMon = monDAO.layTatCaMon();
        } else {
            dsMon = monDAO.layMonTheoLoai(tenLoai);
        }

        for (Mon m : dsMon) {
            modelTableThucDon.addRow(new Object[]{
                m.getMaMon(),
                m.getTenMon(),
                String.format("%,.0f đ", m.getDonGia()),
                1,                   
                "[ THÊM ]"    
            });
        }
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

    private void napDanhSachKhachHang() {
        if (cboKhachHang == null) return;
        cboKhachHang.removeAllItems();
        
        KhachHang khVangLai = new KhachHang(null, "Khách vãng lai (Không tích điểm)", null);
        cboKhachHang.addItem(khVangLai);

        try {
            List<KhachHang> dsKH = khachHangDAO.layTatCaKhachHang();
            for (KhachHang kh : dsKH) {
                cboKhachHang.addItem(kh);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void tinhTongTien() {
        BigDecimal tongChuaVAT = BigDecimal.ZERO;
        for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
            tongChuaVAT = tongChuaVAT.add(new BigDecimal(modelTableCTHD.getValueAt(i, 3).toString()));
        }

        BigDecimal tienGiamGia = BigDecimal.ZERO;
        if (cboKhuyenMai != null && cboKhuyenMai.getSelectedIndex() > 0) {
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

    //XỬ LÝ SỰ KIỆN

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();

        if (o.equals(cboLoaiMon)) {
            napDanhSachThucDon((String) cboLoaiMon.getSelectedItem());
        } else if (o.equals(cboKhuyenMai)) {
            tinhTongTien();
        } else if (o.equals(btnTimKiemMon) || o.equals(txtTimKiemMon)) {
            String keyword = txtTimKiemMon.getText().trim().toLowerCase();
            modelTableThucDon.setRowCount(0);
            List<Mon> dsMon = monDAO.layTatCaMon();
            for (Mon m : dsMon) {
                if (m.getTenMon().toLowerCase().contains(keyword) || m.getMaMon().toLowerCase().contains(keyword)) {
                    modelTableThucDon.addRow(new Object[]{
                        m.getMaMon(), m.getTenMon(), String.format("%,.0f đ", m.getDonGia()), 1, "[ + Thêm ]"
                    });
                }
            }
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
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        int row = tblThucDon.getSelectedRow();
        int col = tblThucDon.getSelectedColumn();
        if (row != -1 && col == 4) {
            int soLuongThem = 1;
            try {
                soLuongThem = Integer.parseInt(modelTableThucDon.getValueAt(row, 3).toString());
                if (soLuongThem <= 0) soLuongThem = 1;
            } catch (Exception ex) {
                soLuongThem = 1;
            }
            themMonVaoHoaDon(row, soLuongThem);
        }
    }

    private void themMonVaoHoaDon(int row, int soLuongThem) {
        if (banDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn ở sơ đồ bên trái trước khi thêm món!");
            return;
        }

        String maMon = modelTableThucDon.getValueAt(row, 0).toString();
        String tenMon = modelTableThucDon.getValueAt(row, 1).toString();

        Mon mon = monDAO.layMonTheoMa(maMon);
        if (mon == null) return;
        BigDecimal donGia = mon.getDonGia();

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

        // Lưu xuống CSDL
        boolean kqLuu = chiTietHoaDonDAO.themChiTietHoaDon(maHD, maMon, soLuongThem, donGia);
        
        if (kqLuu) {
            boolean daCoTrenBang = false;
            for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
                String tenMonInRow = modelTableCTHD.getValueAt(i, 0).toString();
                if (tenMonInRow.equals(tenMon)) {
                    int slCu = Integer.parseInt(modelTableCTHD.getValueAt(i, 1).toString());
                    int slMoi = slCu + soLuongThem;
                    BigDecimal thanhTienMoi = donGia.multiply(BigDecimal.valueOf(slMoi));
                    
                    modelTableCTHD.setValueAt(slMoi, i, 1);
                    modelTableCTHD.setValueAt(thanhTienMoi, i, 3);
                    daCoTrenBang = true;
                    break;
                }
            }

            if (!daCoTrenBang) {
                BigDecimal thanhTien = donGia.multiply(BigDecimal.valueOf(soLuongThem));
                modelTableCTHD.addRow(new Object[]{tenMon, soLuongThem, donGia, thanhTien});
            }

            tinhTongTien();
            napDanhSachKhuyenMai(); 
        } else {
            JOptionPane.showMessageDialog(this, "Thêm món thất bại!");
        }
    }

    private void xuLyXoaMon() {
        int row = tblChiTietHoaDon.getSelectedRow();
        if (row != -1) {
            modelTableCTHD.removeRow(row);
            tinhTongTien();
            napDanhSachKhuyenMai(); 
        } else {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng món cần xóa trong bảng hóa đơn!");
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

        BigDecimal tienGiam = BigDecimal.ZERO;
        String maKM = null;

        if (cboKhuyenMai != null && cboKhuyenMai.getSelectedIndex() > 0) {
            KhuyenMai km = (KhuyenMai) cboKhuyenMai.getSelectedItem();
            if (km != null && km.getMaKM() != null && !km.getMaKM().trim().isEmpty()) {
                maKM = km.getMaKM();
                BigDecimal tongChuaVAT = BigDecimal.ZERO;
                for (int i = 0; i < modelTableCTHD.getRowCount(); i++) {
                    tongChuaVAT = tongChuaVAT.add(new BigDecimal(modelTableCTHD.getValueAt(i, 3).toString()));
                }
                if (km.getPhanTramGiam() != null) {
                    tienGiam = tongChuaVAT.multiply(km.getPhanTramGiam());
                }
            }
        }

        String maKH = null;
        if (cboKhachHang != null && cboKhachHang.getSelectedIndex() > 0) {
            KhachHang selectedKH = (KhachHang) cboKhachHang.getSelectedItem();
            if (selectedKH != null && selectedKH.getMaKH() != null) {
                maKH = selectedKH.getMaKH();
            }
        }

     // Tạo đối tượng hóa đơn để tính tổng tiền
        HoaDon hd = hoaDonDAO.layHoaDonTheoMa(lblMaHD.getText());

        if (hd == null) {
            JOptionPane.showMessageDialog(this, "Không tìm thấy hóa đơn!");
            return;
        }

        // Cập nhật tiền giảm và VAT
        hd.setTienGiam(tienGiam);
        hd.setThueVAT(BigDecimal.valueOf(0.10));

        // Tính tổng tiền thông qua Entity HoaDon
        BigDecimal tongThanhToan = hd.getTongTien();

        boolean kq = hoaDonDAO.thanhToanHoaDon(lblMaHD.getText(), "Tiền mặt", banDangChon.getMaBan(), tienGiam, maKM, maKH, tongThanhToan);
        
        if (kq) {
            JOptionPane.showMessageDialog(this, "Thanh toán thành công! Khách hàng đã được cộng điểm tích lũy (nếu có).");
            modelTableCTHD.setRowCount(0);
            lblTongTien.setText("0 VNĐ");
            lblTenBan.setText("Chưa chọn bàn");
            lblMaHD.setText("---");
            banDangChon = null;
            napDanhSachBan();
            napDanhSachKhuyenMai();
            napDanhSachKhachHang();
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
                    napDanhSachKhuyenMai();
                }
                break;
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}

    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}
}