package com.dao;

/**
 * description：dao层异常的包装
 *
 * @author ajie
 * data 2018/11/29 20:35
 */
public class DaoException extends RuntimeException {

    private static final long serialVersionUID = 6293119645368995803L;

    DaoException() {
        super();
    }

    DaoException(String message) {
        super(message);
    }

    public DaoException(String message, Throwable cause) {
        super(message, cause);
    }

    DaoException(Throwable cause) {
        super(cause);
    }

    DaoException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
