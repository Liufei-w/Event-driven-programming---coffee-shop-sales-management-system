package dao;

import connectDB.ConnectDB;
import entity.KhuyenMai;

import java.sql.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class KhuyenMai_DAO {
    public List<KhuyenMai> layKhuyenMaiHopLe(BigDecimal tongTienMon) {
        List<KhuyenMai> danhSachKM = new ArrayList<>();
        String sql = "SELECT * FROM KhuyenMai WHERE ngayBatDau <= ? AND ngayKetThuc >= ? AND donToiThieu <= ?";
        
        try {
            Connection con = ConnectDB.getConnection();
            try (PreparedStatement stmt = con.prepareStatement(sql)) {
                LocalDate now = LocalDate.now();
                stmt.setDate(1, Date.valueOf(now));
                stmt.setDate(2, Date.valueOf(now));
                stmt.setBigDecimal(3, tongTienMon != null ? tongTienMon : BigDecimal.ZERO);
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        String maKM = rs.getString("maKM");
                        String tenKM = rs.getString("tenKM");
                        BigDecimal phanTramGiam = rs.getBigDecimal("phanTrangGiam"); 
                        BigDecimal donToiThieu = rs.getBigDecimal("donToiThieu");

                        KhuyenMai km = new KhuyenMai(maKM, tenKM, phanTramGiam, null, null, donToiThieu);
                        danhSachKM.add(km);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachKM;
    }
}