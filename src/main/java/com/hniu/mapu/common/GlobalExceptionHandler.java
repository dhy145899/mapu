package com.hniu.mapu.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * @author jiujiu
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
	
	/**
	 * 处理运行时异常
	 * @param e 异常对象
	 * @return 统一响应结果
	 */
	@ExceptionHandler(RuntimeException.class)
	public Result<String> handleRuntimeException(RuntimeException e) {
		log.error("运行时异常：", e);
		return Result.error(e.getMessage());
	}
	
	/**
	 * 处理所有异常
	 * @param e 异常对象
	 * @return 统一响应结果
	 */
	@ExceptionHandler(Exception.class)
	public Result<String> handleException(Exception e) {
		log.error("系统异常：", e);
		return Result.error("系统异常，请联系管理员");
	}
}
