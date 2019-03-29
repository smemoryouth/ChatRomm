package com.bean;

/**
 * description：
 *
 * @author ajie
 * data 2018/11/29 18:15
 */
public class User {
    private String name;
    private String psw;
    private String email;
    @Override
    public String toString() {
        return "User[" +
                "name='" + name + '\'' +
                ", psw='" + psw + '\'' +
                ", email='" + email + '\'' +
                ']';
    }

    public String getName() {
        return name;
    }

    public String getPsw() {
        return psw;
    }

    public String getEmail() {
        return email;
    }

    public User(String name, String psw, String email) {
        this.name = name;
        this.psw = psw;
        this.email = email;
    }
}
