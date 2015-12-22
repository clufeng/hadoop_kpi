package com.hadoop.kpi.model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

/**
 * Created by hubo on 2015/12/22
 */
public class KPI {

    private String remote_addr;// 记录客户端的ip地址
    private String remote_user;// 记录客户端用户名称,忽略属性"-"
    private String time_local;// 记录访问时间与时区
    private String request;// 记录请求的url与http协议
    private String status;// 记录请求状态；成功是200
    private String body_bytes_sent;// 记录发送给客户端文件主体内容大小
    private String http_referer;// 用来记录从那个页面链接访问过来的
    private String http_user_agent;// 记录客户浏览器的相关信息

    private boolean valid = true;// 判断数据是否合法

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("valid:" + this.valid);
        sb.append("\nremote_addr:" + this.remote_addr);
        sb.append("\nremote_user:" + this.remote_user);
        sb.append("\ntime_local:" + this.time_local);
        sb.append("\nrequest:" + this.request);
        sb.append("\nstatus:" + this.status);
        sb.append("\nbody_bytes_sent:" + this.body_bytes_sent);
        sb.append("\nhttp_referer:" + this.http_referer);
        sb.append("\nhttp_user_agent:" + this.http_user_agent);
        return sb.toString();
    }

    public String getRemote_addr() {
        return remote_addr;
    }

    public void setRemote_addr(String remote_addr) {
        this.remote_addr = remote_addr;
    }

    public String getRemote_user() {
        return remote_user;
    }

    public void setRemote_user(String remote_user) {
        this.remote_user = remote_user;
    }

    public String getTime_local() {
        return time_local;
    }

    public Date getTime_local_Date() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd/MMM/yyyy:HH:mm:ss", Locale.US);
        return df.parse(this.time_local);
    }

    public String getTime_local_Date_hour() throws ParseException{
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHH");
        return df.format(this.getTime_local_Date());
    }

    public void setTime_local(String time_local) {
        this.time_local = time_local;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        this.request = request;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBody_bytes_sent() {
        return body_bytes_sent;
    }

    public void setBody_bytes_sent(String body_bytes_sent) {
        this.body_bytes_sent = body_bytes_sent;
    }

    public String getHttp_referer() {
        return http_referer;
    }

    public String getHttp_referer_domain(){
        if(http_referer.length()<8){
            return http_referer;
        }

        String str=this.http_referer.replace("\"", "").replace("http://", "").replace("https://", "");
        return str.indexOf("/")>0?str.substring(0, str.indexOf("/")):str;
    }

    public void setHttp_referer(String http_referer) {
        this.http_referer = http_referer;
    }

    public String getHttp_user_agent() {
        return http_user_agent;
    }

    public void setHttp_user_agent(String http_user_agent) {
        this.http_user_agent = http_user_agent;
    }

    public boolean isValid() {
        return valid;
    }

    public void setValid(boolean valid) {
        this.valid = valid;
    }

    private static KPI parser(String line) {
        System.out.println(line);
        KPI kpi = new KPI();
        String[] arr = line.split(" ");
        if (arr.length > 11) {
            kpi.setRemote_addr(arr[0]);
            kpi.setRemote_user(arr[1]);
            kpi.setTime_local(arr[3].substring(1));
            kpi.setRequest(arr[6]);
            kpi.setStatus(arr[8]);
            kpi.setBody_bytes_sent(arr[9]);
            kpi.setHttp_referer(arr[10]);

            if (arr.length > 12) {
                kpi.setHttp_user_agent(arr[11] + " " + arr[12]);
            } else {
                kpi.setHttp_user_agent(arr[11]);
            }
            try {
                if (Integer.parseInt(kpi.getStatus()) >= 400) {// 大于400，HTTP错误
                    kpi.setValid(false);
                }
            }catch (Exception e) {
                kpi.setValid(false);
            }
        } else {
            kpi.setValid(false);
        }
        return kpi;
    }

    public static KPI filterPVs(String line) {
        KPI kpi = parser(line);
        Set<String> pages = new HashSet<>();
        pages.add("/about");
        pages.add("/black-ip-list/");
        pages.add("/cassandra-clustor/");
        pages.add("/finance-rhive-repurchase/");
        pages.add("/hadoop-family-roadmap/");
        pages.add("/hadoop-hive-intro/");
        pages.add("/hadoop-zookeeper-intro/");
        pages.add("/hadoop-mahout-roadmap/");

        if (!pages.contains(kpi.getRequest())) {
            kpi.setValid(false);
        }
        return kpi;
    }

    public static void main(String args[]) {
        String line = "60.206.230.74 - - [04/Jul/2015:10:22:29 +0800] \"GET / HTTP/1.1\" 304 0 \"-\" \"Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.153 Safari/537.36 SE 2.X MetaSr 1.0\"\n";
        System.out.println(line);
        KPI kpi = new KPI();
        String[] arr = line.split(" ");

        kpi.setRemote_addr(arr[0]);
        kpi.setRemote_user(arr[1]);
        kpi.setTime_local(arr[3].substring(1));
        kpi.setRequest(arr[6]);
        kpi.setStatus(arr[8]);
        kpi.setBody_bytes_sent(arr[9]);
        kpi.setHttp_referer(arr[10]);
        kpi.setHttp_user_agent(arr[11] + " " + arr[12]);
        System.out.println(kpi);

        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd:HH:mm:ss", Locale.US);
            System.out.println(df.format(kpi.getTime_local_Date()));
            System.out.println(kpi.getTime_local_Date_hour());
            System.out.println(kpi.getHttp_referer_domain());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
