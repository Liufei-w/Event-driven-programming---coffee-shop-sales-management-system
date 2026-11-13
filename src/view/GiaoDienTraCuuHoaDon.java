package view;

import dao.HoaDon_DAO;
import entity.ChiTietHoaDon;
import entity.HoaDon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class GiaoDienTraCuuHoaDon extends JPanel implements ActionListener {
    private JTextField txtMaHD;
    private JComboBox<String> cboTrangThai;
    private JButton btnTimKiem, btnXemChiTiet;
    private JTable tblHoaDon;
    private DefaultTableModel modelTableHD;

    private HoaDon_DAO hoaDonDAO;

    public GiaoDienTraCuuHoaDon() {
        hoaDonDAO = new HoaDon_DAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // 1. PHẦN BẮC (NORTH): THANH TÌM KIẾM
        JPanel pnlNorth = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        pnlNorth.setBorder(BorderFactory.createTitledBorder("Bộ lọc tìm kiếm hóa đơn"));

        pnlNorth.add(new JLabel("Mã hóa đơn:"));
        txtMaHD = new JTextField(15);
        pnlNorth.add(txtMaHD);

        pnlNorth.add(new JLabel("Trạng thái:"));
        cboTrangThai = new JComboBox<>(new String[]{"Tất cả", "Đã thanh toán", "Chưa thanh toán"});
        pnlNorth.add(cboTrangThai);

        btnTimKiem = new JButton("Tìm kiếm");
        btnTimKiem.setBackground(new Color(70, 130, 180));
        btnTimKiem.setForeground(Color.WHITE);
        pnlNorth.add(btnTimKiem);

        add(pnlNorth, BorderLayout.NORTH);

        // 2. PHẦN GIỮA (CENTER): BẢNG HIỂN THỊ DANH SÁCH HÓA ĐƠN
        String[] columnNames = {"Mã HĐ", "Ngày lập", "Bàn", "Nhân viên", "Trạng thái", "Tiền giảm", "Tổng thanh toán"};
        modelTableHD = new DefaultTableModel(columnNames, 0);
        tblHoaDon = new JTable(modelTableHD);
        add(new JScrollPane(tblHoaDon), BorderLayout.CENTER);

        // 3. PHẦN NAM (SOUTH): NÚT THAO TÁC XEM CHI TIẾT
        JPanel pnlSouth = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnXemChiTiet = new JButton("Xem chi tiết / In lại HĐ");
        btnXemChiTiet.setFont(new Font("Arial", Font.BOLD, 12));
        btnXemChiTiet.setBackground(new Color(46, 139, 87));
        btnXemChiTiet.setForeground(Color.WHITE);
        pnlSouth.add(btnXemChiTiet);

        add(pnlSouth, BorderLayout.SOUTH);

        // Đăng ký sự kiện
        btnTimKiem.addActionListener(this);
        btnXemChiTiet.addActionListener(this);

        taiDanhSachHoaDon("", "Tất cả");
    }

    private void taiDanhSachHoaDon(String maHD, String trangThai) {
        modelTableHD.setRowCount(0);
        List<HoaDon> dsHD = hoaDonDAO.timKiemHoaDon(maHD, trangThai);
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        for (HoaDon hd : dsHD) {
            String ngayLapStr = (hd.getNgayLap() != null) ? hd.getNgayLap().format(formatter) : "";
            String tenBan = (hd.getBan() != null) ? hd.getBan().getTenBan() : "---";
            String tenNV = (hd.getNhanVien() != null) ? hd.getNhanVien().getMaNV() : "---";
            
            BigDecimal tienGiam = hd.getTienGiam() != null ? hd.getTienGiam() : BigDecimal.ZERO;
            
            BigDecimal tongThanhToan = BigDecimal.ZERO; 

            modelTableHD.addRow(new Object[]{
                hd.getMaHD(),
                ngayLapStr,
                tenBan,
                tenNV,
                hd.getTrangThai(),
                String.format("%,.0f đ", tienGiam),
                "Xem chi tiết"
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnTimKiem)) {
            String maHD = txtMaHD.getText();
            String trangThai = (String) cboTrangThai.getSelectedItem();
            taiDanhSachHoaDon(maHD, trangThai);
        } else if (o.equals(btnXemChiTiet)) {
            int row = tblHoaDon.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Vui lòng chọn dòng hóa đơn cần xem chi tiết!");
                return;
            }
            String maHD = modelTableHD.getValueAt(row, 0).toString();
            HoaDon hd = hoaDonDAO.layHoaDonTheoMa(maHD);
            if (hd != null) {
                JFrame parentFrame = (JFrame) SwingUtilities.getWindowAncestor(this);
                DialogInHoaDon dialogIn = new DialogInHoaDon(parentFrame, hd);
                dialogIn.setVisible(true);
            } else {
                JOptionPane.showMessageDialog(this, "Không tìm thấy thông tin chi tiết hóa đơn!");
            }
        }
    }
}