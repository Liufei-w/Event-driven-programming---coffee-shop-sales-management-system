package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.NhanVien_DAO;
import entity.NhanVien;

public class GiaoDienQuanLyNhanVien extends JFrame 
		implements ActionListener, MouseListener {
	
	private static final long serialVersionUID = 1L;
	private static final DateTimeFormatter DINH_DANG_NGAY = DateTimeFormatter
            .ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
	
	private JTextField txtMaNhanVien;
	private JTextField txtTenNhanVien;
	private JComboBox<String> cboGioiTinh;
	private JTextField txtNgaySinh;
	private JTextField txtSoDienThoai;
	private JTextField txtDiaChi;
	private JTextField txtNgayVaoLam;
	private JComboBox<String> cboTrangThai;
	private JButton btnThem;
	private JButton btnXoa;
	private JButton btnSua;
	private JButton btnLamMoi;
	private DefaultTableModel tableModel;
	private JTable table;
	private String maNVDangChon;
	
	private final NhanVien_DAO nhanVienDAO = new NhanVien_DAO();

	public GiaoDienQuanLyNhanVien() {
		setSize(980, 690);
		setResizable(false);
		setTitle("Chức năng quản lý nhân viên");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel p = new JPanel(new BorderLayout());
		this.add(p);
		
		JPanel pTieuDe = new JPanel(new BorderLayout());
		JLabel lblTieuDe = new JLabel("	QUẢN LÝ NHÂN VIÊN");
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 26f));
		lblTieuDe.setForeground(Color.blue);
		JLabel lblMoTa = new JLabel("Thêm, xóa, sửa nhân viên");
		lblMoTa.setForeground(Color.gray);
		pTieuDe.add(lblTieuDe, BorderLayout.NORTH);
		pTieuDe.add(lblMoTa, BorderLayout.SOUTH);
		p.add(pTieuDe, BorderLayout.NORTH);
		
		JPanel pThongTin = new JPanel(new BorderLayout());
		pThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin nhân viên"));
		
		JPanel pNhap = new JPanel(new GridLayout(4, 4));
		pNhap.add(new JLabel("Mã nhân viên: "));
		txtMaNhanVien = new JTextField();
		pNhap.add(txtMaNhanVien);
		pNhap.add(new JLabel("Tên nhân viên: "));
		txtTenNhanVien = new JTextField();
		pNhap.add(txtTenNhanVien);
		pNhap.add(new JLabel("Giới tính: "));
		cboGioiTinh = new JComboBox<String>(new String[] {"Nam", "Nữ"});
		pNhap.add(cboGioiTinh);
		pNhap.add(new JLabel("Ngày sinh (yyyy-MM-dd): "));
		txtNgaySinh = new JTextField();
		pNhap.add(txtNgaySinh);
		pNhap.add(new JLabel("Số điện thoại: "));
		txtSoDienThoai = new JTextField();
		pNhap.add(txtSoDienThoai);
		pNhap.add(new JLabel("Địa chỉ: "));
		txtDiaChi = new JTextField();
		pNhap.add(txtDiaChi);
		pNhap.add(new JLabel("Ngày vào làm (yyyy-MM-dd): "));
		txtNgayVaoLam = new JTextField();
		pNhap.add(txtNgayVaoLam);
		pNhap.add(new JLabel("Trạng thái: "));
		cboTrangThai = new JComboBox<String>(new String[] {"Đang làm việc", "Đã nghỉ việc"});
		pNhap.add(cboTrangThai);
		pThongTin.add(pNhap, BorderLayout.CENTER);
		
		JPanel pNut = new JPanel();
		btnThem = new JButton("Thêm");
		btnXoa = new JButton("Xóa");
		btnSua = new JButton("Sửa");
		btnLamMoi = new JButton("Làm mới");
		pNut.add(btnThem);
		pNut.add(btnXoa);
		pNut.add(btnSua);
		pNut.add(btnLamMoi);
		pThongTin.add(pNut, BorderLayout.SOUTH);
		
		String[] tieuDeCot = {"Mã NV", "Tên nhân viên", "Giới tính", "Ngày sinh", 
				"Số điện thoại", "Địa chỉ", "Ngày vào làm", "Trạng thái"};
		
		tableModel = new DefaultTableModel(tieuDeCot, 0);
		table = new JTable(tableModel);
		
		JPanel pDanhSach = new JPanel(new BorderLayout());
		pDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách nhân viên"));
		pDanhSach.add(new JScrollPane(table), BorderLayout.CENTER);
		table.setRowHeight(30);
		
		JPanel pNoiDung = new JPanel(new BorderLayout());
		pNoiDung.add(pThongTin, BorderLayout.NORTH);
		pNoiDung.add(pDanhSach, BorderLayout.CENTER);
		p.add(pNoiDung, BorderLayout.SOUTH);
		
		btnThem.addActionListener(this);
		btnXoa.addActionListener(this);
		btnSua.addActionListener(this);
		btnLamMoi.addActionListener(this);
		
		xoaTrangForm();
        napDanhSachNhanVien();
	}
	
	public static void main(String[] args) {
		new GiaoDienQuanLyNhanVien().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == btnThem) {
			themNhanVien();
		}
		
		else if(o == btnXoa) {
			xoaNhanVien();
		}
		
		else if(o == btnSua) {
			suaNhanVien();
		}
		
		else if(o == btnLamMoi) {
			xoaTrangForm();
		}
		
	}
	
	
	private NhanVien docNhanVienTuForm() {
        NhanVien nv = new NhanVien();
        nv.setMaNV(txtMaNhanVien.getText().trim());
        nv.setTenNV(txtTenNhanVien.getText().trim());
        nv.setGioiTinh((String) cboGioiTinh.getSelectedItem());
        nv.setNgaySinh(LocalDate.parse(txtNgaySinh.getText().trim(), DINH_DANG_NGAY));
        nv.setSoDienThoai(txtSoDienThoai.getText().trim());
        nv.setDiaChi(txtDiaChi.getText().trim());
        nv.setNgayVaoLam(LocalDate.parse(txtNgayVaoLam.getText().trim(), DINH_DANG_NGAY));
        nv.setTrangThai((String) cboTrangThai.getSelectedItem());
        return nv;
    }

	public void themNhanVien() {
        if (maNVDangChon != null) {
            JOptionPane.showMessageDialog(this, "Nhấn Làm mới để nhập nhân viên mới.");
            return;
        }
        if (!validData()) {
            return;
        }

        NhanVien nv = docNhanVienTuForm();
        try {
	        	if (nhanVienDAO.kiemTraTrungMa(nv.getMaNV())) {
	        	    JOptionPane.showMessageDialog(this,
	        	            "Mã nhân viên " + nv.getMaNV()
	        	            + " đã tồn tại! Vui lòng nhập mã khác.");
	
	        	    txtMaNhanVien.requestFocusInWindow();
	        	    txtMaNhanVien.selectAll();
	        	    return; 
	        	}
        	
            boolean ketQua = nhanVienDAO.themNhanVien(nv);
            if (ketQua) {
                xoaTrangForm();
                napDanhSachNhanVien();
                JOptionPane.showMessageDialog(this, "Đã thêm nhân viên vào database.");
            } else {
                JOptionPane.showMessageDialog(this, "Không thêm được nhân viên.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
	
	public void suaNhanVien() {
        if (maNVDangChon == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần sửa trên bảng.");
            return;
        }
        if (!validData()) {
            return;
        }

        NhanVien nv = docNhanVienTuForm();
        nv.setMaNV(maNVDangChon);
        try {
            boolean ketQua = nhanVienDAO.suaNhanVien(nv);
            if (ketQua) {
                xoaTrangForm();
                napDanhSachNhanVien();
                JOptionPane.showMessageDialog(this, "Đã sửa nhân viên trong database.");
            } else {
                napDanhSachNhanVien();
                JOptionPane.showMessageDialog(this, "Nhân viên không còn tồn tại trong database.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();;
        }
    }
	
	
	public void xoaNhanVien() {
	    if (maNVDangChon == null) {
	        JOptionPane.showMessageDialog(this, "Vui lòng chọn nhân viên cần xóa trên bảng.");
	        return;
	    }
	
	    int luaChon = JOptionPane.showConfirmDialog(this,
	            "Bạn có muốn xóa nhân viên " + maNVDangChon + "?",
	            "Xác nhận xóa", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
	    if (luaChon != JOptionPane.YES_OPTION) {
	        return;
	    }
	
	    try {
	       
	        boolean ketQua = nhanVienDAO.xoaNhanVien(maNVDangChon);
	        if (ketQua) {
	            xoaTrangForm();
	            napDanhSachNhanVien();
	            JOptionPane.showMessageDialog(this, "Đã xóa nhân viên khỏi database.");
	        } else {
	            napDanhSachNhanVien();
	            JOptionPane.showMessageDialog(this, "Nhân viên không còn tồn tại trong database.");
	        }
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	    }
	}
	
	public void napDanhSachNhanVien() {
	    try {
	      
	        ArrayList<NhanVien> danhSach = nhanVienDAO.layDanhSachNhanVien();
	        table.clearSelection();
	        tableModel.setRowCount(0);
	
	        for (int i = 0; i < danhSach.size(); i++) {
	            NhanVien nv = danhSach.get(i);
	            Object[] dong = { nv.getMaNV(), nv.getTenNV(), nv.getGioiTinh(),
	                    dinhDangNgay(nv.getNgaySinh()), nv.getSoDienThoai(),
	                    nv.getDiaChi(), dinhDangNgay(nv.getNgayVaoLam()), nv.getTrangThai() };
	            tableModel.addRow(dong);
	        }
	        xoaTrangForm();
	    } catch (SQLException ex) {
	        ex.printStackTrace();;
	    }
	}
	
	public void xoaTrangForm() {
        table.clearSelection();
        maNVDangChon = null;
        txtMaNhanVien.setText("");
        txtTenNhanVien.setText("");
        cboGioiTinh.setSelectedIndex(0);
        txtNgaySinh.setText("");
        txtSoDienThoai.setText("");
        txtDiaChi.setText("");
        txtNgayVaoLam.setText("");
        cboTrangThai.setSelectedIndex(0);
        txtMaNhanVien.setEditable(true);
        txtMaNhanVien.requestFocusInWindow();
    }
	
	private boolean validData() {
		String maNV = txtMaNhanVien.getText().trim();
        String tenNV = txtTenNhanVien.getText().trim();
        String ngaySinh = txtNgaySinh.getText().trim();
        String soDienThoai = txtSoDienThoai.getText().trim();
        String diaChi = txtDiaChi.getText().trim();
        String ngayVaoLam = txtNgayVaoLam.getText().trim();

        if (!maNV.matches("NV\\d{3}")) {
        		JOptionPane.showMessageDialog(this, "Mã nhân viên phải có dạng NV + 3 chữ số, ví dụ NV001.");
            return false;
        }
        if (tenNV.isEmpty() || tenNV.length() > 100) {
        		JOptionPane.showMessageDialog(this, "Tên nhân viên không được rỗng và tối đa 100 ký tự.");
            return false;
        }
        if (cboGioiTinh.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn giới tính.");
            return false;
        }
        if (ngaySinh.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày sinh theo dạng yyyy-MM-dd.");
            return false;
        }

        LocalDate ngaySinhDate;
        try {
            ngaySinhDate = LocalDate.parse(ngaySinh, DINH_DANG_NGAY);
        } catch (DateTimeParseException ex) {
        		JOptionPane.showMessageDialog(this, "Ngày sinh không tồn tại hoặc sai định dạng yyyy-MM-dd.");
            return false;
        }
        LocalDate homNay = LocalDate.now();
        if (ngaySinhDate.isAfter(homNay)) {
        		JOptionPane.showMessageDialog(this, "Ngày sinh không được lớn hơn ngày hiện tại.");
            return false;
        }
        if (ngaySinhDate.isAfter(homNay.minusYears(18))) {
        		JOptionPane.showMessageDialog(this, "Nhân viên phải đủ 18 tuổi.");
            return false;
        }
        if (!soDienThoai.matches("\\d{1,15}")) {
        		JOptionPane.showMessageDialog(this, "Số điện thoại phải có từ 1 đến 15 chữ số.");
            return false;
        }
        
        if (diaChi.isEmpty() || diaChi.length() > 255) {
        		JOptionPane.showMessageDialog(this, "Địa chỉ không được rỗng và tối đa 255 ký tự.");
            return false;
        }
        if (ngayVaoLam.isEmpty()) {
        		JOptionPane.showMessageDialog(this, "Vui lòng nhập ngày vào làm theo dạng yyyy-MM-dd.");
            return false;
        }
        try {
            LocalDate ngayVaoLamDate = LocalDate.parse(ngayVaoLam, DINH_DANG_NGAY);
            if (ngayVaoLamDate.isAfter(homNay)) {
            		JOptionPane.showMessageDialog(this, "Ngày vào làm không được lớn hơn ngày hiện tại.");
                return false;
            }
        } catch (DateTimeParseException ex) {
        		JOptionPane.showMessageDialog(this, "Ngày vào làm không tồn tại hoặc sai định dạng yyyy-MM-dd.");
            return false;
        }
        if (cboTrangThai.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, "Vui lòng chọn trạng thái nhân viên.");
            return false;
        }
        return true;
	}
	
	private void chonGiaTriComboBox(JComboBox<String> comboBox, String giaTri) {
        if (giaTri.isEmpty()) {
            comboBox.setSelectedItem(null);
            return;
        }
        
        for (int i = 0; i < comboBox.getItemCount(); i++) {
            if (giaTri.equals(comboBox.getItemAt(i))) {
                comboBox.setSelectedIndex(i);
                return;
            }
        }
        comboBox.addItem(giaTri);
        comboBox.setSelectedItem(giaTri);
    }
	
	private String dinhDangNgay(LocalDate ngay) {
        if (ngay == null) {
            return "";
        }
        return ngay.format(DINH_DANG_NGAY);
    }
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int dong = table.getSelectedRow();
		txtMaNhanVien.setText(table.getValueAt(dong, 0).toString());
		txtTenNhanVien.setText(table.getValueAt(dong, 1).toString());
		chonGiaTriComboBox(cboGioiTinh, table.getValueAt(dong, 2).toString());
		txtNgaySinh.setText(table.getValueAt(dong, 3).toString());
		txtSoDienThoai.setText(table.getValueAt(dong, 4).toString());
		txtDiaChi.setText(table.getValueAt(dong, 5).toString());
		txtNgayVaoLam.setText(table.getValueAt(dong, 6).toString());
		chonGiaTriComboBox(cboTrangThai, table.getValueAt(dong, 7).toString());
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
