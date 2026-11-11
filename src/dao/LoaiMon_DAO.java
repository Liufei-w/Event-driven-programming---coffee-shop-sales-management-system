package dao;

import connectDB.ConnectDB;
import entity.LoaiMon;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoaiMon_DAO {

    /**
     * 1. LẤY DANH SÁCH TẤT CẢ LOẠI MÓN
     */
    public List<LoaiMon> layTatCaLoaiMon() {
        List<LoaiMon> danhSachLoai = new ArrayList<>();
        String sql = "SELECT * FROM LoaiMon";
        
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            
            while (rs.next()) {
                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                String moTa = rs.getString("moTa");

                LoaiMon loaiMon = new LoaiMon(maLoai, tenLoai, moTa);
                danhSachLoai.add(loaiMon);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return danhSachLoai;
    }

    /**
     * TÌM LOẠI MÓN THEO MÃ
     */
    public LoaiMon timLoaiMonTheoMa(String maLoaiTim) {
        String sql = "SELECT * FROM LoaiMon WHERE maLoai = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maLoaiTim);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String maLoai = rs.getString("maLoai");
                String tenLoai = rs.getString("tenLoai");
                String moTa = rs.getString("moTa");
                return new LoaiMon(maLoai, tenLoai, moTa);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}