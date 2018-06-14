/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package data;

/**
 *
 * @author emil
 */
public class RawDatapoint {
    
    private String wss; //µs
    private String rpm; //µs
    private String time; //µs

    
    public RawDatapoint(String wss, String rpm, String time) {
        this.wss = wss;
        this.rpm = rpm;
        this.time = time;
    }

    
    public String getWss() {
        return wss;
    }

    public String getRpm() {
        return rpm;
    }

    public String getTime() {
        return time;
    }

    
    public void setWss(String wss) {
        this.wss = wss;
    }

    public void setRpm(String rpm) {
        this.rpm = rpm;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
}
