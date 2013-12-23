package com.lostleon.qdrealestate;

import com.lostleon.qdrealestate.crawler.CrawlException;
import com.lostleon.qdrealestate.crawler.Crawler;

public class Application {

    public static void main(String[] args) {
        Crawler c = new Crawler();
        c.init();
        try {
            // 获取最新的Project列表
            c.crawlList();
            // 根据Project列表，逐一爬取Project
            c.diffList();
        } catch (CrawlException e) {
            e.printStackTrace();
        }
        c.close();
    }

}
