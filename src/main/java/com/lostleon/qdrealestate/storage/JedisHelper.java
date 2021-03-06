package com.lostleon.qdrealestate.storage;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lostleon.qdrealestate.bean.DailyMetrics;
import com.lostleon.qdrealestate.bean.District;
import com.lostleon.qdrealestate.bean.ProjectBean;
import com.lostleon.qdrealestate.bean.ProjectStatus;
import com.lostleon.qdrealestate.bean.UnitBean;
import com.lostleon.qdrealestate.util.Util;

import redis.clients.jedis.Jedis;

/**
 * 
 * 存储结构：
 * 
 * 1. hash        用来存储ProjectBean
 * 
 *    key         "PROJECT:1428"
 *    field-value "status" "xxx" 
 *    field-value "xxx"    "xxx"
 *    field-value "xxx"    "xxx"
 *    field-value "xxx"    "xxx"
 *    field-value "xxx"    "xxx"
 *    field-value "xxx"    "xxx"
 *    
 * 2. list        用来存储DailyMetrics
 *    
 *    key         "METRICS:1428"
 *    element1    "date|totalSoldSize|totalSoldNum|totalAvgPrice|todaySoldSize|todaySoldNum|todayAvgPrice"
 *    element2    "date|totalSoldSize|totalSoldNum|totalAvgPrice|todaySoldSize|todaySoldNum|todayAvgPrice"
 *    element3    "date|totalSoldSize|totalSoldNum|totalAvgPrice|todaySoldSize|todaySoldNum|todayAvgPrice"
 *    
 * 3. list        用来存储Unit
 *    key         "UNIT:1428"
 *    element1    "unitId|totalNum|availNum|totalSize|availSize|isResident"
 *    element2    "unitId|totalNum|availNum|totalSize|availSize|isResident"
 *    element3    "unitId|totalNum|availNum|totalSize|availSize|isResident"
 *    
 * @author lostleon@gmail.com
 *
 */
public class JedisHelper {

    private final static Logger redislogger = LoggerFactory.getLogger("redis");

    private final static String REDIS_IP = "127.0.0.1";
    private final static int REDIS_PORT = 6379;

    private static Jedis jedis;

    public static void init() {
        jedis = new Jedis(REDIS_IP, REDIS_PORT);
        jedis.connect();
    }

    public static void close() {
        jedis.disconnect();
    }

    public static Jedis getJedis() {
        return jedis;
    }

    public static void updateProjectStatus(int id, ProjectStatus st) {
        jedis.hset("PROJECT:" + id, "status", st.toString());
        jedis.hset("PROJECT:" + id, "lastUpdate", Util.date2str(new Date()));
        redislogger.info("Project|HSET|updateStatus|" + id + "|" + st);
    }

    public static void updateProjectMetrics(int id, int residentNumAvail,
            float residentSizeAvail) {
        jedis.hset("PROJECT:" + id, "residentNumAvail",
                String.valueOf(residentNumAvail));
        jedis.hset("PROJECT:" + id, "residentSizeAvail",
                String.valueOf(residentSizeAvail));
        jedis.hset("PROJECT:" + id, "lastUpdate", Util.date2str(new Date()));
        redislogger.info("Project|HSET|updateMetrics|" + id + "|"
                + residentNumAvail + "|" + residentSizeAvail);
    }
    
    public static void updateProjectMetrics(int id, float residentSizeTotal,
            float residentSizeAvail) {
        jedis.hset("PROJECT:" + id, "residentSizeTotal",
                String.valueOf(residentSizeTotal));
        jedis.hset("PROJECT:" + id, "residentSizeAvail",
                String.valueOf(residentSizeAvail));
        jedis.hset("PROJECT:" + id, "lastUpdate", Util.date2str(new Date()));
        redislogger.info("Project|HSET|updateMetrics|" + id + "|"
                + residentSizeTotal + "|" + residentSizeAvail);
    }

