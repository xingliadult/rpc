package cn.ggstd.discovery;


import cn.ggstd.common.model.service.RpcService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by lixing on 2021-2-26 下午 4:01.
 */
public class ServerDiscoveryCache {

    private static final ConcurrentMap<String,List<RpcService>> SERVICE_CACHE = new ConcurrentHashMap<>();
    public static final List<String> SERVICE_CLASS_CACHE = new ArrayList<>();

    public static void addServiceClass(String serviceName){
        SERVICE_CLASS_CACHE.add(serviceName);
    }

    public static void removeService(String serviceName){
        SERVICE_CACHE.remove(serviceName);
    }

    public static void removeServiceByAddress(String address){
        for(List<RpcService> serviceList : SERVICE_CACHE.values()){
            for (RpcService service : serviceList) {
                if(address.equals(service.getAddress())){
                    serviceList.remove(service);
                    return;//不写return的话会报ConcurrentModificationException: null
                }
            }
        }
    }

    public static List<RpcService> putService(String serviceName,List<RpcService> serviceList){
        return SERVICE_CACHE.putIfAbsent(serviceName,serviceList);
    }

    public static List<RpcService> getService(String serviceName){
        return new ArrayList<>(SERVICE_CACHE.getOrDefault(serviceName,new ArrayList<>()));
    }

    public static void clear() {
        SERVICE_CACHE.clear();
    }

    public static boolean isEmpty() {
        return SERVICE_CACHE.isEmpty();
    }

}
