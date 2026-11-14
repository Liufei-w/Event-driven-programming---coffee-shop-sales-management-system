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
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import dao.Ban_DAO;
import entity.Ban;


public class GiaoDienQuanLyBan extends JFrame 
		implements ActionListener, MouseListener{
	private JTextField txtMaBan;
	private JTextField txtTenBan;
	private JTextField txtSoChoNgoi;
	private JComboBox<String> cboTrangThai;
	private JButton btnThem;
	private JButton btnXoa;
	private JButton btnSua;
	private JButton btnLamMoi;
	private DefaultTableModel tableModel;
	private JTable table;
	private Ban_DAO ban_DAO = new Ban_DAO();

	public GiaoDienQuanLyBan() {
		setTitle("Giao diện quản lý bàn");
		setSize(1000, 700);
		setResizable(false);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		JPanel p = new JPanel(new BorderLayout());
		this.add(p);
		
		JPanel pTieuDe = new JPanel(new BorderLayout());
		JLabel lblTieuDe = new JLabel("QUẢN LÝ BÀN");
		lblTieuDe.setFont(lblTieuDe.getFont().deriveFont(Font.BOLD, 32f));
		lblTieuDe.setForeground(Color.blue);
		
		JLabel lblMoTa = new JLabel("Theo dõi và cập nhật thông tin bàn trong quán cà phê");
		lblMoTa.setFont(lblMoTa.getFont().deriveFont(14f));
		lblMoTa.setForeground(Color.LIGHT_GRAY);
		
		pTieuDe.add(lblTieuDe, BorderLayout.NORTH);
		pTieuDe.add(lblMoTa, BorderLayout.SOUTH);
		
		JPanel pNhap = new JPanel(new GridLayout(2, 4));
		pNhap.add(new JLabel("Mã bàn: "));
		txtMaBan = new JTextField();
		pNhap.add(txtMaBan);
		txtMaBan.setEditable(false);
		pNhap.add(new JLabel("Tên bàn: "));
		txtTenBan = new JTextField();
		pNhap.add(txtTenBan);
		pNhap.add(new JLabel("Số chỗ ngồi: "));
		txtSoChoNgoi = new JTextField();
		pNhap.add(txtSoChoNgoi);
		pNhap.add(new JLabel("Trạng thái: "));
		cboTrangThai = new JComboBox<String>(new String[] {"Trống", "Đang phục vụ", "Đã đặt trước"});
		pNhap.add(cboTrangThai);
		
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
		pThongTin.setBorder(BorderFactory.createTitledBorder("Thôn tin bàn"));
		pThongTin.add(pNhap, BorderLayout.CENTER);
		pThongTin.add(pNut, BorderLayout.SOUTH);
		
		JPanel pTren = new JPanel(new BorderLayout());
		pTren.add(pTieuDe, BorderLayout.NORTH);
		pTren.add(pThongTin, BorderLayout.CENTER);
		
		String[] tieuDeCot = {"Mã bàn", "Tên bàn", "Số chỗ ngồi", "Trạng thái"};
		tableModel = new DefaultTableModel(tieuDeCot, 0);
		table = new JTable(tableModel);
		table.setRowHeight(30);
		
		JPanel pDanhSach = new JPanel(new BorderLayout());
		pDanhSach.setBorder(BorderFactory.createTitledBorder("Danh sách bàn"));
		pDanhSach.add(new JScrollPane(table), BorderLayout.CENTER);
		
		p.add(pTren, BorderLayout.NORTH);
		p.add(pDanhSach, BorderLayout.CENTER);
		
		
		btnThem.addActionListener(this);
		btnXoa.addActionListener(this);
		btnSua.addActionListener(this);
		btnLamMoi.addActionListener(this);
		table.addMouseListener(this);
		
		try {
			lamMoi();
			docDuLieuLenBang();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn.");
		}
	}
	
	public static void main(String[] args) {
		new GiaoDienQuanLyBan().setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		
		if(o == btnThem) {
			themBan();
		}
		else if(o == btnXoa) {
			xoaBan();
		}
		else if(o == btnSua) {
			suaBan();
		}
		else if(o == btnLamMoi) {
			lamMoi();
		}
		
	}
	
	private void docDuLieuLenBang() throws SQLException {
		List<Ban> danhSachBan = ban_DAO.layTatCaBan();
		table.clearSelection();
		tableModel.setRowCount(0);
		for (Ban ban : danhSachBan) {
			tableModel.addRow(new Object[] {
					ban.getMaBan(), ban.getTenBan(),
					ban.getSoChoNgoi(), ban.getTrangThai()
			});
		}
	}
	
	private boolean validData() {
		String tenBan = txtTenBan.getText().trim();
		if (tenBan.isEmpty() || tenBan.length() > 50) {
			JOptionPane.showMessageDialog(this, "Tên bàn không được rỗng và phải có từ 1 đến 50 ký tự!");
			txtTenBan.requestFocusInWindow();
			return false;
		}
		try {
			int soChoNgoi = Integer.parseInt(txtSoChoNgoi.getText().trim());
			if (soChoNgoi <= 0) {
				JOptionPane.showMessageDialog(this, "Số chỗ ngồi không được rỗng và phải là số nguyên lớn hơn 0!");
				txtSoChoNgoi.requestFocusInWindow();
				return false;
			}
		} catch (NumberFormatException ex) {
			JOptionPane.showMessageDialog(this, "Vui lòng nhập số chỗ ngồi là số nguyên hợp lệ!");
			txtSoChoNgoi.requestFocusInWindow();
			return false;
		}
		return true;
	}
	
	private Ban layBanTuForm() {
		return new Ban("", txtTenBan.getText().trim(),
				Integer.parseInt(txtSoChoNgoi.getText().trim()),
				cboTrangThai.getSelectedItem().toString());
	}
	
	private void themBan() {
		if (!validData()) {
			return;
		}
		Ban ban = layBanTuForm();
		try {
			if (!ban_DAO.themBan(ban)) {
				JOptionPane.showMessageDialog(this, "Không thêm được bàn!");
				return;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 2627 || e.getErrorCode() == 2601) {
				JOptionPane.showMessageDialog(this,
						"Mã bàn vừa được sử dụng bởi thao tác khác. Vui lòng nhấn Thêm để thử lại!");
			} else {
				JOptionPane.showMessageDialog(this, "Không thể thêm bàn.");
			}
			return;
		}
		JOptionPane.showMessageDialog(this, "Thêm bàn " + ban.getMaBan() + " thành công!");
		try {
			docDuLieuLenBang();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn.");
		}
	}
	
	private void suaBan() {
		int dong = table.getSelectedRow();
		if (dong == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần sửa!");
			return;
		}
		if (!validData()) {
			return;
		}
		Ban ban = layBanTuForm();
		ban.setMaBan(table.getValueAt(dong, 0).toString());
		try {
			if (!ban_DAO.suaBan(ban)) {
				JOptionPane.showMessageDialog(this,
						"Bàn không còn tồn tại trong database. Nhấn Làm mới để tải lại danh sách.");
				return;
			}
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể sửa bàn.");
			return;
		}
		JOptionPane.showMessageDialog(this, "Sửa bàn thành công!");
		try {
			docDuLieuLenBang();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn.");
		}
	}
	
	private void xoaBan() {
		int dong = table.getSelectedRow();
		if (dong == -1) {
			JOptionPane.showMessageDialog(this, "Vui lòng chọn bàn cần xóa!");
			return;
		}
		String maBan = table.getValueAt(dong, 0).toString();
		int luaChon = JOptionPane.showConfirmDialog(this,
				"Bạn có chắc muốn xóa bàn " + maBan + "?", "Xác nhận xóa",
				JOptionPane.YES_NO_OPTION);
		if (luaChon != JOptionPane.YES_OPTION) {
			return;
		}
		try {
			if (!ban_DAO.xoaBan(maBan)) {
				JOptionPane.showMessageDialog(this,
						"Bàn không còn tồn tại trong database. Nhấn Làm mới để tải lại danh sách.");
				return;
			}
		} catch (SQLException e) {
			if (e.getErrorCode() == 547) {
				JOptionPane.showMessageDialog(this,
						"Không thể xóa bàn vì bàn đã được sử dụng trong hóa đơn.");
			} else {
				JOptionPane.showMessageDialog(this, "Không thể xóa bàn.");
			}
			return;
		}
		JOptionPane.showMessageDialog(this, "Xóa bàn thành công!");
		try {
			docDuLieuLenBang();
		} catch (SQLException e) {
			JOptionPane.showMessageDialog(this, "Không thể tải danh sách bàn.");
		}
	}
	
	private void lamMoi() {
		table.clearSelection();
		txtMaBan.setText("");
		txtMaBan.setEditable(false);
		txtTenBan.setText("");
		txtSoChoNgoi.setText("");
		cboTrangThai.setSelectedIndex(0);
		txtTenBan.requestFocusInWindow();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		int dong = table.getSelectedRow();
		
		if(dong == -1) {
			return;
		}
		
		txtMaBan.setText(table.getValueAt(dong, 0).toString());
		txtTenBan.setText(table.getValueAt(dong, 1).toString());
		txtSoChoNgoi.setText(table.getValueAt(dong, 2).toString());
		cboTrangThai.setSelectedItem(table.getValueAt(dong, 3));
		
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
