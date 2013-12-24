package com.lostleon.qdrealestate.selector;

import java.util.List;
import java.util.Set;

import com.lostleon.qdrealestate.bean.DailyMetrics;
import com.lostleon.qdrealestate.bean.ProjectBean;
import com.lostleon.qdrealestate.bean.UnitBean;
import com.lostleon.qdrealestate.storage.JedisHelper;

import redis.clients.jedis.Jedis;

public class Selector {

    public static void main(String[] args) {
        JedisHelper.init();
        Jedis j = JedisHelper.getJedis();
            
        Set<String> projects = j.keys("PROJECT:*");
        for (String p : projects) {
            ProjectBean pb = JedisHelper.getProject(Integer.valueOf(p.substring(8)));
            System.out.println(pb.toString());
        }
        
        System.out.println("\n\n");
        
        Set<String> metrics = j.keys("METRICS:*");
        for (String m : metrics) {
            DailyMetrics dm = JedisHelper.getLatestDailyMetrics(Integer.valueOf(m.substring(8)));
            System.out.println(m + "|" + dm.toString());
        }
        
        System.out.println("\n\n");
        
        Set<String> units = j.keys("UNIT:*");
        for (String m : units) {
            List<UnitBean> ubs = JedisHelper.getResidentUnitBeans(Integer.valueOf(m.substring(5)));
            for (UnitBean ub : ubs) {
                System.out.println(m + "|" + ub.toString());
            }
        }
        
        JedisHelper.close();
    }

}
