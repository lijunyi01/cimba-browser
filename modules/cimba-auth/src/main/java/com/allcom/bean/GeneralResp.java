package com.allcom.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy on 2017/11/7.
 * ok
 */
public class GeneralResp {

    private int errorCode;
    private String errorMsg;
    private String retContent;

    public GeneralResp() {
        this.errorCode = -1;
        this.errorMsg = "failed";
        this.retContent = "";
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getRetContent() {
        return retContent;
    }

    public void setRetContent(String retContent) {
        this.retContent = retContent;
    }
}
