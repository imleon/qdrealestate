package com.lostleon.qdrealestate.bean;

import java.util.Date;

import com.lostleon.qdrealestate.util.Util;

/**
 * 每天每个Project的数据都记录成一个本类的对象
 * 
 * @author lostleon@gmail.com
 * 
 */
public class DailyMetrics {

    /* 日期 */
    private Date date;

    /* 截止到当天24:00的总成交面积 */
    private float totalSoldSize;

    /* 截止到当天24:00的总成交套数 */
    private int totalSoldNum;

    /* 截止到当天24:00的均价 */
    private float totalAvgPrice;

    /* 当天的成交面积 */
    private float todaySoldSize;

    /* 当天的成交套数 */
    private int todaySoldNum;

    /* 当天的成交均价 */
    private float todayAvgPrice;

    public DailyMetrics(Date d, float totalSoldSize, int totalSoldNum,
            float totalAvgPrice, float todaySoldSize, int todaySoldNum,
            float todayAvgPrice) {
        this.date = d;

        this.totalSoldSize = totalSoldSize;
        this.totalSoldNum = totalSoldNum;
        this.totalAvgPrice = totalAvgPrice;

        this.todaySoldSize = todaySoldSize;
        this.todaySoldNum = todaySoldNum;
        this.todayAvgPrice = todayAvgPrice;
    }

    public float getTotalAvgPrice() {
        return this.totalAvgPrice;
    }
    
    public Date getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(Util.date2str(date)).append("|").append(totalSoldSize)
                .append("|").append(totalSoldNum).append("|")
                .append(totalAvgPrice).append("|").append(totalSoldSize)
                .append("|").append(totalSoldNum).append("|")
                .append(todayAvgPrice);
        return sb.toString();
    }
}