    public static void createProject(ProjectBean p) {
        String key = "PROJECT:" + p.getProjectId();
        jedis.hset(key, "status", p.getProjectStatus().toString());
        jedis.hset(key, "name", p.getName());
        jedis.hset(key, "addr", p.getAddr());
        jedis.hset(key, "district", p.getDistrict().toString());
        jedis.hset(key, "company", p.getCompany());
        jedis.hset(key, "lat", String.valueOf(p.getLat()));
        jedis.hset(key, "lng", String.valueOf(p.getLng()));
        jedis.hset(key, "residentNumTotal",
                String.valueOf(p.getResidentNumTotal()));
        jedis.hset(key, "residentNumAvail",
                String.valueOf(p.getResidentNumAvail()));
        jedis.hset(key, "residentSizeTotal",
                String.valueOf(p.getResidentSizeTotal()));
        jedis.hset(key, "residentSizeAvail",
                String.valueOf(p.getResidentSizeAvail()));
        jedis.hset(key, "lastUpdate", Util.date2str(new Date()));
        redislogger.info("Project|HSET|create|" + p.getProjectId() + "|"
                + p.getProjectStatus().toString() + "|" + p.getName());
    }

    public static ProjectBean getProject(int id) {
        Map<String, String> p = jedis.hgetAll("PROJECT:" + id);
        if (p == null || p.size() == 0) {
            return null;
        }
        ProjectStatus st = ProjectStatus.getStatusByENG(p.get("status"));
        String name = p.get("name");
        District district = District.getDistrictByENG(p.get("district"));
        String addr = p.get("addr");
        String company = p.get("company");
        double lng = Double.parseDouble(p.get("lng"));
        double lat = Double.parseDouble(p.get("lat"));
        int residentNumTotal = Integer.parseInt(p.get("residentNumTotal"));
        int residentNumAvail = Integer.parseInt(p.get("residentNumAvail"));
        float residentSizeTotal = Float.parseFloat(p.get("residentSizeTotal"));
        float residentSizeAvail = Float.parseFloat(p.get("residentSizeAvail"));

        ProjectBean bean = new ProjectBean(id, st, name, district, addr,
                company, lng, lat, residentNumTotal, residentNumAvail,
                residentSizeTotal, residentSizeAvail);
        return bean;
    }

    public static void addDailyMetrics(int id, DailyMetrics dms) {
        String val = dms.toString();
        jedis.rpush("METRICS:" + id, val);
        jedis.hset("PROJECT:" + id, "lastUpdate", Util.date2str(new Date()));
        redislogger.info("Metrics|RPUSH|" + id + "|" + val);
    }
    
    public static void addUnits(int id, List<UnitBean> ubs) {
        for (UnitBean ub : ubs) {
            String val = ub.toString();
            jedis.rpush("UNIT:" + id, val);
            redislogger.info("Unit|RPUSH|" + id + "|" + val);
        }
    }

    public static DailyMetrics getLatestDailyMetrics(int prjId) {
        String m = jedis.lindex("METRICS:" + prjId, -1);
        if (m == null) {
            return null;
        }
        String[] elms = m.split("\\|");
        if (elms.length != 7) { // 1个日期+3个总的+3个当天的
            return null;
        }

        Date d = Util.str2dateComplex(elms[0]);

        float totalSoldSize = Float.parseFloat(elms[1]);
        int totalSoldNum = Integer.parseInt(elms[2]);
        float totalAvgPrice = Float.parseFloat(elms[3]);

        float todaySoldSize = Float.parseFloat(elms[4]);
        int todaySoldNum = Integer.parseInt(elms[5]);
        float todayAvgPrice = Float.parseFloat(elms[6]);

        DailyMetrics dms = new DailyMetrics(d, totalSoldSize, totalSoldNum,
                totalAvgPrice, todaySoldSize, todaySoldNum, todayAvgPrice);
        return dms;
    }
    
    
    public static List<UnitBean> getResidentUnitBeans(int prjId) {
        List<UnitBean> ret = new ArrayList<UnitBean>();
        
        List<String> ubs = jedis.lrange("UNIT:" + prjId, 0, -1);
        if (ubs == null || ubs.size() == 0) {
            return null;
        }
        for (String ub : ubs) {
            String[] elms = ub.split("\\|");
            if (elms.length != 7) { // 1个id+4个数据+2个bool
                return null;
            }
            boolean isResident = Boolean.parseBoolean(elms[5]);
            if (isResident) {
                long ubId = Long.parseLong(elms[0]);
                int totalNum = Integer.parseInt(elms[1]);
                int availNum = Integer.parseInt(elms[2]);
                float totalSize = Float.parseFloat(elms[3]);
                float availSize = Float.parseFloat(elms[4]);
                boolean isFake = Boolean.parseBoolean(elms[6]);
                UnitBean ubObj = new UnitBean(ubId, totalNum, availNum,
                        totalSize, availSize, isResident, isFake);
                ret.add(ubObj);
            }
        }
        return ret;
    }
    
}
