package com.pure.service.service.dto;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private String openid;
    private String session_key;

    public String getSession_key() {
        return session_key;
    }

    public void setSession_key(String session_key) {
        this.session_key = session_key;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
            "openid='" + openid + '\'' +
            ", session_key='" + session_key + '\'' +
            '}';
    }
}
