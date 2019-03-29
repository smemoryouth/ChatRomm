package com.service.serviceimpl;

import com.bean.User;
import com.dao.UserDao;
import com.dao.DaoFactory;
import com.service.UserService;
import com.util.EmailUtils;

/**
 * description：service层的具体操作
 *
 * @author ajie
 * data 2018/11/29 18:19
 */
public class UserServiceImpl implements UserService {

    private UserDao dao = DaoFactory.getDaoFactory().getUserDao();

    /**
     * 登录
     * @param name
     * @param psw
     * @return
     */
    @Override
    public boolean loginVerification(String name, String psw) {
        User user = dao.getUser(name);
        if (user == null){
            return false;
        }
        return user.getPsw().equals(psw);
    }

    /**
     * 注册
     * @param name
     * @param psw
     * @param email
     * @return
     */
    @Override
    public boolean registerVerification(String name, String psw, String email) {
        User user = dao.getUser(name);
        if (user == null){
            user = new User(name, psw, email);
            return dao.insertUser(user) == 1;
        }
        return false;
    }

    /**
     * 忘记密码
     * @param name
     * @param email
     * @return
     */
    @Override
    public boolean retrievePassword(String name, String email) {
        User user = dao.getUser(name);
        if (user != null){
            String toEmail = user.getEmail();
            if (toEmail.equals(email)){
                String psw = user.getPsw();
                String msg = "您的密码是：\n" +
                        "===========\n" +
                        psw + "\n" +
                        "===========\n" +
                        "请妥善保管您的密码，勿将密码泄露给他人，可在登录系统后修改密码。\n" +
                        "如有疑问请回复 smemoryouth@yeah.net  感谢您的使用";
                return EmailUtils.sentEmail(1, email, msg);
            }
        }
        return false;
    }

    /**
     * 修改密码
     * @param name
     * @param oldPsw
     * @param newPsw
     * @param email
     * @return
     */
    @Override
    public boolean modifyPswVerification(String name, String oldPsw, String newPsw, String email) {
        User user = dao.getUser(name);
        if (user.getPsw().equals(oldPsw) && user.getEmail().equals(email)){
            return dao.updateUser(name, newPsw, null) == 1;
        }
        return false;
    }

    /**
     * 更换邮箱
     * @param name
     * @param psw
     * @param email
     * @param newEmail
     * @return
     */
    @Override
    public boolean modifyEmailVerification(String name, String psw, String email, String newEmail) {
        User user = dao.getUser(name);
        if (user.getPsw().equals(psw) && user.getEmail().equals(email)){
            return dao.updateUser(name, null, newEmail) == 1;
        }
        return false;
    }

    /**
     * 注销账户
     * @param name
     * @param psw
     * @return
     */
    @Override
    public boolean logoutVerification(String name, String psw, String email) {
        User user = dao.getUser(name);
        if (user.getPsw().equals(psw) && user.getEmail().equals(email)){
            String msg = "您的账户已注销，感谢您的使用。\n" +
                    "如果您有任何建议或疑问，请联系 smemoryouth@yeah.net。\n" +
                    "期待与您再次相遇，再见。";
            EmailUtils.sentEmail(2, email, msg);
            return dao.deleteUser(name) == 1;
        }
        return false;
    }

    @Override
    public User getUserInfo(String name) {
        return dao.getUser(name);
    }

    @Override
    public boolean isExist(String name) {
        return dao.getUser(name) != null;
    }
}
