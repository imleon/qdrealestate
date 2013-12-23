package com.lostleon.qdrealestate.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connection {

    private final static Logger logger = LoggerFactory
            .getLogger(Connection.class);

    private final static String PROTOCOL = "http";

    private final static int PORT = 80;

    private final static String ENCODEING = "GBK";

    private final static String USER_AGENT = "Mozilla/5.0 (X11; U; Linux x86_64; en-US; rv:1.9.2.13) Gecko/20101206 Ubuntu/10.10 (maverick) Firefox/3.6.13";

    private final static String REFERER = "http://http://www.qdfd.com.cn";

    public static HttpClient httpclient;

    public static void init() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme(PROTOCOL, PORT, PlainSocketFactory
                .getSocketFactory()));
        ClientConnectionManager cm = new PoolingClientConnectionManager(
                schemeRegistry);
        httpclient = new DefaultHttpClient(cm);
    }

    public static String doGet(String url) {
        StringBuilder ret = new StringBuilder();
        HttpGet hg = new HttpGet(url);
        hg.setHeader("Referer", REFERER);

        HttpContext HTTP_CONTEXT = new BasicHttpContext();
        HTTP_CONTEXT.setAttribute(CoreProtocolPNames.USER_AGENT, USER_AGENT);
        httpclient.getParams().setParameter(ClientPNames.COOKIE_POLICY,
                CookiePolicy.BROWSER_COMPATIBILITY);

        String line = "";
        try {
            HttpResponse res = httpclient.execute(hg, HTTP_CONTEXT);
            HttpEntity ent = res.getEntity();
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    ent.getContent(), ENCODEING));
            while ((line = br.readLine()) != null) {
                ret.append(line);
            }
            EntityUtils.consume(ent);
        } catch (ClientProtocolException e) {
            logger.error("HttpGet Exception", e);
        } catch (IllegalStateException e) {
            logger.error("HttpGet Exception", e);
        } catch (IOException e) {
            logger.error("HttpGet Exception", e);
        }
        return ret.toString();
    }

    public static void close() {
        httpclient.getConnectionManager().shutdown();
    }

}
