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
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.KhachHang_DAO;
import entity.KhachHang;

public class GiaoDienQuanLyKhachHang extends JFrame 
		implements ActionListener, MouseListener{
	private JTextField txtMaKhachHang;
	private JTextField txtTenKhachHang;
	private JTextField txtSoDienThoai;
	private JButton btnThem;
	private JButton btnXoa;
	private JButton btnSua;
	private JButton btnLamMoi;
	private DefaultTableModel tableModel;
	private JTable table;
	private KhachHang_DAO khachHang_DAO = new KhachHang_DAO();

	public GiaoDienQuanLyKhachHang() {
		setSize(1000, 700);
		setResizable(false);
		setTitle("Chức năng quản lý khách hàng");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel p = new JPanel(new BorderLayout());
		this.add(p);
		
		JPanel pTieuDe = new JPanel(new BorderLayout());
		JLabel lblTieuDe = new JLabel("QUẢN LÝ KHÁCH HÀNG");
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 32f));
		lblTieuDe.setForeground(Color.blue);
		JLabel lblMoTa = new JLabel("Thêm và cập nhật thông tin khách hàng");
		lblMoTa.setForeground(Color.LIGHT_GRAY);
		pTieuDe.add(lblTieuDe, BorderLayout.NORTH);
		pTieuDe.add(lblMoTa, BorderLayout.SOUTH);
		
		JPanel pNhap = new JPanel(new GridLayout(3, 2));
		pNhap.add(new JLabel("Mã khách hàng: "));
		txtMaKhachHang = new JTextField();
		pNhap.add(txtMaKhachHang);
		txtMaKhachHang.setEditable(false);
		pNhap.add(new JLabel("Tên khách hàng: "));
		txtTenKhachHang = new JTextField();
		pNhap.add(txtTenKhachHang);
		pNhap.add(new JLabel("Số điện thoại: "));
		txtSoDienThoai = new JTextField();
		pNhap.add(txtSoDienThoai);
		
		JPanel pNut = new JPanel();
		btnThem = new JButton("Thêm");
		btnXoa = new JButton("Xóa");
		btnSua = new JButton("Sửa");
		btnLamMoi = new JButton("Làm mới");
		pNut.add(btnThem);
		pNut.add(btnXoa);
		pNut.add(btnSua);
		pNut.add(btnLamMoi);
		
		JPanel pThongTin = new JPanel(new BorderLayout());
		pThongTin.setBorder(BorderFactory.createTitledBorder("Thông tin khách hàng"));
		pThongTin.add(pNhap, BorderLayout.CENTER);
		pThongTin.add(pNut, BorderLayout.SOUTH);
		
		JPanel pTren = new JPanel(new BorderLayout());
		pTren.add(pTieuDe, BorderLayout.NORTH);
		pTren.add(pThongTin, BorderLayout.CENTER);
		
		String[] tieuDeCot = {"Mã khách hàng", "Tên khách hàng", "Số điện thoại"};
		tableModel = new DefaultTableModel(tieuDeCot, 0) {
			public boolean isCellEditable(int row, int column) {return false;}
		};
		
		table = new JTable(tableModel);
		table.setRowHeight(30);
		
		JPanel pDanhSach = new JPanel(new BorderLayout());
		pDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách khách hàng"));
		pDanhSach.add(new JScrollPane(table), BorderLayout.CENTER);
		
		p.add(pTren, BorderLayout.NORTH);
		p.add(pDanhSach, BorderLayout.CENTER);
		
		btnThem.addActionListener(this);
		btnXoa.addActionListener(this);
		btnSua.addActionListener(this);
		btnLamMoi.addActionListener(this);
		table.addMouseListener(this);
		
		lamMoi();
		try {
			docDuLieuLenBang();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách khách hàng.");
		}
	}
	
	public static void main(String[] args) {
		new GiaoDienQuanLyKhachHang().setVisible(true);
	}
	
	private void lamMoi() {
		table.clearSelection();
		txtMaKhachHang.setText("");
		txtMaKhachHang.setEditable(false);
		txtTenKhachHang.setText("");
		txtSoDienThoai.setText("");
		txtTenKhachHang.requestFocusInWindow();
	}
	
	private void docDuLieuLenBang() throws SQLException {
		List<KhachHang> danhSach = khachHang_DAO.layTatCaKhachHang();
		table.clearSelection();
		tableModel.setRowCount(0);
		for (KhachHang khachHang : danhSach) {
			tableModel.addRow(new Object[] {
					khachHang.getMaKH(), khachHang.getTenKH(), khachHang.getSoDienThoai()
			});
		}
	}
	
	private boolean validData() {
		String tenKH = txtTenKhachHang.getText().trim();
		if (tenKH.isEmpty() || tenKH.length() > 100) {
			JOptionPane.showMessageDialog(this, "Tên khách hàng không được rỗng và phải có từ 1 đến 100 ký tự!");
			txtTenKhachHang.requestFocusInWindow();
			return false;
		}
		if (txtSoDienThoai.getText().trim().length() > 15 || txtSoDienThoai.getText().trim().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Số điện thoại không được rỗng và không được vượt quá 15 ký tự!");
			txtSoDienThoai.requestFocusInWindow();
			return false;
		}
		return true;
	}
	
	private KhachHang layKhachHangTuForm() {
		return new KhachHang("", txtTenKhachHang.getText().trim(), txtSoDienThoai.getText().trim());
	}
	
	private void themKhachHang() {
		if (!validData()) {
			return;
		}
		KhachHang khachHang = layKhachHangTuForm();
		try {
			if (!khachHang_DAO.themKhachHang(khachHang)) {
				JOptionPane.showMessageDialog(this, "Không thêm được khách hàng!");
				return;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể thêm khách hàng.");
			return;
		}
		JOptionPane.showMessageDialog(this, "Đăng ký khách hàng " + khachHang.getMaKH() + " thành công!");
		try {
			docDuLieuLenBang();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tải lại danh sách khách hàng.");
		}
	}
	
	private void suaKhachHang() {
		int dong = table.getSelectedRow();
		if (dong == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần sửa!");
			return;
		}
		if (!validData()) {
			return;
		}
		KhachHang khachHang = layKhachHangTuForm();
		khachHang.setMaKH(table.getValueAt(dong, 0).toString());
		try {
			if (!khachHang_DAO.suaKhachHang(khachHang)) {
				JOptionPane.showMessageDialog(this,
						"Khách hàng không còn tồn tại. Nhấn Làm mới để tải lại danh sách.");
				return;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể sửa khách hàng.");
			return;
		}
		JOptionPane.showMessageDialog(this, "Sửa khách hàng thành công!");
		try {
			docDuLieuLenBang();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tải lại danh sách khách hàng.");
		}
	}
	
	private void xoaKhachHang() {
		int dong = table.getSelectedRow();
		if (dong == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn khách hàng cần xóa!");
			return;
		}
		String maKH = table.getValueAt(dong, 0).toString();
		int luaChon = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn xóa khách hàng " + maKH + "?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION);
		if (luaChon != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			if (!khachHang_DAO.xoaKhachHang(maKH)) {
				JOptionPane.showMessageDialog(this,
						"Khách hàng không còn tồn tại. Nhấn Làm mới để tải lại danh sách.");
				return;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 547) {
				JOptionPane.showMessageDialog(this,
						"Không thể xóa khách hàng vì đã có hóa đơn tham chiếu đến khách hàng này.");
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa khách hàng.");
			}
			return;
		}
		JOptionPane.showMessageDialog(this, "Xóa khách hàng thành công!");
		try {
			lamMoi();
			docDuLieuLenBang();
		} catch (SQLException ex) {
			JOptionPane.showMessageDialog(this, "Không thể tải lại danh sách khách hàng.");
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int dong = table.getSelectedRow();
		
		if(dong == -1) {
			return;
		}
		
		txtMaKhachHang.setText(table.getValueAt(dong, 0).toString());
		txtTenKhachHang.setText(table.getValueAt(dong, 1).toString());
		txtSoDienThoai.setText(table.getValueAt(dong, 2).toString());
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

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == btnThem) {
			themKhachHang();
		}
		
		else if(o == btnXoa) {
			xoaKhachHang();
		}
		
		else if(o == btnSua) {
			suaKhachHang();
		}
		
		else if(o == btnLamMoi) {
			lamMoi();
		}
		
	}
}
