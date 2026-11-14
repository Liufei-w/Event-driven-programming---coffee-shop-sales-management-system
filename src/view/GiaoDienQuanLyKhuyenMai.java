package view;

import dao.KhuyenMai_DAO;
import entity.KhuyenMai;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class GiaoDienQuanLyKhuyenMai extends JPanel implements ActionListener {
    private JTextField txtMaKM, txtTenKM, txtPhanTramGiam, txtNgayBatDau, txtNgayKetThuc, txtDonToiThieu, txtTimKiem;
    private JButton btnThem, btnSua, btnXoa, btnLamMoi, btnTim;
    private JTable tblKhuyenMai;
    private DefaultTableModel modelKhuyenMai;
    
    // --- DAO ---
    private KhuyenMai_DAO khuyenMaiDAO;
    
    // Định dạng format ngày, tháng, năm theo "dd/MM/yyyy"
    private DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // -- THIẾT KẾ GIAO DIỆN ĐƠN GIẢN --
    public GiaoDienQuanLyKhuyenMai() {
        khuyenMaiDAO = new KhuyenMai_DAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel pnlChinh = new JPanel(new BorderLayout(5, 5));
        pnlChinh.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE), "Quản lý Khuyến mãi"));

        // ==========================================
        // 1. PHẦN TRÊN: FORM NHẬP LIỆU & CÁC NÚT CHỨC NĂNG
        // ==========================================
        JPanel pnlNorth = new JPanel(new BorderLayout(5, 5));
        pnlNorth.setBorder(BorderFactory.createTitledBorder(
        		BorderFactory.createLineBorder(Color.BLUE), "Thông tin Khuyến mãi"));

        JPanel pnlForm = new JPanel(new GridLayout(3, 4, 10, 8));
        pnlForm.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        pnlForm.add(new JLabel("Mã Khuyến mãi:"));
        txtMaKM = new JTextField();
        txtMaKM.setEditable(false);
        pnlForm.add(txtMaKM);

        pnlForm.add(new JLabel("Tên Khuyến mãi:"));
        txtTenKM = new JTextField();
        pnlForm.add(txtTenKM);

        pnlForm.add(new JLabel("Phần trăm giảm (0 - 1):"));
        txtPhanTramGiam = new JTextField();
        pnlForm.add(txtPhanTramGiam);

        pnlForm.add(new JLabel("Đơn tối thiểu (VND):"));
        txtDonToiThieu = new JTextField("0");
        pnlForm.add(txtDonToiThieu);

        pnlForm.add(new JLabel("Ngày bắt đầu (dd/MM/yyyy):"));
        txtNgayBatDau = new JTextField();
        pnlForm.add(txtNgayBatDau);

        pnlForm.add(new JLabel("Ngày kết thúc (dd/MM/yyyy):"));
        txtNgayKetThuc = new JTextField();
        pnlForm.add(txtNgayKetThuc);

        // Thanh tìm kiếm và các nút chức năng
        JPanel pnlChucNang = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 5));
        pnlChucNang.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));

        pnlChucNang.add(new JLabel("Nhập mã KM cần tìm:"));
        txtTimKiem = new JTextField(15);
        pnlChucNang.add(txtTimKiem);

        btnTim = new JButton("Tìm kiếm");
        btnTim.setBackground(new Color(70, 130, 180));
        btnTim.setForeground(Color.WHITE);
        
        btnThem = new JButton("Thêm KM");
        btnSua = new JButton("Cập nhật KM");
        btnXoa = new JButton("Xóa");
        btnLamMoi = new JButton("Làm mới");

        pnlChucNang.add(btnTim);
        // Tạo khoảng trắng ngăn cách phần Tìm kiếm với các nút CRUD
        pnlChucNang.add(Box.createHorizontalStrut(50));
        
        pnlChucNang.add(btnThem);
        pnlChucNang.add(btnSua);
        pnlChucNang.add(btnXoa);
        pnlChucNang.add(btnLamMoi);

        pnlNorth.add(pnlForm, BorderLayout.CENTER);
        pnlNorth.add(pnlChucNang, BorderLayout.SOUTH);
        pnlChinh.add(pnlNorth, BorderLayout.NORTH);

        // ==========================================
        // 2. PHẦN GIỮA: BẢNG DANH SÁCH KHUYẾN MÃI
        // ==========================================
        String[] cols = {"Mã KM", "Tên KM", "Phần trăm giảm", "Ngày bắt đầu", "Ngày kết thúc", "Đơn tối thiểu"};
        modelKhuyenMai = new DefaultTableModel(cols, 0) {
        	// Khoá các ô của Bảng (Chế độ "Chỉ đọc")
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblKhuyenMai = new JTable(modelKhuyenMai);
        JScrollPane scrollKM = new JScrollPane(tblKhuyenMai);
        scrollKM.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(Color.BLUE), "Danh sách Khuyến mãi"));
        pnlChinh.add(scrollKM, BorderLayout.CENTER);

        add(pnlChinh, BorderLayout.CENTER);

        // ==========================================
        // 3. ĐĂNG KÝ SỰ KIỆN & NẠP DỮ LIỆU
        // ==========================================
        btnTim.addActionListener(this);
        btnThem.addActionListener(this);
        btnSua.addActionListener(this);
        btnXoa.addActionListener(this);
        btnLamMoi.addActionListener(this);

        // Sự kiện click cho bảng ds Khuyến mãi
        tblKhuyenMai.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblKhuyenMai.getSelectedRow();
                if (row >= 0) {
                    txtMaKM.setText(modelKhuyenMai.getValueAt(row, 0).toString());
                    txtTenKM.setText(modelKhuyenMai.getValueAt(row, 1).toString());
                    txtPhanTramGiam.setText(modelKhuyenMai.getValueAt(row, 2).toString());
                    txtNgayBatDau.setText(modelKhuyenMai.getValueAt(row, 3).toString());
                    txtNgayKetThuc.setText(modelKhuyenMai.getValueAt(row, 4).toString());
                    txtDonToiThieu.setText(modelKhuyenMai.getValueAt(row, 5).toString());
                }
            }
        });

        // Tải dữ liệu ban đầu
        taiDuLieuKhuyenMai();
    }
    
    // ==========================================
    // CÁC HÀM XỬ LÝ SỰ KIỆN & RÀNG BUỘC
    // ==========================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        if (o.equals(btnThem)) {
            themKhuyenMai();
        } else if (o.equals(btnSua)) {
            suaKhuyenMai();
        } else if (o.equals(btnXoa)) {
            xoaKhuyenMai();
        } else if (o.equals(btnTim)) {
            timKhuyenMai();
        } else if (o.equals(btnLamMoi)) {
            lamMoi();
        }
    }

    private void taiDuLieuKhuyenMai() {
        modelKhuyenMai.setRowCount(0);
        List<KhuyenMai> ds = khuyenMaiDAO.layTatCaKhuyenMai();
        for (KhuyenMai km : ds) {
            modelKhuyenMai.addRow(new Object[]{
                km.getMaKM(),
                km.getTenKM(),
                km.getPhanTramGiam(),
                km.getNgayBatDau() != null ? km.getNgayBatDau().format(dtf) : "",
                km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(dtf) : "",
                km.getDonToiThieu()
            });
        }
    }

    private void lamMoi() {
        txtMaKM.setText("");
        txtTenKM.setText("");
        txtPhanTramGiam.setText("");
        txtNgayBatDau.setText("");
        txtNgayKetThuc.setText("");
        txtDonToiThieu.setText("0");
        txtTimKiem.setText("");
        tblKhuyenMai.clearSelection();
        taiDuLieuKhuyenMai();
        txtTenKM.requestFocus();
    }

    // Kiểm tra toàn bộ ràng buộc theo tài liệu cho đặc tả class KhuyenMai
    private KhuyenMai kiemTraVaLayDuLieu(String maKM) {
        String tenKM = txtTenKM.getText().trim();
        String strPhanTram = txtPhanTramGiam.getText().trim();
        String strNgayBD = txtNgayBatDau.getText().trim();
        String strNgayKT = txtNgayKetThuc.getText().trim();
        String strDonToiThieu = txtDonToiThieu.getText().trim();

        // 1. Tên KM: không được để trống, tối đa 100 ký tự
        if (tenKM.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên Khuyến mãi không được để trống!");
            txtTenKM.requestFocus();
            return null;
        }
        if (tenKM.length() > 100) {
            JOptionPane.showMessageDialog(this, "Tên Khuyến mãi không được vượt quá 100 ký tự!");
            txtTenKM.requestFocus();
            return null;
        }

        // 2. Phần trăm giảm: từ 0 đến 1 (VD: 0.1 = 10%)
        BigDecimal phanTramGiam;
        try {
            phanTramGiam = new BigDecimal(strPhanTram);
            if (phanTramGiam.compareTo(BigDecimal.ZERO) < 0 || phanTramGiam.compareTo(BigDecimal.ONE) > 0) {
                JOptionPane.showMessageDialog(this, "Phần trăm giảm phải nằm trong khoảng từ 0 đến 1 (VD: 0.1 = 10%)!");
                txtPhanTramGiam.requestFocus();
                return null;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Phần trăm giảm phải là số hợp lệ (từ 0 đến 1)!");
            txtPhanTramGiam.requestFocus();
            return null;
        }

        // 3. Ngày bắt đầu & Ngày kết thúc: không để trống, ngày bắt đầu <= ngày kết thúc
        if (strNgayBD.isEmpty() || strNgayKT.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu và ngày kết thúc không được để trống!");
            return null;
        }
        LocalDate ngayBatDau;
        LocalDate ngayKetThuc;
        try {
            ngayBatDau = LocalDate.parse(strNgayBD, dtf);
            ngayKetThuc = LocalDate.parse(strNgayKT, dtf);
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Định dạng ngày không hợp lệ! Vui lòng nhập theo dạng dd/MM/yyyy (VD: 15/11/2026).");
            return null;
        }

        if (ngayBatDau.isAfter(ngayKetThuc)) {
            JOptionPane.showMessageDialog(this, "Ngày bắt đầu không được lớn hơn ngày kết thúc!");
            txtNgayBatDau.requestFocus();
            return null;
        }

        // 4. Đơn tối thiểu: mặc định là 0, giá trị không âm
        BigDecimal donToiThieu = BigDecimal.ZERO;
        if (!strDonToiThieu.isEmpty()) {
            try {
                donToiThieu = new BigDecimal(strDonToiThieu);
                if (donToiThieu.compareTo(BigDecimal.ZERO) < 0) {
                    JOptionPane.showMessageDialog(this, "Đơn tối thiểu không được âm!");
                    txtDonToiThieu.requestFocus();
                    return null;
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Đơn tối thiểu phải là số!");
                txtDonToiThieu.requestFocus();
                return null;
            }
        }

        return new KhuyenMai(maKM, tenKM, phanTramGiam, ngayBatDau, ngayKetThuc, donToiThieu);
    }

    // THÊM KHUYẾN MÃI MỚI VÀO BẢNG
    private void themKhuyenMai() {
        String maMoi = khuyenMaiDAO.phatSinhMaKM();
        KhuyenMai km = kiemTraVaLayDuLieu(maMoi);
        if (km == null) return;

        if (khuyenMaiDAO.themKhuyenMai(km)) {
            JOptionPane.showMessageDialog(this, "Đã thêm khuyến mãi thành công!");
            taiDuLieuKhuyenMai();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    // CẬP NHẬT KHUYẾN MÃI TRONG BẢNG
    private void suaKhuyenMai() {
        int row = tblKhuyenMai.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn khuyến mãi cần sửa!");
            return;
        }

        String maKM = txtMaKM.getText().trim();
        KhuyenMai km = kiemTraVaLayDuLieu(maKM);
        if (km == null) return;

        if (khuyenMaiDAO.suaKhuyenMai(km)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật khuyến mãi thành công!");
            taiDuLieuKhuyenMai();
            lamMoi();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật khuyến mãi thất bại!");
        }
    }

    // XOÁ KHUYẾN MÃI
    private void xoaKhuyenMai() {
        int row = tblKhuyenMai.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Hãy chọn khuyến mãi bạn muốn xóa!");
            return;
        }

        int xacNhan = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa khuyến mãi này không?",
                "Cảnh báo!", JOptionPane.YES_NO_OPTION);

        if (xacNhan == JOptionPane.YES_OPTION) {
            String maKM = modelKhuyenMai.getValueAt(row, 0).toString();
            try {
                if (khuyenMaiDAO.xoaKhuyenMai(maKM)) {
                    JOptionPane.showMessageDialog(this, "Đã xóa khuyến mãi thành công!");
                    taiDuLieuKhuyenMai();
                    lamMoi();
                } else {
                    JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa vì khuyến mãi này đã được áp dụng trong hóa đơn!");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Lỗi ràng buộc: Khuyến mãi này đang tồn tại trong hóa đơn!");
            }
        }
    }

    // TÌM KHUYẾN MÃI THEO MÃ
    private void timKhuyenMai() {
        String str = txtTimKiem.getText();
        if (str != null && str.trim().length() > 0) {
            try {
                String maTim = str.trim();
                // 1. Gọi hàm tìm kiếm từ CSDL
                KhuyenMai km = khuyenMaiDAO.timKhuyenMaiTheoMa(maTim);

                // 2. Kiểm tra nếu km trả về có dữ liệu (tìm thấy)
                if (km != null) {
                    modelKhuyenMai.setRowCount(0);
                    modelKhuyenMai.addRow(new Object[]{
                        km.getMaKM(),
                        km.getTenKM(),
                        km.getPhanTramGiam(),
                        km.getNgayBatDau() != null ? km.getNgayBatDau().format(dtf) : "",
                        km.getNgayKetThuc() != null ? km.getNgayKetThuc().format(dtf) : "",
                        km.getDonToiThieu()
                    });
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy Khuyến mãi có mã: " + maTim);
                    txtTimKiem.selectAll();
                    txtTimKiem.requestFocus();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
                ex.printStackTrace();
            }
        } else {
            modelKhuyenMai.setRowCount(0); // Xóa hết dữ liệu trên TableModel
            taiDuLieuKhuyenMai(); // Đọc dữ liệu Database vào Table
        }
    }
}