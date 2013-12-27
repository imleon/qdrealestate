package com.lostleon.qdrealestate.render;

import java.util.List;
import java.util.Set;

import redis.clients.jedis.Jedis;

import com.lostleon.qdrealestate.bean.DailyMetrics;
import com.lostleon.qdrealestate.bean.ProjectBean;
import com.lostleon.qdrealestate.bean.UnitBean;
import com.lostleon.qdrealestate.storage.JedisHelper;

public class RenderKML {
    
    private static final String KML_PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><kml xmlns=\"http://earth.google.com/kml/2.0\"><Document><name>Qingdao</name>";
    private static final String KML_POSTFIX = "</Document></kml>";
    public static void main(String[] args) {
        JedisHelper.init();
        Jedis j = JedisHelper.getJedis();
            
        
        /*
         * 
<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://earth.google.com/kml/2.0">
  <Document>
    <name>Yesterday's Observed</name>
    <Placemark>
      <Point>
        <coordinates>-123.8317,46.9725,100.0000</coordinates>
      </Point>
    </Placemark>
    <Placemark>
      <Point>
        <coordinates>-68.2609,44.3771,100.0000</coordinates>
      </Point>
    </Placemark>
    <Placemark>    
      <Point>
        <coordinates>-73.1686,42.6367,100.0000</coordinates>
      </Point>
    </Placemark>
  </Document>
</kml>
         * 
         * 
         * var point = new BMap.Point(sw.lng + lngSpan * (Math.random() * 0.7), ne.lat - latSpan * (Math.random() * 0.7));
         * 
         */
        
        
        Set<String> projects = j.keys("PROJECT:*");
        for (String p : projects) {
            ProjectBean pb = JedisHelper.getProject(Integer.valueOf(p.substring(8)));
            System.out.println("var point = new BMap.Point(" + pb.getLng() + ", " + pb.getLat() + ");addMarker(point);");
//            System.out.println("<Placemark><Point><coordinates>" + pb.getLng() + "," + pb.getLat() + ",100.0000</coordinates></Point></Placemark>");
        }
        
//        
//        Set<String> metrics = j.keys("METRICS:*");
//        for (String m : metrics) {
//            DailyMetrics dm = JedisHelper.getLatestDailyMetrics(Integer.valueOf(m.substring(8)));
//            System.out.println(m + "|" + dm.toString());
//        }
        
      
        
        JedisHelper.close();
    }

}
