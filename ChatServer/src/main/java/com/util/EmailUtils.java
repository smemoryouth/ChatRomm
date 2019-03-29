package com.util;

import org.apache.commons.mail.SimpleEmail;

/**
 * @author 阿劼
 */
public class EmailUtils {
    public static boolean sentEmail(int code, String toEmail, String msg) {
        try {
            SimpleEmail email = new SimpleEmail();
            email.setHostName("smtp.yeah.net");
            // 注意这里使用的是邮箱SMTP服务器的授权码而不是自己的邮箱登录密码
            email.setAuthentication("smemoryouth@yeah.net", "wl968640");
            email.setSSLOnConnect(true);
            email.setFrom("smemoryouth@yeah.net", "局域网聊天系统邮件");
            switch (code) {
                case 1:
                    email.setSubject("密码找回");
                    break;
                case 2:
                    email.setSubject("账户注销");
                    break;
                default:
                    email.setSubject("局域网聊天系统");
                    break;
            }
            email.setCharset("UTF-8");
            email.setMsg(msg);
            email.addTo(toEmail);
            email.send();
            System.out.println("邮件发送成功");
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}