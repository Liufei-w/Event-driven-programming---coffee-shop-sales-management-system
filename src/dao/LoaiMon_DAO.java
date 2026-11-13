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
    
    // --- CODE BỔ SUNG CHO QUẢN LÝ LOẠI MÓN ---
    /**
     * TỰ ĐỘNG PHÁT SINH MÃ LOẠI MÓN
     */
    public String phatSinhMaLoai() {
        String maMoi = "LM01";
        String sql = "SELECT TOP 1 maLoai FROM LoaiMon ORDER BY maLoai DESC";
        try {
            Connection con = ConnectDB.getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String maCu = rs.getString("maLoai"); // VD: LM01
                int stt = Integer.parseInt(maCu.substring(2)) + 1;
                maMoi = String.format("LM%02d", stt);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return maMoi;
    }

    /**
     * TÌM LOẠI MÓN THEO TÊN
     */
    public LoaiMon timLoaiMonTheoTen(String tenLoai) {
        String sql = "SELECT * FROM LoaiMon WHERE tenLoai = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, tenLoai);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new LoaiMon(rs.getString("maLoai"), rs.getString("tenLoai"), rs.getString("moTa"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * THÊM LOẠI MÓN
     */
    public boolean themLoaiMon(LoaiMon lm) {
        String sql = "INSERT INTO LoaiMon(maLoai, tenLoai, moTa) VALUES(?, ?, ?)";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, lm.getMaLoai());
            stmt.setString(2, lm.getTenLoai());
            stmt.setString(3, lm.getMoTa());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    
    /**
     * XÓA LOẠI MÓN THEO MÃ
     */
    public boolean xoaLoaiMon(String maLoai) {
        String sql = "DELETE FROM LoaiMon WHERE maLoai = ?";
        try {
            Connection con = ConnectDB.getConnection();
            PreparedStatement stmt = con.prepareStatement(sql);
            stmt.setString(1, maLoai);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
}