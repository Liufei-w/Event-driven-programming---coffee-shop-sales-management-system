
package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import dao.HoaDon_DAO;
import entity.HoaDon;
import java.time.LocalDate;
import java.math.BigDecimal;

public class GiaoDienThongKeDoanhThu extends JPanel implements ActionListener {

    private JLabel lblTongDoanhThu;
    private JLabel lblSoHoaDon;
    private JLabel lblTrungBinh;

    private JSpinner spnTuNgay;
    private JSpinner spnDenNgay;

    private JButton btnThongKe;
    private JButton btnXemChiTiet;

    private JTable tblDoanhThu;
    private DefaultTableModel model;

    private HoaDon_DAO hoaDon_DAO = new HoaDon_DAO();
    
    public GiaoDienThongKeDoanhThu() {

        setLayout(new BorderLayout(10, 15));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Tiêu đề
        JLabel lblTieuDe = new JLabel("THỐNG KÊ DOANH THU");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 24));
        lblTieuDe.setHorizontalAlignment(JLabel.CENTER);

        add(lblTieuDe, BorderLayout.NORTH);

        // Nội dung chính
        JPanel pnlMain = new JPanel(new BorderLayout(10, 15));

        // Khu vực tổng quan
        JPanel pnlTongQuan = new JPanel(new GridLayout(1, 3, 15, 10));

        lblTongDoanhThu = new JLabel("0 VNĐ");
        lblSoHoaDon = new JLabel("0");
        lblTrungBinh = new JLabel("0 VNĐ");

        pnlTongQuan.add(taoTheThongKe("Tổng doanh thu", lblTongDoanhThu));
        pnlTongQuan.add(taoTheThongKe("Số hóa đơn", lblSoHoaDon));
        pnlTongQuan.add(taoTheThongKe("Trung bình / hóa đơn", lblTrungBinh));

        pnlMain.add(pnlTongQuan, BorderLayout.NORTH);

        // Khu vực lọc dữ liệu
        JPanel pnlLoc = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));

        JLabel lblTuNgay = new JLabel("Từ ngày:");
        spnTuNgay = taoOChonNgay();

        JLabel lblDenNgay = new JLabel("Đến ngày:");
        spnDenNgay = taoOChonNgay();

        btnThongKe = new JButton("Thống kê");
        btnXemChiTiet = new JButton("Xem chi tiết doanh thu");

        pnlLoc.add(lblTuNgay);
        pnlLoc.add(spnTuNgay);
        pnlLoc.add(lblDenNgay);
        pnlLoc.add(spnDenNgay);
        pnlLoc.add(btnThongKe);
        pnlLoc.add(btnXemChiTiet);

        // Bảng doanh thu
        String[] tieuDe = {
            "Mã hóa đơn",
            "Ngày lập",
            "Nhân viên lập",
            "Tổng tiền"
        };

        model = new DefaultTableModel(tieuDe, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        tblDoanhThu = new JTable(model);
        tblDoanhThu.setRowHeight(28);
        tblDoanhThu.getTableHeader().setFont(
            new Font("Arial", Font.BOLD, 13)
        );

        JScrollPane scrollPane = new JScrollPane(tblDoanhThu);

        JLabel lblBang = new JLabel("CHI TIẾT DOANH THU");
        lblBang.setFont(new Font("Arial", Font.BOLD, 16));

        JPanel pnlBang = new JPanel(new BorderLayout(5, 5));
        pnlBang.add(lblBang, BorderLayout.NORTH);
        pnlBang.add(scrollPane, BorderLayout.CENTER);

        // Ghép khu vực lọc và bảng
        JPanel pnlDuoi = new JPanel(new BorderLayout(5, 10));

        pnlDuoi.add(pnlLoc, BorderLayout.NORTH);
        pnlDuoi.add(pnlBang, BorderLayout.CENTER);

        pnlMain.add(pnlDuoi, BorderLayout.CENTER);

        add(pnlMain, BorderLayout.CENTER);
        btnThongKe.addActionListener(this);
        btnXemChiTiet.addActionListener(this);
    }
    @Override
	public void actionPerformed(ActionEvent e) {
    	  if (e.getSource() == btnThongKe) {
    	        thongKeDoanhThu();
    	    }else if (e.getSource() == btnXemChiTiet) {
    	        xemChiTietDoanhThu();
    	    }
	}
    // Tạo thẻ hiển thị thông tin thống kê
    private JPanel taoTheThongKe(String tieuDe, JLabel lblGiaTri) {

        JPanel pnl = new JPanel(new BorderLayout(5, 5));

        pnl.setBorder(BorderFactory.createTitledBorder(tieuDe));

        lblGiaTri.setFont(new Font("Arial", Font.BOLD, 20));
        lblGiaTri.setHorizontalAlignment(JLabel.CENTER);

        pnl.add(lblGiaTri, BorderLayout.CENTER);

        return pnl;
    }

    // Tạo ô chọn ngày
    private JSpinner taoOChonNgay() {

        JSpinner spinner = new JSpinner(new SpinnerDateModel());

        JSpinner.DateEditor editor =
            new JSpinner.DateEditor(spinner, "dd/MM/yyyy");

        spinner.setEditor(editor);
        spinner.setPreferredSize(new Dimension(120, 28));

        return spinner;
    }
    private LocalDate layNgay(JSpinner spinner) {
        Date ngay = (Date) spinner.getValue();

        return ngay.toInstant()
                   .atZone(java.time.ZoneId.systemDefault())
                   .toLocalDate();
    }
    private void thongKeDoanhThu() {
        LocalDate tuNgay = layNgay(spnTuNgay);
        LocalDate denNgay = layNgay(spnDenNgay);

        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        ArrayList<HoaDon> dsHD = (ArrayList<HoaDon>) hoaDon_DAO.layDanhSachHoaDonTheoNgay(tuNgay, denNgay);

        BigDecimal tongDoanhThu = BigDecimal.ZERO;

        for (HoaDon hd : dsHD) {
            tongDoanhThu = tongDoanhThu.add(hd.getTongTien());
        }

        int soHoaDon = dsHD.size();

        BigDecimal trungBinh = BigDecimal.ZERO;

        if (soHoaDon > 0) {
            trungBinh = tongDoanhThu.divide(
                BigDecimal.valueOf(soHoaDon), 0, java.math.RoundingMode.HALF_UP
            );
        }

        lblTongDoanhThu.setText(String.format("%,.0f VNĐ", tongDoanhThu));
        lblSoHoaDon.setText(String.valueOf(soHoaDon));
        lblTrungBinh.setText(String.format("%,.0f VNĐ", trungBinh));
    }
    private void xemChiTietDoanhThu() {
        LocalDate tuNgay = layNgay(spnTuNgay);
        LocalDate denNgay = layNgay(spnDenNgay);

        if (tuNgay.isAfter(denNgay)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            return;
        }

        ArrayList<HoaDon> dsHD = (ArrayList<HoaDon>) hoaDon_DAO.layDanhSachHoaDonTheoNgay(tuNgay, denNgay);

        model.setRowCount(0);

        for (HoaDon hd : dsHD) {
            Object[] row = {
                hd.getMaHD(),
                hd.getNgayLap(),
                hd.getNhanVien().getTenNV(),
                String.format("%,.0f VNĐ", hd.getTongTien())
            };

            model.addRow(row);
        }
    }
}
