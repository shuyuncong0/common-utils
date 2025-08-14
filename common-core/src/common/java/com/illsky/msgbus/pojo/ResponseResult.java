package com.illsky.msgbus.pojo;

import java.io.Serializable;

/**
 * @author: succongccong
 * @date: 2024-05-10
 * @description: TODO
 * @modiFy:
 */
public class ResponseResult<T> implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean success = true;
    private String message = "";
    private int statusCode = 0;
    private String errorCode = "";
    private T result;
    private long timestamp = System.currentTimeMillis();

    public ResponseResult() {
    }

    public static ResponseResult<Object> error(String msg) {
        return error(500, msg);
    }

    public static ResponseResult<Object> error(int statusCode, String msg) {
        ResponseResult<Object> result = new ResponseResult();
        result.setStatusCode(statusCode);
        result.setErrorCode("error");
        result.setMessage(msg);
        result.setSuccess(false);
        return result;
    }

    public static ResponseResult<Object> error(int statusCode, String errorCode, String msg) {
        ResponseResult<Object> result = new ResponseResult();
        result.setStatusCode(statusCode);
        result.setErrorCode(errorCode);
        result.setMessage(msg);
        result.setSuccess(false);
        return result;
    }





    public static ResponseResult<Object> ok(String msg) {
        ResponseResult<Object> result = new ResponseResult();
        result.setSuccess(true);
        result.setStatusCode(200);
        result.setErrorCode("success");
        result.setMessage(msg);
        return result;
    }

    public static ResponseResult<Object> ok(Object data) {
        ResponseResult<Object> result = new ResponseResult();
        result.setSuccess(true);
        result.setStatusCode(200);
        result.setErrorCode("success");
        result.setMessage("操作成功！");
        result.setResult(data);
        return result;
    }

    public static ResponseResult<Object> ok(String msg, Object data) {
        ResponseResult<Object> result = new ResponseResult();
        result.setSuccess(true);
        result.setStatusCode(200);
        result.setMessage(msg);
        result.setResult(data);
        return result;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getMessage() {
        return this.message;
    }

    public int getStatusCode() {
        return this.statusCode;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public T getResult() {
        return this.result;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public void setResult(T result) {
        this.result = result;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof ResponseResult)) {
            return false;
        } else {
            ResponseResult<?> other = (ResponseResult)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isSuccess() != other.isSuccess()) {
                return false;
            } else {
                label57: {
                    Object this$message = this.getMessage();
                    Object other$message = other.getMessage();
                    if (this$message == null) {
                        if (other$message == null) {
                            break label57;
                        }
                    } else if (this$message.equals(other$message)) {
                        break label57;
                    }

                    return false;
                }

                if (this.getStatusCode() != other.getStatusCode()) {
                    return false;
                } else {
                    Object this$errorCode = this.getErrorCode();
                    Object other$errorCode = other.getErrorCode();
                    if (this$errorCode == null) {
                        if (other$errorCode != null) {
                            return false;
                        }
                    } else if (!this$errorCode.equals(other$errorCode)) {
                        return false;
                    }

                    label42: {
                        Object this$result = this.getResult();
                        Object other$result = other.getResult();
                        if (this$result == null) {
                            if (other$result == null) {
                                break label42;
                            }
                        } else if (this$result.equals(other$result)) {
                            break label42;
                        }

                        return false;
                    }

                    if (this.getTimestamp() != other.getTimestamp()) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        }
    }

    protected boolean canEqual(Object other) {
        return other instanceof ResponseResult;
    }

    public int hashCode() {
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        Object $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        result = result * 59 + this.getStatusCode();
        Object $errorCode = this.getErrorCode();
        result = result * 59 + ($errorCode == null ? 43 : $errorCode.hashCode());
        Object $result = this.getResult();
        result = result * 59 + ($result == null ? 43 : $result.hashCode());
        long $timestamp = this.getTimestamp();
        result = result * 59 + Long.hashCode($timestamp);
        return result;
    }

    public String toString() {
        return "ResponseResult(success=" + this.isSuccess() + ", message=" + this.getMessage() + ", statusCode=" + this.getStatusCode() + ", errorCode=" + this.getErrorCode() + ", result=" + this.getResult() + ", timestamp=" + this.getTimestamp() + ")";
    }
}
