package com.baidu.chinajoy;

/**
 * Created by yangmengrong on 14-7-11.
 */
public class Config {
    private String ip;
    private int port;
    private String number;

    public Config(String ip, int port, String number) {
        this.ip = ip;
        this.port = port;
        this.number = number;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Config{" +
                "ip='" + ip + '\'' +
                ", port=" + port +
                ", number=" + number +
                '}';
    }
}
