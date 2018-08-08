package cn.yanss.m.kitchen.cws.cache;

import cn.yanss.m.kitchen.cws.utils.MapperUtils;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Component
public class EhCacheServiceImpl {


    @Autowired
    private CacheManager cacheManager;

    private volatile String name = "cache";

    public void save(String key,Object value){
        Cache cache = cacheManager.getCache(name);
        Element element = new Element(key,value);
        cache.put(element);
    }

    /**
     * 注意:后期测试去除json转换
     * @param key
     * @return
     */
    public <T> T getValue(String key,Class<T> clz){
        Cache cache = cacheManager.getCache(name);
        Element element = cache.get(key);
        if(null == element){
            return null;
        }
        Object obj = element.getObjectValue();
        return MapperUtils.obj2pojo(obj,clz);
    }

    public Object getObj(String key){
        Cache cache = cacheManager.getCache(name);
        Element element = cache.get(key);
        if(null == element){
            return null;
        }
        return element.getObjectValue();

    }

    public void remove(String key){
        Cache cache = cacheManager.getCache(name);
        cache.remove(key);
    }

    public void update(String key,Object value){
        Cache cache = cacheManager.getCache(name);
        cache.replace(new Element(key,value));
    }

    public List<Object> getList(List<String> key){
        Cache cache = cacheManager.getCache(name);
        Map<Object,Element> map = cache.getAll(key);
        return map.values().stream().map(d -> d.getObjectValue()).collect(Collectors.toList());
    }

    public boolean exists(Object key){
        Cache cache = cacheManager.getCache(name);
        return cache.isKeyInCache(key);
    }
}
