package view;

import entity.ChiTietHoaDon;
import entity.HoaDon;

import javax.swing.*;
import java.awt.*;
import java.awt.print.PrinterException;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.text.NumberFormat;
import java.util.Locale;

public class DialogInHoaDon extends JDialog {
    private JTextPane txtPaneHoaDon;
    private JButton btnInNgay, btnDong;

    public DialogInHoaDon(JFrame parent, HoaDon hd) {
        super(parent, "Xem trước hóa đơn", true);
        setSize(400, 600);
        setLocationRelativeTo(parent);
        setLayout(new BorderLayout(10, 10));

        // TextPane hiển thị hóa đơn dạng HTML
        txtPaneHoaDon = new JTextPane();
        txtPaneHoaDon.setContentType("text/html");
        txtPaneHoaDon.setEditable(false);
        txtPaneHoaDon.setText(taoNoiDungHTML(hd));
        
        JScrollPane scrollPane = new JScrollPane(txtPaneHoaDon);
        add(scrollPane, BorderLayout.CENTER);

        // Panel nút bấm
        JPanel pnlBottom = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnInNgay = new JButton("In Hóa Đơn");
        btnInNgay.setBackground(new Color(46, 139, 87));
        btnInNgay.setForeground(Color.WHITE);
        btnDong = new JButton("Đóng");

        pnlBottom.add(btnInNgay);
        pnlBottom.add(btnDong);
        add(pnlBottom, BorderLayout.SOUTH);

        // Sự kiện in hoặc xuất PDF qua Microsoft Print to PDF
        btnInNgay.addActionListener(e -> {
            try {
                boolean complete = txtPaneHoaDon.print();
                if (complete) {
                    JOptionPane.showMessageDialog(this, "Đã gửi lệnh in/xuất file thành công!");
                    dispose();
                }
            } catch (PrinterException ex) {
                JOptionPane.showMessageDialog(this, "Lỗi khi in: " + ex.getMessage(), "Lỗi", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        btnDong.addActionListener(e -> dispose());
    }

    /**
     * Tạo nội dung HTML chuyên nghiệp cho hóa đơn quán cà phê
     */
    private String taoNoiDungHTML(HoaDon hd) {
        NumberFormat nf = NumberFormat.getInstance(new Locale("vi", "VN"));
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        StringBuilder html = new StringBuilder();
        html.append("<html><body style='font-family: Arial, sans-serif; font-size: 12px; width: 320px; margin: auto;'>");
        
        // Header
        html.append("<div style='text-align: center;'>");
        html.append("<h2 style='margin-bottom: 2px;'>QUÁN CÀ PHÊ EVERGREEN</h2>");
        html.append("<p style='margin-top: 0; font-size: 11px;'>ĐC: 12 Nguyễn Văn Bảo, Gò Vấp, TP.HCM<br>ĐT: 0901234567</p>");
        html.append("<hr style='border: dashed 1px #ccc;'>");
        html.append("<h3>HÓA ĐƠN THANH TOÁN</h3>");
        html.append("</div>");

        // Thông tin chung
        html.append("<div style='margin-bottom: 10px;'>");
        html.append("<b>Mã HD:</b> ").append(hd.getMaHD()).append("<br>");
        html.append("<b>Ngày lập:</b> ").append(hd.getNgayLap() != null ? hd.getNgayLap().format(dtf) : "").append("<br>");
        html.append("<b>Bàn:</b> ").append(hd.getBan() != null ? hd.getBan().getTenBan() : "Mang đi").append("<br>");
        html.append("<b>Thu ngân:</b> ").append(hd.getNhanVien() != null ? hd.getNhanVien().getMaNV() : "").append("<br>");
        html.append("</div>");
        html.append("<hr style='border: dashed 1px #ccc;'>");

        // Bảng chi tiết món
        html.append("<table style='width: 100%; font-size: 11px; border-collapse: collapse;'>");
        html.append("<tr style='border-bottom: 1px solid #000;'>");
        html.append("<th align='left'>Món</th><th align='center'>SL</th><th align='right'>Đơn giá</th><th align='right'>T.Tien</th>");
        html.append("</tr>");

        BigDecimal tongTienMon = BigDecimal.ZERO;
        if (hd.getDanhSachChiTiet() != null) {
            for (ChiTietHoaDon cthd : hd.getDanhSachChiTiet()) {
                BigDecimal thanhTien = cthd.getThanhTien(); // Tận dụng hàm trong ChiTietHoaDon entity
                tongTienMon = tongTienMon.add(thanhTien);
                
                html.append("<tr>");
                html.append("<td style='padding: 3px 0;'>").append(cthd.getMon().getTenMon()).append("</td>");
                html.append("<td align='center'>").append(cthd.getSoLuong()).append("</td>");
                html.append("<td align='right'>").append(nf.format(cthd.getDonGia())).append("</td>");
                html.append("<td align='right'>").append(nf.format(thanhTien)).append("</td>");
                html.append("</tr>");
            }
        }
        html.append("</table>");
        html.append("<hr style='border: dashed 1px #ccc;'>");

        // Sử dụng các phương thức tính toán từ Entity HoaDon để đồng bộ dữ liệu chuẩn 100%
        BigDecimal tienGiam = hd.getTienGiam() != null ? hd.getTienGiam() : BigDecimal.ZERO;
        BigDecimal thueVAT = hd.getThueVAT() != null ? hd.getThueVAT() : BigDecimal.valueOf(0.10);
        BigDecimal tongThanhToan = hd.getTongTien();

        html.append("<div style='text-align: right; font-size: 12px;'>");
        html.append("Thành tiền món: ").append(nf.format(tongTienMon)).append(" đ<br>");
        if (tienGiam.compareTo(BigDecimal.ZERO) > 0) {
            html.append("Giảm giá: -").append(nf.format(tienGiam)).append(" đ<br>");
        }
        html.append("VAT (10%): ").append(nf.format(tongTienMon.multiply(thueVAT))).append(" đ<br>");
        html.append("<b>Tổng thanh toán: <span style='font-size: 14px; color: red;'>").append(nf.format(tongThanhToan)).append(" đ</span></b>");
        html.append("</div>");

        // Footer
        html.append("<div style='text-align: center; margin-top: 15px;'>");
        html.append("<hr style='border: dashed 1px #ccc;'>");
        html.append("<p style='font-style: italic; font-size: 11px;'>Cảm ơn quý khách và hẹn gặp lại!</p>");
        html.append("</div>");

        html.append("</body></html>");
        return html.toString();
    }
}