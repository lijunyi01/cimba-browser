package com.allcom.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy on 2017/11/7.
 * ok
 */
public class BWInfoResp {

    private int bwFlag;
    private List<String> whiteList;
    private List<String> blackList;

    public BWInfoResp() {
        this.bwFlag = 0;
        this.whiteList = new ArrayList<>();
        this.blackList = new ArrayList<>();
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    public List<String> getBlackList() {
        return blackList;
    }

    public void setBlackList(List<String> blackList) {
        this.blackList = blackList;
    }

    public int getBwFlag() {
        return bwFlag;
    }

    public void setBwFlag(int bwFlag) {
        this.bwFlag = bwFlag;
    }
}
