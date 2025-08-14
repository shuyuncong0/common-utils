package com.illsky.retry.exception;

import java.util.Calendar;
import java.util.Date;

/**
 * @author: succongccong
 * @date: 2023-05-17
 * @description: 自定义补偿异常
 * @modiFy:
 */
public class RetryException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private String message;

    private String res;

    private Date retryexpirydate;

    public RetryException() {
        super();
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0); //这是将当天的【秒】设置为0
        calendar.set(Calendar.MINUTE,0); //这是将当天的【分】设置为0
        calendar.add(Calendar.DATE,1);
        this.retryexpirydate = calendar.getTime();
    }

    public RetryException(String message, Throwable cause) {
        super(message, cause);
    }

    public RetryException(String message) {
        super(message);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND,0); //这是将当天的【秒】设置为0
        calendar.set(Calendar.MINUTE,0); //这是将当天的【分】设置为0
        calendar.add(Calendar.DATE,1);
        this.retryexpirydate = calendar.getTime();
        this.message = message;
    }

    public RetryException(String message, Date retryexpirydate) {
        super(message);
        this.retryexpirydate = retryexpirydate;
        this.message = message;
    }

    public RetryException(String message, Date retryexpirydate, String res) {
        super(message);
        this.retryexpirydate = retryexpirydate;
        this.message = message;
        this.res = res;
    }

    public RetryException(Date retryexpirydate) {
        super();
        this.retryexpirydate = retryexpirydate;
    }

    public RetryException(Throwable cause) {
        super(cause);
    }

    public Date getRetryexpirydate() {
        return retryexpirydate;
    }

    public String getRes() {
        return res;
    }


}
