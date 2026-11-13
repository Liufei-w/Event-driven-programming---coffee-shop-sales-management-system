package view;

import javax.swing.*;
import java.awt.*;

public class main extends JFrame {
    private JPanel pnlContainer;
    private CardLayout cardLayout;

    public main() {
        setTitle("Hệ Thống Quản Lý Quán Cà Phê - Evergreen");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        //Sidebar
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new GridLayout(10, 1, 5, 5));
        
        JButton btnTrangChu = new JButton("Trang Chủ");
        JButton btnLapHoaDon = new JButton("Lập Hóa Đơn");
        JButton btnTraCuuHoaDon = new JButton("Tra cứu hoá đơn");
        JButton btnQuanLyMon = new JButton("Quản Lý Món");
        JButton btnThongKe = new JButton("Thống Kê Doanh Thu");

        pnlMenu.add(btnTrangChu);
        pnlMenu.add(btnLapHoaDon);
        pnlMenu.add(btnTraCuuHoaDon);
        pnlMenu.add(btnQuanLyMon);
        pnlMenu.add(btnThongKe);

        add(pnlMenu, BorderLayout.WEST);

        //Khu vực hiển thị nội dung thay đổi ở giữa
        cardLayout = new CardLayout();
        pnlContainer = new JPanel(cardLayout);

        JPanel pnlWelcome = new JPanel(new GridBagLayout());
        JLabel lblWelcome = new JLabel("<html><h2>Chào mừng đến với Hệ thống Quản lý Quán Cà Phê Evergreen</h2></html>");
        pnlWelcome.add(lblWelcome);
        
        GiaoDienBanHang pnlBanHang = new GiaoDienBanHang();

        GiaoDienQuanLyMon pnlMon = new GiaoDienQuanLyMon();

        GiaoDienTraCuuHoaDon pnlTraCuu = new GiaoDienTraCuuHoaDon();
        
        pnlContainer.add(pnlWelcome, "TRANG_CHU");
        pnlContainer.add(pnlBanHang, "LAP_HOA_DON");
        pnlContainer.add(pnlMon, "QUAN_LY_MON");
        pnlContainer.add(pnlTraCuu, "TRA_CUU_HOADON");
        add(pnlContainer, BorderLayout.CENTER);

        // Đăng ký sự kiện bấm nút trên Menu để chuyển màn hình
        btnTrangChu.addActionListener(e -> cardLayout.show(pnlContainer, "TRANG_CHU"));
        btnLapHoaDon.addActionListener(e -> cardLayout.show(pnlContainer, "LAP_HOA_DON"));
        btnTraCuuHoaDon.addActionListener(e -> cardLayout.show(pnlContainer, "TRA_CUU_HOADON"));
        
        btnQuanLyMon.addActionListener(e -> {
            cardLayout.show(pnlContainer, "QUAN_LY_MON");
        });
        
        cardLayout.show(pnlContainer, "TRANG_CHU");
    }

    public static void main(String[] args) {
        new main().setVisible(true);
    }
}