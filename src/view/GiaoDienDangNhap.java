package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

import dao.TaiKhoan_DAO;
import entity.TaiKhoan;
public class GiaoDienDangNhap extends JFrame implements ActionListener {

    private JLabel lblLogo;
    private JLabel lblTieuDe;
    private JLabel lblTenDangNhap;
    private JLabel lblMatKhau;

    private JTextField txtTenDangNhap;
    private JPasswordField txtMatKhau;

    private JButton btnDangNhap;

    private TaiKhoan_DAO taiKhoan_DAO;

    public GiaoDienDangNhap() {

        setTitle("Đăng nhập - EVERGREEN COFFEE & TEA");
        setSize(550, 730);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        taiKhoan_DAO = new TaiKhoan_DAO();

        JPanel pnlChinh = new JPanel(null);
        pnlChinh.setBackground(new Color(243, 242, 247));

        JPanel pnlDangNhap = new JPanel(null);
        pnlDangNhap.setBackground(Color.WHITE);
        pnlDangNhap.setBorder(new LineBorder(
                new Color(225, 225, 230), 1, true));
        pnlDangNhap.setBounds(30, 20, 490, 650);

       
        ImageIcon icon = new ImageIcon("img/cf.jpg");

        Image img = icon.getImage().getScaledInstance(
                120, 120, Image.SCALE_SMOOTH);

        lblLogo = new JLabel(new ImageIcon(img));
        lblLogo.setHorizontalAlignment(JLabel.CENTER);
        lblLogo.setBounds(185, 15, 120, 120);
        
        lblTieuDe = new JLabel("Đăng nhập vào hệ thống");
        lblTieuDe.setFont(new Font("Arial", Font.BOLD, 25));
        lblTieuDe.setForeground(new Color(36, 83, 65));
        lblTieuDe.setHorizontalAlignment(JLabel.CENTER);
        lblTieuDe.setBounds(25, 155, 440, 45);

        
        lblTenDangNhap = new JLabel("Tên đăng nhập");
        lblTenDangNhap.setFont(new Font("Arial", Font.PLAIN, 21));
        lblTenDangNhap.setBounds(42, 215, 400, 30);

        txtTenDangNhap = new JTextField();
        txtTenDangNhap.setFont(new Font("Arial", Font.PLAIN, 16));
        txtTenDangNhap.setOpaque(false);
        txtTenDangNhap.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(5, 18, 5, 18)));
        txtTenDangNhap.setBounds(42, 250, 406, 55);

        
        lblMatKhau = new JLabel("Mật khẩu");
        lblMatKhau.setFont(new Font("Arial", Font.PLAIN, 21));
        lblMatKhau.setBounds(42, 325, 400, 30);

        txtMatKhau = new JPasswordField();
        txtMatKhau.setFont(new Font("Arial", Font.PLAIN, 16));
        txtMatKhau.setOpaque(false);
        txtMatKhau.setBorder(BorderFactory.createCompoundBorder(
                new LineBorder(new Color(215, 215, 215), 1, true),
                new EmptyBorder(5, 18, 5, 18)));
        txtMatKhau.setBounds(42, 360, 406, 55);

        
        btnDangNhap = new JButton("Đăng Nhập");
        btnDangNhap.setFont(new Font("Arial", Font.PLAIN, 21));
        btnDangNhap.setForeground(Color.WHITE);
        btnDangNhap.setBackground(new Color(65, 119, 94));
        btnDangNhap.setFocusPainted(false);
        btnDangNhap.setBorderPainted(false);
        btnDangNhap.setOpaque(true);
        btnDangNhap.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnDangNhap.setBounds(42, 475, 406, 58);
        btnDangNhap.addActionListener(this);

        
        pnlDangNhap.add(lblLogo);
        pnlDangNhap.add(lblTieuDe);
        pnlDangNhap.add(lblTenDangNhap);
        pnlDangNhap.add(txtTenDangNhap);
        pnlDangNhap.add(lblMatKhau);
        pnlDangNhap.add(txtMatKhau);
        pnlDangNhap.add(btnDangNhap);

        pnlChinh.add(pnlDangNhap);

        add(pnlChinh);

        
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == btnDangNhap) {

            String tenDangNhap = txtTenDangNhap.getText().trim();
            String matKhau = new String(txtMatKhau.getPassword());

            if (tenDangNhap.isEmpty() || matKhau.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                        "Vui lòng nhập đầy đủ thông tin!");
                return;
            }

            TaiKhoan tk = taiKhoan_DAO.dangNhap(
                    tenDangNhap, matKhau);

            if (tk != null) {

                JOptionPane.showMessageDialog(this,
                        "Đăng nhập thành công!");

                new main().setVisible(true);
                this.dispose();

            } else {

                JOptionPane.showMessageDialog(this,
                        "Tên đăng nhập hoặc mật khẩu không chính xác!");
            }
        }
    }

    public static void main(String[] args) {
        new GiaoDienDangNhap().setVisible(true);;
    }
}
