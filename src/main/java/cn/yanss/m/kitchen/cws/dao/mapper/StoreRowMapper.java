package cn.yanss.m.kitchen.cws.dao.mapper;

import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.lang.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StoreRowMapper implements RowMapper<StoreResponse> {
    @Nullable
    @Override
    public StoreResponse mapRow(ResultSet rs, int i) throws SQLException {
        StoreResponse store = new StoreResponse();
        store.setAddressDetail(rs.getString("address_detail"));
        store.setBardStoreId(rs.getString("bard_store_id"));
        store.setBookDay(rs.getInt("book_day"));
        store.setCity(rs.getString("city"));
        store.setCountry(rs.getString("country"));
        store.setCreateTime(rs.getDate("create_time"));
        store.setDadaStoreId(rs.getString("dada_store_id"));
        store.setEndTime(rs.getTime("end_time"));
        store.setLat(rs.getDouble("lat"));
        store.setLinkman(rs.getString("linkman"));
        store.setLng(rs.getDouble("lng"));
        store.setLocation(rs.getString("location"));
        store.setMeituanStoreId(rs.getString("meituan_store_id"));
        store.setPic(rs.getString("pic"));
        store.setProvince(rs.getString("province"));
        store.setReceiveTime(rs.getInt("receive_time"));
        store.setSendPrice(rs.getDouble("send_price"));
        store.setSendType(rs.getString("send_type"));
        store.setStartTime(rs.getTime("start_time"));
        store.setStatus(rs.getInt("status"));
        store.setStoreId(rs.getInt("store_id"));
        store.setStoreIntro(rs.getString("store_intro"));
        store.setStoreName(rs.getString("store_name"));
        return store;
    }
}
