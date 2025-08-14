package com.illsky.basic.exception;

import cn.hutool.core.util.StrUtil;

import java.lang.reflect.InvocationTargetException;

/**
 * 提示给用户的错误信息
 *
 * @author sucongcong
 * @since 2024-05-17 09:10
 */
public class ExternalInterfaceException extends RuntimeException {


	/**
	 * 异常消息
	 */
	protected String message;
	/**
	 * Name: serialVersionUID Type: long
	 */
	private static final long serialVersionUID = 1L;

	public ExternalInterfaceException() {
		super();
	}

	public ExternalInterfaceException(String message, Throwable cause) {
		super(message, cause);
		this.message = message;
	}

	public ExternalInterfaceException(String message) {
		super(message);
		this.message = message;
	}


	public ExternalInterfaceException(Throwable cause) {
		super(cause);
		this.message = cause.getMessage();
		if (cause instanceof InvocationTargetException) {
			if (cause.getCause() != null && !StrUtil.isEmpty(cause.getCause().getMessage())) {
				this.message = cause.getCause().getMessage();
			}
		}
	}



	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public String getLocalizedMessage() {
		return getMessage();
	}

	@Override
	public String toString() {
		String s = getClass().getName();
		String message = getLocalizedMessage();
		return (message != null) ? (s + ": " + message) : s;
	}


}
