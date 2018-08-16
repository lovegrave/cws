package cn.yanss.m.kitchen.cws.dao.impl;

import cn.yanss.m.kitchen.cws.dao.StoreDao;
import cn.yanss.m.kitchen.cws.dao.mapper.StoreRowMapper;
import cn.yanss.m.kitchen.cws.entity.response.StoreResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StoreDaoImpl implements StoreDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final StoreRowMapper storeRowMapper = new StoreRowMapper();

    @Override
    public List<Integer> findStoreIds(String sql){
        return jdbcTemplate.queryForList(sql,Integer.TYPE);
    }

    @Override
    public StoreResponse findStore(String sql) {
        return jdbcTemplate.queryForObject(sql,storeRowMapper);
    }

    @Override
    public void updateStore(StoreResponse storeResponse) {
        StringBuilder builder = new StringBuilder(" update tb_store set ");
        if(null != storeResponse.getAddressDetail()){
            builder.append(" address_detail = `" + storeResponse.getAddressDetail()+"`");
        }
        if(null != storeResponse.getBardStoreId()){
            builder.append(" bard_store_id = `"+storeResponse.getBardStoreId()+"`");
        }
        if(null != storeResponse.getBookDay()){
            builder.append(" book_day = " +storeResponse.getBookDay());
        }
        if(null != storeResponse.getCity()){
            builder.append(" city = `"+storeResponse.getCity()+"` ");
        }
        if(null != storeResponse.getCountry()){
            builder.append(" country = `"+storeResponse.getCountry()+"`");
        }
        if(null != storeResponse.getDadaStoreId()){
            builder.append(" dada_store_id = `"+storeResponse.getDadaStoreId()+"`");
        }
        if(null != storeResponse.getEndTime()){
            builder.append(" end_time = `"+storeResponse.getEndTime()+"`");
        }
        if(null != storeResponse.getStartTime()){
            builder.append(" start_time = `"+storeResponse.getStartTime());
        }
        if(null != storeResponse.getLat()){
            builder.append(" lat = "+storeResponse.getLat());
        }
        if(null != storeResponse.getLinkman()){
            builder.append(" linkman = `"+storeResponse.getLinkman()+"`");
        }
        if(null != storeResponse.getLng()){
            builder.append(" lng = "+storeResponse.getLng());
        }
        if(null != storeResponse.getLocation()){
            builder.append(" location = `"+storeResponse.getLocation()+"`");
        }
        if(null != storeResponse.getMeituanStoreId()){
            builder.append(" meituan_store_id = '"+storeResponse.getMeituanStoreId()+"`");
        }
        if(null != storeResponse.getPic()){
            builder.append(" pic = `"+storeResponse.getPic()+"`");
        }
        if(null != storeResponse.getReceiveTime()){
            builder.append(" receive_time = "+storeResponse.getReceiveTime());
        }
        if(null != storeResponse.getSendPrice()){
            builder.append(" send_price = "+storeResponse.getSendPrice());
        }
        if(null != storeResponse.getSendType()){
            builder.append(" send_type = `"+storeResponse.getSendType()+"`");
        }
        if(null != storeResponse.getStatus()){
            builder.append(" status = "+storeResponse.getStatus());
        }
        if(null!= storeResponse.getStoreIntro()){
            builder.append(" store_intro = `"+storeResponse.getStoreIntro()+"`");
        }
        if(null != storeResponse.getStoreName()){
            builder.append(" store_name = `"+storeResponse.getStoreName()+"`");
        }
        builder.append(" where store_id = "+storeResponse.getStoreId());
        jdbcTemplate.update(builder.toString());
    }
}
