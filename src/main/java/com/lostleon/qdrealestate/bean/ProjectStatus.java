package com.lostleon.qdrealestate.bean;

public enum ProjectStatus {
    PREPARE, // 即将开盘
    PRESELL, // 即将开盘,在售
    ONSELL,  // 在售
    SOLDOUT; // 售完

    public static ProjectStatus getStatusByCHN(String str) {
        if (str.equals("即将开盘")) {
            return PREPARE;
        } else if (str.equals("即将开盘在售")) {
            return PRESELL;
        } else if (str.equals("在售")) {
            return ONSELL;
        } else if (str.equals("售完")) {
            return SOLDOUT;
        }
        throw new IllegalArgumentException("No Status for string: " + str);
    }

    public static ProjectStatus getStatusByENG(String str) {
        if (str.equals("PREPARE")) {
            return PREPARE;
        } else if (str.equals("PRESELL")) {
            return PRESELL;
        } else if (str.equals("ONSELL")) {
            return ONSELL;
        } else if (str.equals("SOLDOUT")) {
            return SOLDOUT;
        }
        throw new IllegalArgumentException("No Status for string: " + str);
    }

}
