package com.lostleon.qdrealestate.crawler;

public class ParseHelper {
    public static String trim(String orig) {
        return orig.trim();
    }

    public static String trimComma(String orig) {
        return orig.replaceAll(",", "").trim();
    }

    /**
     * Example: change 21,843 ㎡ to 21843
     * 
     * @param orig
     * @return
     */
    public static String trimCommaAndReserveFirst(String orig) {
        return orig.replace(",", "").trim().split(" ")[0];
    }

    public static String getQueryValue(String allQuery, String queryKey) {
        String[] params = allQuery.split("&");
        for (String p : params) {
            String key = p.split("=")[0];
            if (key.equals(queryKey)) {
                return p.split("=")[1];
            }
        }
        return null;
    }
}
