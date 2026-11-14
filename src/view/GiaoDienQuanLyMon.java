package view;

import dao.LoaiMon_DAO;
import dao.Mon_DAO;
import entity.LoaiMon;
import entity.Mon;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.math.BigDecimal;
import java.util.List;

public class GiaoDienQuanLyMon extends JPanel implements ActionListener {
    private JTextField txtMaLoai, txtTenLoai, txtMoTaLoai, txtTimLoai;
    private JButton btnThemLoai, btnXoaLoai, btnLamMoiLoai, btnTimLoai;
    private JTable tblLoaiMon;
    private DefaultTableModel modelLoaiMon;

    private JTextField txtMaMon, txtTenMon, txtDonViTinh, txtGiaNhap, txtLoiNhuan, txtDonGia;
    private JComboBox<String> cboTrangThaiMon, cboLoaiMon;
    private JButton btnThemMon, btnSuaMon, btnXoaMon, btnLamMoiMon, btnTimMon;
    private JTable tblMon;
    private DefaultTableModel modelMon;

    // --- DAO ---
    private LoaiMon_DAO loaiMonDAO;
    private Mon_DAO monDAO;
    
    // -- THIẾT KẾ GIAO DIỆN ĐƠN GIẢN --
    public GiaoDienQuanLyMon() {
        loaiMonDAO = new LoaiMon_DAO();
        monDAO = new Mon_DAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // CHIA MÀN HÌNH LÀM 2 PHẦN: TRÁI (Loại món) - PHẢI (Món)
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setResizeWeight(0.4); // Chia 40% Layout cho Loại món và 60% cho Món

        // ==========================================
        // 1. PHẦN BÊN TRÁI: QUẢN LÝ LOẠI MÓN
        // ==========================================
        JPanel pnlTrai = new JPanel(new BorderLayout(5, 5));
        pnlTrai.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "1. Quản lý danh mục (Loại món)"));

        // Form nhập Loại món
        JPanel pnlFormLoai = new JPanel(new GridLayout(3, 2, 5, 5));
        pnlFormLoai.add(new JLabel("Mã loại món:"));
        txtMaLoai = new JTextField();
        txtMaLoai.setEditable(false);
        pnlFormLoai.add(txtMaLoai);

        pnlFormLoai.add(new JLabel("Tên loại món:"));
        txtTenLoai = new JTextField();
        pnlFormLoai.add(txtTenLoai);

        pnlFormLoai.add(new JLabel("Mô tả:"));
        txtMoTaLoai = new JTextField();
        pnlFormLoai.add(txtMoTaLoai);

        // Các nút chức năng của "Quản lý Loại món"
        JPanel pnlChucNangLoai = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        pnlChucNangLoai.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        pnlChucNangLoai.add(new JLabel("Nhập mã Loại cần tìm:"));
        txtTimLoai = new JTextField(10);
        pnlChucNangLoai.add(txtTimLoai);
        
        btnTimLoai = new JButton("Tìm kiếm");
        btnTimLoai.setBackground(new Color(70, 130, 180));
        btnTimLoai.setForeground(Color.WHITE);
        
        btnThemLoai = new JButton("Thêm Loại món");
        btnXoaLoai = new JButton("Xóa");
        btnLamMoiLoai = new JButton("Làm mới");
        
        pnlChucNangLoai.add(btnTimLoai);
        // Tạo khoảng trắng ngăn cách phần Tìm kiếm với các nút chức năng
        pnlChucNangLoai.add(Box.createHorizontalStrut(30));
        pnlChucNangLoai.add(btnThemLoai);
        pnlChucNangLoai.add(btnXoaLoai);
        pnlChucNangLoai.add(btnLamMoiLoai);

        JPanel pnlNorthTrai = new JPanel(new BorderLayout());
        pnlNorthTrai.add(pnlFormLoai, BorderLayout.CENTER);
        pnlNorthTrai.add(pnlChucNangLoai, BorderLayout.SOUTH);
        pnlTrai.add(pnlNorthTrai, BorderLayout.NORTH);

        // Bảng Loại món
        String[] colLoai = {"Mã Loại", "Tên Loại", "Mô tả"};
        modelLoaiMon = new DefaultTableModel(colLoai, 0) {
        	// Khoá các ô của Bảng (Chế độ "Chỉ đọc")
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblLoaiMon = new JTable(modelLoaiMon);
        JScrollPane scrollLoaiMon = new JScrollPane(tblLoaiMon);
        scrollLoaiMon.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Danh sách Loại món"));
        pnlTrai.add(scrollLoaiMon, BorderLayout.CENTER);


        // ==========================================
        // 2. PHẦN BÊN PHẢI: QUẢN LÝ MÓN
        // ==========================================
        JPanel pnlPhai = new JPanel(new BorderLayout(5, 5));
        pnlPhai.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "2. Quản lý Món nước"));

        // Form nhập Món
        JPanel pnlFormMon = new JPanel(new GridLayout(4, 4, 5, 5));
        
        pnlFormMon.add(new JLabel("Mã món:"));
        txtMaMon = new JTextField();
        txtMaMon.setEditable(false);
        pnlFormMon.add(txtMaMon);

        pnlFormMon.add(new JLabel("Tên món:"));
        txtTenMon = new JTextField();
        pnlFormMon.add(txtTenMon);

        pnlFormMon.add(new JLabel("Loại món:"));
        cboLoaiMon = new JComboBox<>();
        pnlFormMon.add(cboLoaiMon);

        pnlFormMon.add(new JLabel("Đơn vị tính:"));
        txtDonViTinh = new JTextField();
        pnlFormMon.add(txtDonViTinh);

        pnlFormMon.add(new JLabel("Giá nhập:"));
        txtGiaNhap = new JTextField();
        pnlFormMon.add(txtGiaNhap);

        pnlFormMon.add(new JLabel("Lợi nhuận:"));
        txtLoiNhuan = new JTextField();
        pnlFormMon.add(txtLoiNhuan);

        pnlFormMon.add(new JLabel("Đơn giá:"));
        txtDonGia = new JTextField("0");
        txtDonGia.setEditable(false);
        txtDonGia.setForeground(Color.RED);
        pnlFormMon.add(txtDonGia);

        pnlFormMon.add(new JLabel("Trạng thái:"));
        cboTrangThaiMon = new JComboBox<>(new String[]{"Đang bán", "Ngừng bán"});
        pnlFormMon.add(cboTrangThaiMon);

        // Các nút chức năng của "Quản lý Món"
        JPanel pnlChucNangMon = new JPanel(new FlowLayout());
        pnlChucNangMon.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        btnThemMon = new JButton("Thêm Món");
        btnSuaMon = new JButton("Cập nhật Món");
        btnXoaMon = new JButton("Xóa");
        btnLamMoiMon = new JButton("Làm mới");    
        btnTimMon = new JButton("Tìm theo tên Món");
        btnTimMon.setBackground(new Color(70, 130, 180));
        btnTimMon.setForeground(Color.WHITE);
        
        pnlChucNangMon.add(btnThemMon);
        pnlChucNangMon.add(btnSuaMon);
        pnlChucNangMon.add(btnXoaMon);
        pnlChucNangMon.add(btnLamMoiMon);
        
        pnlChucNangMon.add(Box.createHorizontalStrut(30));
        pnlChucNangMon.add(btnTimMon);

        JPanel pnlNorthPhai = new JPanel(new BorderLayout());
        pnlNorthPhai.add(pnlFormMon, BorderLayout.CENTER);
        pnlNorthPhai.add(pnlChucNangMon, BorderLayout.SOUTH);
        pnlPhai.add(pnlNorthPhai, BorderLayout.NORTH);

        // Bảng Món
        String[] colMon = {"Mã Món", "Tên Món", "Loại Món", "ĐVT", "Giá nhập", "Lợi nhuận", "Đơn giá", "Trạng thái"};
        modelMon = new DefaultTableModel(colMon, 0) {
        	// Khoá các ô của Bảng (Chế độ "Chỉ đọc")
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tblMon = new JTable(modelMon);
        JScrollPane scrollMon = new JScrollPane(tblMon);
        scrollMon.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.BLUE), "Danh sách Món nước"));
        pnlPhai.add(scrollMon, BorderLayout.CENTER);

        splitPane.setLeftComponent(pnlTrai);
        splitPane.setRightComponent(pnlPhai);
        add(splitPane, BorderLayout.CENTER);

        // ==========================================
        // 3. ĐĂNG KÝ SỰ KIỆN VÀ NẠP DỮ LIỆU
        // ==========================================
        btnTimLoai.addActionListener(this);
        btnThemLoai.addActionListener(this);
        btnXoaLoai.addActionListener(this);
        btnLamMoiLoai.addActionListener(this);
        
        btnThemMon.addActionListener(this);
        btnSuaMon.addActionListener(this);
        btnXoaMon.addActionListener(this);
        btnLamMoiMon.addActionListener(this);
        btnTimMon.addActionListener(this);

        // Sự kiện click bảng Loại món
        tblLoaiMon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblLoaiMon.getSelectedRow();
                if (row >= 0) {
                    txtMaLoai.setText(modelLoaiMon.getValueAt(row, 0).toString());
                    txtTenLoai.setText(modelLoaiMon.getValueAt(row, 1).toString());
                    txtMoTaLoai.setText(modelLoaiMon.getValueAt(row, 2).toString());
                }
            }
        });

        // Sự kiện click bảng Món
        tblMon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = tblMon.getSelectedRow();
                if (row >= 0) {
                    txtMaMon.setText(modelMon.getValueAt(row, 0).toString());
                    txtTenMon.setText(modelMon.getValueAt(row, 1).toString());
                    cboLoaiMon.setSelectedItem(modelMon.getValueAt(row, 2).toString());
                    txtDonViTinh.setText(modelMon.getValueAt(row, 3).toString());
                    txtGiaNhap.setText(modelMon.getValueAt(row, 4).toString());
                    txtLoiNhuan.setText(modelMon.getValueAt(row, 5).toString());
                    txtDonGia.setText(modelMon.getValueAt(row, 6).toString());
                    cboTrangThaiMon.setSelectedItem(modelMon.getValueAt(row, 7).toString());
                }
            }
        });
        
        // Tải dữ liệu ban đầu
        taiDuLieuLoaiMon();
        taiDuLieuMon();
    }

    // ==========================================
    // CÁC HÀM XỬ LÝ SỰ KIỆN & RÀNG BUỘC
    // ==========================================
    @Override
    public void actionPerformed(ActionEvent e) {
        Object o = e.getSource();
        // Loại Món
        if (o.equals(btnThemLoai)) {
        	themLoaiMon();
        }
        else if (o.equals(btnXoaLoai)) {
        	xoaLoaiMon();
        }
        else if (o.equals(btnTimLoai)) {
        	timLoaiMon();
        }
        else if (o.equals(btnLamMoiLoai)) {
        	lamMoiLoai();
        }
        
        // Món
        else if (o.equals(btnThemMon)) {
        	themMon();
        }
        else if (o.equals(btnSuaMon)) {
        	suaMon();
        }
        else if (o.equals(btnXoaMon)) {
        	xoaMon();
        }
        else if (o.equals(btnLamMoiMon)) {
        	lamMoiMon();
        }
        else if (o.equals(btnTimMon)) {
        	timMonTheoTen();
        }
    }

    private void taiDuLieuLoaiMon() {
        modelLoaiMon.setRowCount(0);
        cboLoaiMon.removeAllItems();
        List<LoaiMon> dsLoai = loaiMonDAO.layTatCaLoaiMon();
        for (LoaiMon lm : dsLoai) {
            modelLoaiMon.addRow(new Object[]{lm.getMaLoai(), lm.getTenLoai(), lm.getMoTa()});
            cboLoaiMon.addItem(lm.getTenLoai());
        }
    }

    private void taiDuLieuMon() {
        modelMon.setRowCount(0);
        List<Mon> dsMon = monDAO.layTatCaMon();
        for (Mon m : dsMon) {
            modelMon.addRow(new Object[]{
                m.getMaMon(), m.getTenMon(), m.getLoaiMon().getTenLoai(), 
                m.getDonViTinh(), m.getGiaNhap(), m.getLoiNhuan(), m.getDonGia(), m.getTrangThai()
            });
        }
    }

    private void lamMoiLoai() {
        txtMaLoai.setText("");
        txtTenLoai.setText("");
        txtMoTaLoai.setText("");
        txtTimLoai.setText("");
        tblLoaiMon.clearSelection();
        taiDuLieuLoaiMon();
        txtTenLoai.requestFocus();
    }

    private void lamMoiMon() {
        txtMaMon.setText("");
        txtTenMon.setText("");
        txtDonViTinh.setText("");
        txtGiaNhap.setText("");
        txtLoiNhuan.setText("");
        txtDonGia.setText("0");
        if (cboLoaiMon.getItemCount() > 0) {
        	cboLoaiMon.setSelectedIndex(0);
        }
        cboTrangThaiMon.setSelectedIndex(0);
        tblMon.clearSelection();
        taiDuLieuMon();
        txtTenMon.requestFocus();
    }
    
    // ==========================================
    // CÁC HÀM XỬ LÝ LOẠI MÓN
    // ==========================================

    // THÊM LOẠI MÓN (Ràng buộc: Tên loại không được để trống, tối đa 50 ký tự; 
    // Mã loại được sinh theo quy luật: LM + [XX], tự động sinh tăng dần; mô tả tối đa 255 ký tự)
    private void themLoaiMon() {
        String tenLoai = txtTenLoai.getText().trim();
        String moTa = txtMoTaLoai.getText().trim();

        if (tenLoai.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên loại món không được để trống!");
            txtTenLoai.requestFocus();
            return;
        }
        
        // Bổ sung các ràng buộc độ dài ký tự
        if (tenLoai.length() > 50) {
            JOptionPane.showMessageDialog(this, "Tên loại món không được vượt quá 50 ký tự!");
            txtTenLoai.requestFocus();
            return;
        }
        
        if (moTa.length() > 255) {
             JOptionPane.showMessageDialog(this, "Mô tả không được vượt quá 255 ký tự!");
             txtMoTaLoai.requestFocus();
             return;
        }

        String maLoaiMoi = loaiMonDAO.phatSinhMaLoai();
        LoaiMon lm = new LoaiMon(maLoaiMoi, tenLoai, moTa);
        if (loaiMonDAO.themLoaiMon(lm)) {
            JOptionPane.showMessageDialog(this, "Đã thêm loại món thành công!");
            taiDuLieuLoaiMon();
            lamMoiLoai();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm thất bại!");
        }
    }

    
    // XOÁ LOẠI MÓN
    private void xoaLoaiMon() {
        int r = tblLoaiMon.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn dòng bạn muốn xóa!");
        } else {
            if (JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa dòng này không?", 
                    "Cảnh báo",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
                String maLoai = modelLoaiMon.getValueAt(r, 0).toString();
                
                try {
                    if (loaiMonDAO.xoaLoaiMon(maLoai)) {
                        modelLoaiMon.removeRow(r);
                        taiDuLieuMon(); 
                        lamMoiLoai();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa vì Loại món này đang có trong CSDL!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi ràng buộc: Không thể xóa vì Loại món đang được sử dụng!");
                }
            }
        }
    }
    
    // TÌM LOẠI MÓN THEO MÃ
    private void timLoaiMon() {
        String str = txtTimLoai.getText();
        if (str != null && str.trim().length() > 0) {
            try {
                String maTim = str.trim();
                // 1. Gọi hàm tìm kiếm từ CSDL
                LoaiMon lm = loaiMonDAO.timLoaiMonTheoMa(maTim);

                // 2. Kiểm tra nếu lm trả về có dữ liệu (nghĩa là tìm thấy)
                if (lm != null) {
                    modelLoaiMon.setRowCount(0); // Xóa hết dữ liệu trên TableModel
                    modelLoaiMon.addRow(new Object[]{lm.getMaLoai(), lm.getTenLoai(), lm.getMoTa()});
                } else {
                    JOptionPane.showMessageDialog(this, "Không tìm thấy Loại món có mã: " + maTim);
                    txtTimLoai.selectAll();
                    txtTimLoai.requestFocus();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Dữ liệu không hợp lệ!");
                ex.printStackTrace();
            }
        } else {
            modelLoaiMon.setRowCount(0); // Xóa hết dữ liệu trên TableModel
            taiDuLieuLoaiMon(); // Đọc dữ liệu Database vào Table
        }
    }
    
    // ==========================================
    // CÁC HÀM XỬ LÝ MÓN NƯỚC
    // ==========================================
    
    // THÊM MÓN (Ràng buộc: Mã món được sinh theo quy luật: M + [XXX], tự động sinh tăng dần theo loại;
    // Tên món không được để trống, tối đa 100 ký tự; Đơn giá, giá nhập và lợi nhuận có giá trị không âm; 
    // đơn vị tính tối đa 20 ký tự)
    private void themMon() {
        String tenMon = txtTenMon.getText().trim();
        String donViTinh = txtDonViTinh.getText().trim();
        String tenLoai = (String) cboLoaiMon.getSelectedItem();
        String trangThai = (String) cboTrangThaiMon.getSelectedItem();

        if (tenMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món không được để trống!");
            txtTenMon.requestFocus();
            return;
        }
        
        // Bổ sung ràng buộc độ dài ký tự
        if (tenMon.length() > 100) {
             JOptionPane.showMessageDialog(this, "Tên món không được vượt quá 100 ký tự!");
             txtTenMon.requestFocus();
             return;
        }
        
        if (donViTinh.length() > 20) {
             JOptionPane.showMessageDialog(this, "Đơn vị tính không được vượt quá 20 ký tự!");
             txtDonViTinh.requestFocus();
             return;
        }
        
//      if (tenLoai == null) {
//          JOptionPane.showMessageDialog(this, "Vui lòng chọn Loại món trước!");
//          return;
//      }

        BigDecimal giaNhap = BigDecimal.ZERO;
        BigDecimal loiNhuan = BigDecimal.ZERO;
        try {
            giaNhap = new BigDecimal(txtGiaNhap.getText().trim());
            loiNhuan = new BigDecimal(txtLoiNhuan.getText().trim());
            if (giaNhap.compareTo(BigDecimal.ZERO) < 0 || loiNhuan.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập và lợi nhuận không được âm!");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giá nhập và lợi nhuận phải là số!");
            return;
        }

        LoaiMon loaiMon = loaiMonDAO.timLoaiMonTheoTen(tenLoai);
        String maMonMoi = monDAO.phatSinhMaMon();
        Mon mon = new Mon(maMonMoi, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, loaiMon);
        
        if (monDAO.themMon(mon)) {
            JOptionPane.showMessageDialog(this, "Đã thêm món thành công!");
            taiDuLieuMon();
            lamMoiMon();
        } else {
            JOptionPane.showMessageDialog(this, "Thêm món thất bại!");
        }
    }

    // SỬA MÓN
    private void suaMon() {
        int row = tblMon.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn món cần sửa!");
            return;
        }
        
        String maMon = txtMaMon.getText();
        String tenMon = txtTenMon.getText().trim();
        String donViTinh = txtDonViTinh.getText().trim();
        String tenLoai = (String) cboLoaiMon.getSelectedItem();
        String trangThai = (String) cboTrangThaiMon.getSelectedItem();

        if (tenMon.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tên món không được để trống!");
            txtTenMon.requestFocus();
            return;
        }
        
        // Bổ sung ràng buộc độ dài cho Món
        if (tenMon.length() > 100) {
             JOptionPane.showMessageDialog(this, "Tên món không được vượt quá 100 ký tự!");
             txtTenMon.requestFocus();
             return;
        }
        
        if (donViTinh.length() > 20) {
             JOptionPane.showMessageDialog(this, "Đơn vị tính không được vượt quá 20 ký tự!");
             txtDonViTinh.requestFocus();
             return;
        }

        BigDecimal giaNhap = BigDecimal.ZERO;
        BigDecimal loiNhuan = BigDecimal.ZERO;
        try {
            giaNhap = new BigDecimal(txtGiaNhap.getText().trim());
            loiNhuan = new BigDecimal(txtLoiNhuan.getText().trim());
            if (giaNhap.compareTo(BigDecimal.ZERO) < 0 || loiNhuan.compareTo(BigDecimal.ZERO) < 0) {
                JOptionPane.showMessageDialog(this, "Giá nhập và lợi nhuận không được âm!");
                return;
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Giá nhập và lợi nhuận phải là số!");
            return;
        }

        LoaiMon loaiMon = loaiMonDAO.timLoaiMonTheoTen(tenLoai);
        Mon mon = new Mon(maMon, tenMon, donViTinh, trangThai, giaNhap, loiNhuan, loaiMon);
        
        if (monDAO.suaMon(mon)) {
            JOptionPane.showMessageDialog(this, "Đã cập nhật thông tin món thành công!");
            taiDuLieuMon();
            lamMoiMon();
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật món thất bại!");
        }
    }
    
    // XOÁ MÓN
    private void xoaMon() {
        int r = tblMon.getSelectedRow();
        if (r == -1) {
            JOptionPane.showMessageDialog(this, "Hãy chọn dòng bạn muốn xóa!");
        } else {
            if (JOptionPane.showConfirmDialog(this,
                    "Bạn có chắc chắn muốn xóa dòng này không?", 
                    "Cảnh báo!",
                    JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                
                String maMon = modelMon.getValueAt(r, 0).toString();
                
                try {
                    if (monDAO.xoaMon(maMon)) {
                        modelMon.removeRow(r);
                        lamMoiMon();
                    } else {
                        JOptionPane.showMessageDialog(this, "Lỗi: Không thể xóa món này trong CSDL!");
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Lỗi ràng buộc: Món này đã tồn tại trong hóa đơn!");
                }
            }
        }
    }

    // TÌM KIẾM MÓN
    private void timMonTheoTen() {
        String tenTim = JOptionPane.showInputDialog(this, "Nhập tên món cần tìm (nhập từ khóa):");
        if (tenTim != null && !tenTim.trim().isEmpty()) {
            modelMon.setRowCount(0);
            List<Mon> dsMon = monDAO.timMonTheoTenTuongDoi(tenTim.trim());
            for (Mon m : dsMon) {
                modelMon.addRow(new Object[]{
                    m.getMaMon(), m.getTenMon(), m.getLoaiMon().getTenLoai(), 
                    m.getDonViTinh(), m.getGiaNhap(), m.getLoiNhuan(), m.getDonGia(), m.getTrangThai()
                });
            }
        }
    }
}