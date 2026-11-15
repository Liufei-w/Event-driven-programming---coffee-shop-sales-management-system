package view;

import javax.swing.*;
import javax.swing.plaf.basic.BasicButtonUI;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class main extends JFrame {
    private JPanel pnlContainer;
    private CardLayout cardLayout;

    // Màu cho sidebar
    private final Color MAU_NEN = new Color(33, 37, 41);
    private final Color MAU_HOVER = new Color(52, 58, 64);
    private final Color MAU_CHON = new Color(46, 139, 87);

    // Nút đang được chọn
    private JButton btnDangChon = null;

    public main() {
        setTitle("Hệ Thống Quản Lý Quán Cà Phê - Evergreen");
        setSize(1300, 750);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // ===== Sidebar =====
        JPanel pnlMenu = new JPanel();
        pnlMenu.setLayout(new BoxLayout(pnlMenu, BoxLayout.Y_AXIS));
        pnlMenu.setBackground(MAU_NEN);
        pnlMenu.setPreferredSize(new Dimension(230, 0));

        // Logo ở trên cùng
        JLabel lblLogo = new JLabel(taoLogo(110, 110));
        lblLogo.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblLogo.setBorder(BorderFactory.createEmptyBorder(25, 0, 10, 0));

        // Tên hệ thống dưới logo
        JLabel lblTenQuan = new JLabel("EVERGREEN");
        lblTenQuan.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTenQuan.setForeground(Color.WHITE);
        lblTenQuan.setAlignmentX(Component.CENTER_ALIGNMENT);
        lblTenQuan.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));

        pnlMenu.add(lblLogo);
        pnlMenu.add(lblTenQuan);

        JButton btnTrangChu = taoNutMenu("Trang Chủ");
        JButton btnLapHoaDon = taoNutMenu("Lập Hóa Đơn");
        JButton btnTraCuuHoaDon = taoNutMenu("Tra cứu hoá đơn");
        JButton btnQuanLyMon = taoNutMenu("Quản Lý Món");
        JButton btnKhuyenMai = taoNutMenu("Khuyến Mãi");
        JButton btnThongKe = taoNutMenu("Thống Kê Doanh Thu");

        pnlMenu.add(btnTrangChu);
        pnlMenu.add(btnLapHoaDon);
        pnlMenu.add(btnTraCuuHoaDon);
        pnlMenu.add(btnQuanLyMon);
        pnlMenu.add(btnKhuyenMai);
        pnlMenu.add(btnThongKe);

        // Đẩy các nút lên trên, phần trống nằm dưới cùng
        pnlMenu.add(Box.createVerticalGlue());

        add(pnlMenu, BorderLayout.WEST);

        // Khu vực hiển thị nội dung
        cardLayout = new CardLayout();
        pnlContainer = new JPanel(cardLayout);

        JPanel pnlWelcome = new JPanel(new GridBagLayout());
        JLabel lblWelcome = new JLabel("<html><h2>Chào mừng đến với Hệ thống Quản lý Quán Cà Phê Evergreen</h2></html>");
        pnlWelcome.add(lblWelcome);

        GiaoDienBanHang pnlBanHang = new GiaoDienBanHang();

        GiaoDienQuanLyMon pnlMon = new GiaoDienQuanLyMon();

        GiaoDienQuanLyKhuyenMai pnlKhuyenMai = new GiaoDienQuanLyKhuyenMai();

        GiaoDienTraCuuHoaDon pnlTraCuu = new GiaoDienTraCuuHoaDon();

        GiaoDienThongKeDoanhThu pnlThongKe = new GiaoDienThongKeDoanhThu();

        pnlContainer.add(pnlWelcome, "TRANG_CHU");
        pnlContainer.add(pnlBanHang, "LAP_HOA_DON");
        pnlContainer.add(pnlMon, "QUAN_LY_MON");
        pnlContainer.add(pnlKhuyenMai, "KHUYEN_MAI");
        pnlContainer.add(pnlTraCuu, "TRA_CUU_HOADON");
        pnlContainer.add(pnlThongKe, "THONG_KE");
        add(pnlContainer, BorderLayout.CENTER);

        // Đăng ký sự kiện bấm nút trên Menu để chuyển màn hình
        btnTrangChu.addActionListener(e -> chonMenu(btnTrangChu, "TRANG_CHU"));
        btnLapHoaDon.addActionListener(e -> chonMenu(btnLapHoaDon, "LAP_HOA_DON"));
        btnTraCuuHoaDon.addActionListener(e -> chonMenu(btnTraCuuHoaDon, "TRA_CUU_HOADON"));
        btnQuanLyMon.addActionListener(e -> chonMenu(btnQuanLyMon, "QUAN_LY_MON"));
        btnKhuyenMai.addActionListener(e -> chonMenu(btnKhuyenMai, "KHUYEN_MAI"));
        btnThongKe.addActionListener(e -> chonMenu(btnThongKe, "THONG_KE"));

        // Mặc định mở Trang Chủ
        chonMenu(btnTrangChu, "TRANG_CHU");
    }

    // Tạo một nút cho sidebar
    private JButton taoNutMenu(String text) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("Segoe UI", Font.PLAIN, 15));
        btn.setForeground(Color.WHITE);
        btn.setBackground(MAU_NEN);
        btn.setOpaque(true);
        btn.setContentAreaFilled(true);
        btn.setBorderPainted(false);
        btn.setUI(new BasicButtonUI());
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setBorder(BorderFactory.createEmptyBorder(0, 25, 0, 0));

        // Nút chiếm hết chiều ngang sidebar, cao 48px
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setMaximumSize(new Dimension(Integer.MAX_VALUE, 48));
        btn.setPreferredSize(new Dimension(230, 48));

        // nút đang chọn thì giữ nguyên màu xanh
        btn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (btn != btnDangChon) {
                    btn.setBackground(MAU_HOVER);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                if (btn != btnDangChon) {
                    btn.setBackground(MAU_NEN);
                }
            }
        });

        return btn;
    }

    // Chuyển màn hình + đổi màu nút đang chọn
    private void chonMenu(JButton btn, String tenCard) {
        if (btnDangChon != null) {
            btnDangChon.setBackground(MAU_NEN);
        }
        btnDangChon = btn;
        btnDangChon.setBackground(MAU_CHON);
        cardLayout.show(pnlContainer, tenCard);
    }

    // Logo mẫu (vẽ tạm hình tròn có chữ LOGO)
    // Khi có logo thật thì thay bằng:
    //   ImageIcon icon = new ImageIcon("images/logo.png");
    //   Image anh = icon.getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH);
    //   return new ImageIcon(anh);
    private ImageIcon taoLogo(int w, int h) {
        BufferedImage img = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = img.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2.setColor(MAU_CHON);
        g2.fillOval(0, 0, w - 1, h - 1);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Segoe UI", Font.BOLD, 22));
        FontMetrics fm = g2.getFontMetrics();
        String chu = "LOGO";
        int x = (w - fm.stringWidth(chu)) / 2;
        int y = (h - fm.getHeight()) / 2 + fm.getAscent();
        g2.drawString(chu, x, y);

        g2.dispose();
        return new ImageIcon(img);
    }

    public static void main(String[] args) {
        new GiaoDienDangNhap().setVisible(true);
    }
}