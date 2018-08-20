package cn.yanss.m.kitchen.cws.cache;

import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis封装类
 * Created by a on 2018/3/19.
 *
 * @author HL
 */
@Service
@Log4j2
public class RedisService {

    /**
     * 日志对象
     */
    @Resource
    private JedisPool jedisPool;

    /**
     * 日志对象
     */
    public synchronized Jedis getJedis() {
        Jedis jedis = jedisPool.getResource();
        jedis.select(4);
        return jedis;
    }

    /**
     * 删除redis一个键值对
     * @param key
     */
    public void remove(String key) {
        Jedis jedis = getJedis();
        try {
            Boolean exists = jedis.exists(key);
            if(exists) {
                jedis.del(key);
            }
        } finally {
            jedis.close();
        }

    }

    /**
     * 删除hash(map)内的一个键值对
     * @param key
     * @param field
     */
    public void hashDel(String key, String field) {
        Jedis jedis = getJedis();
        try {
            Boolean exists = jedis.exists(key);
            if(exists){
                jedis.hdel(key, field);
            }
        } finally {
            jedis.close();
        }
    }

    /**
     * 获取Map对象
     */
    public Map getMap(final String key) {
        try {
            Jedis jedis = getJedis();
            try {
                return jedis.hgetAll(key);
            } finally {
                jedis.close();
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return Collections.emptyMap();
        }
    }

    /**
     * 写入Map对象
     * 无失效时间
     * @param key
     * @param map
     * @return
     */
    public String setMap(String key, Map<String, String> map) {
        Jedis jedis = getJedis();
        String result;
        try {
            result = jedis.hmset(key, map);
        } finally {
            jedis.close();
        }
        return result;
    }


    /**
     * 写入Map对象（有效期）
     * 有失效时间
     * @param key
     * @param map
     * @param seconds 有效期 单位：s
     */
    public String setMap(String key, Map<String, String> map, int seconds) {
        Jedis jedis = getJedis();
        String result;
        try {
            result = jedis.hmset(key, map);
            jedis.expire(key, seconds);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 将一个键值对存入redis map中
     * @param key
     * @param field
     * @param value
     * @param seconds
     * @return
     */
    public Long setMapString(String key, String field, String value,int seconds) {
        Jedis jedis = getJedis();
        Long result;
        try {
            result = jedis.hset(key, field, value);
            jedis.expire(key, seconds);
        } finally {
            jedis.close();
        }
        return result;
    }


    /**
     * map 取一个键值对
     */
    public String getMapString(String key,String field){
        try(Jedis jedis = getJedis()){
            return jedis.hget(key,field);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }

    public Map<String,String> getAllMap(String key){

        try(Jedis jedis = getJedis()){
            return jedis.hgetAll(key);
        }catch (Exception e){
            log.error(e.getMessage());
            return null;
        }
    }
    /**
     * 获取String
     */
    public String getString(String key) {
        Jedis jedis = getJedis();
        String result = null;
        try {
            result = jedis.get(key);
        } finally {
            jedis.close();
        }
        return result;
    }


    /**
     * 写入String
     *
     * @param key
     * @param value
     */
    public String setString(String key, String value) {
        Jedis jedis = getJedis();
        String result;
        try {
            result = jedis.set(key, value);
        } finally {
            jedis.close();
        }
        return result;
    }

    /**
     * 写入String（有效期）
     *
     * @param key
     * @param value
     * @param seconds 有效期 单位：s
     */
    public String setString(String key, String value, int seconds) {
        Jedis jedis = getJedis();
        String result;
        try {
            result = jedis.setex(key, seconds, value);
        } finally {
            jedis.close();
        }
        return result;
    }


    /**
     * 写入object对象
     */
    public String setObject(String key, Object object,int seconds) {
        String result = null;
        try (Jedis jedis = getJedis();){
            result = jedis.set(key, MapperUtils.obj2jsonIgnoreNull(object));
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    /**
     * 获取object对象
     */
    public <T> T getObject(String key, Class<T> clz) {
        try {
            Jedis jedis = getJedis();
            try {
                String result = jedis.get(key);
                if (result != null) {
                    return MapperUtils.json2pojo(result,clz);
                }
            } finally {
                jedis.close();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
            return null;
        }
        return null;
    }


    /**
     * 写入一个值到redis中的list集合
     */
    public Long lpush(String key,  String value,int seconds) {

        Long result = null;
        try(Jedis jedis = getJedis()) {
            result = jedis.lpush(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }

    public Long lpush(String key,int seconds,  String ... value) {
        Long result = null;
        try(Jedis jedis = getJedis()) {
            result = jedis.lpush(key, value);
            jedis.expire(key, seconds);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return result;
    }


    public Long lrem(String key,String value){
        try(Jedis jedis = getJedis()){
            return jedis.lrem(key,0,value);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return 0L;
    }

    /**
     * 取list
     */
    public List<String> lpList(String key) {
        try (Jedis jedis = getJedis()){
            return jedis.lrange(key, 0, -1);
        } catch (Exception e) {
            log.error("Exception", e);
            return Collections.emptyList();
        }
    }

    /**
     * redis 自增
     * @param key
     * @return
     */
    public Long incr(String key,Integer seconds){

        Long value;
        try(Jedis jedis = jedisPool.getResource()){
            jedis.select(1);
            value = jedis.incr(key);
            jedis.expire(key,seconds);
        }
        return value;
    }

    /**
     * 检测缓存中是否有对应的value
     */
    public Boolean exists(String key) {

        Boolean result = false;
        try(Jedis jedis = getJedis()) {
            result = jedis.exists(key);
        }
        return result;
    }

    /**
     * SortedSet
     */

    /**
     * 添加元素到集合，元素在集合中存在则更新对应score
     * sorted Set
     * @param key
     * @param score
     * @param member
     */
    public void zadd(String key,double score,String member,Integer seconds){
        log.info(key);
        try(Jedis jedis = getJedis()){
            jedis.zadd(key,score,member);
            jedis.expire(key,seconds);
        }catch (Exception e){
            log.error("Exception",e);
        }
    }


    /**
     * 查询范围内的元素集合
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Set<String> zrange(String key, Integer start, Integer stop){
        try (Jedis jedis = getJedis()){
             return jedis.zrange(key,start,stop);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return Collections.emptySet();
    }

    /**
     * 返回元素在在集合中的排名
     * @param key
     * @param member
     * @return
     */
    public Long zrank(String key,String member){
        try (Jedis jedis = getJedis()){
            return jedis.zrank(key,member);
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 删除集合中一个或者多个元素
     * @param key
     * @param member
     * @return
     */
    public Long zrem(String key,String ... member){
        try (Jedis jedis = getJedis()){
            if(jedis.exists(key)){
                return jedis.zrem(key,member);
            }
        }catch (Exception e){
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 删除指定排名的元素
     * @param key
     * @param start
     * @param stop
     * @return
     */
    public Long zremRangeRank(String key,Integer start,Integer stop){
        try (Jedis jedis = getJedis()){
             return jedis.zremrangeByRank(key,start,stop);
        }catch (Exception e){
            log.error("Excetion",e);
        }
        return null;
    }

    /**
     * 返回一个元素的score值
     * @param key
     * @param member
     * @return
     */
    public Double zscore(String key,String member){
        try (Jedis jedis = getJedis()){
            return jedis.zscore(key,member);
        }catch (Exception e){
            log.error("Excetion",e);
        }
        return null;
    }

    /**
     * redis 乐观锁 ，监控key
     * @param key
     * @return
     */
    public String watch(String key){
        try(Jedis jedis = getJedis()){
            return jedis.watch(key);
        }catch (Exception e){
            log.error("exception",e);
        }
        return null;
    }

    /**
     * 将接下来的写操作放入事物队列
     * @return
     */
    public Transaction  multi(){
        try(Jedis jedis = getJedis()){
            return jedis.multi();
        }catch (Exception e){
            log.error("exception",e);
        }
        return null;
    }


}