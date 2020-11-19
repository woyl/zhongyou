package com.zhongyou.meet.mobile.entities;

/**
 * @author Dongce
 * create time: 2018/10/23
 */
public class LoginWithPhoneNumber {

    private boolean authPass;
    private User user;

    public boolean getAuthPass() {
        return authPass;
    }

    public void setAuthPass(boolean authPass) {
        this.authPass = authPass;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
