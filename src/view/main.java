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
        setExtendedState(JFrame.MAXIMIZED_BOTH);
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

        JButton btnLapHoaDon = taoNutMenu("Lập Hóa Đơn");
        JButton btnTraCuuHoaDon = taoNutMenu("Tra cứu hoá đơn");
        JButton btnQuanLyMon = taoNutMenu("Quản Lý Món");
        JButton btnKhuyenMai = taoNutMenu("Khuyến Mãi");
        JButton btnThongKe = taoNutMenu("Thống Kê Doanh Thu");
        JButton btnQuanLyNhanVien = taoNutMenu("Quản Lý Nhân Viên");
        JButton btnQuanLyBan = taoNutMenu("Quản Lý Bàn");
        JButton btnQuanLyKhachHang = taoNutMenu("Quản Lý Khách Hàng");
        
        pnlMenu.add(btnLapHoaDon);
        pnlMenu.add(btnTraCuuHoaDon);
        pnlMenu.add(btnQuanLyMon);
        pnlMenu.add(btnKhuyenMai);
        pnlMenu.add(btnThongKe);
        pnlMenu.add(btnQuanLyNhanVien);
        pnlMenu.add(btnQuanLyBan);
        pnlMenu.add(btnQuanLyKhachHang);

        pnlMenu.add(Box.createVerticalGlue());

        add(pnlMenu, BorderLayout.WEST);

        // Khu vực hiển thị nội dung
        cardLayout = new CardLayout();
        pnlContainer = new JPanel(cardLayout);

//        JPanel pnlWelcome = new JPanel(new GridBagLayout());
//        JLabel lblWelcome = new JLabel("Chào mừng đến với Hệ thống Quản lý Quán Cà Phê Evergreen");
//        pnlWelcome.add(lblWelcome);

        GiaoDienBanHang pnlBanHang = new GiaoDienBanHang();

        GiaoDienQuanLyMon pnlMon = new GiaoDienQuanLyMon();

        GiaoDienQuanLyKhuyenMai pnlKhuyenMai = new GiaoDienQuanLyKhuyenMai();

        GiaoDienTraCuuHoaDon pnlTraCuu = new GiaoDienTraCuuHoaDon();

        GiaoDienThongKeDoanhThu pnlThongKe = new GiaoDienThongKeDoanhThu();
        
        GiaoDienQuanLyNhanVien pnlQuanLyNhanVien = new GiaoDienQuanLyNhanVien();
        GiaoDienQuanLyBan pnlQuanLyBan = new GiaoDienQuanLyBan();
        GiaoDienQuanLyKhachHang pnlQuanLyKhachHang = new GiaoDienQuanLyKhachHang();

        pnlContainer.add(pnlBanHang, "LAP_HOA_DON");
        pnlContainer.add(pnlMon, "QUAN_LY_MON");
        pnlContainer.add(pnlKhuyenMai, "KHUYEN_MAI");
        pnlContainer.add(pnlTraCuu, "TRA_CUU_HOADON");
        pnlContainer.add(pnlThongKe, "THONG_KE");
        pnlContainer.add(pnlQuanLyNhanVien, "QUAN_LY_NHAN_VIEN");
        pnlContainer.add(pnlQuanLyBan, "QUAN_LY_BAN");
        pnlContainer.add(pnlQuanLyKhachHang, "QUAN_LY_KHACH_HANG");
        add(pnlContainer, BorderLayout.CENTER);

        // Đăng ký sự kiện bấm nút trên Menu để chuyển màn hình
        btnLapHoaDon.addActionListener(e -> chonMenu(btnLapHoaDon, "LAP_HOA_DON"));
        btnTraCuuHoaDon.addActionListener(e -> chonMenu(btnTraCuuHoaDon, "TRA_CUU_HOADON"));
        btnQuanLyMon.addActionListener(e -> chonMenu(btnQuanLyMon, "QUAN_LY_MON"));
        btnKhuyenMai.addActionListener(e -> chonMenu(btnKhuyenMai, "KHUYEN_MAI"));
        btnThongKe.addActionListener(e -> chonMenu(btnThongKe, "THONG_KE"));
        btnQuanLyNhanVien.addActionListener(e -> chonMenu(btnQuanLyNhanVien, "QUAN_LY_NHAN_VIEN"));
        btnQuanLyBan.addActionListener(e -> chonMenu(btnQuanLyBan, "QUAN_LY_BAN"));
        btnQuanLyKhachHang.addActionListener(e -> chonMenu(btnQuanLyKhachHang, "QUAN_LY_KHACH_HANG"));

        // Mặc định mở Trang Chủ
        chonMenu(btnLapHoaDon, "LAP_HOA_DON");
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

    private ImageIcon taoLogo(int w, int h) {
        ImageIcon icon = new ImageIcon("img/cf.jpg");

        Image anh = icon.getImage().getScaledInstance(
            w, h, Image.SCALE_SMOOTH
        );

        return new ImageIcon(anh);
    }

    public static void main(String[] args) {
        new GiaoDienDangNhap().setVisible(true);
    }
}