package com.example.solak.api.exception;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

//ResponseEntityExceptionHandler captura exceções
@ControllerAdvice  // observa a app
public class FinanceiroExceptionHandler extends ResponseEntityExceptionHandler{
	
	@Autowired
	private MessageSource messageSource;
	
	@Override
	protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		
		String messageForUser = messageSource.getMessage("message.invalid", null, LocaleContextHolder.getLocale());
		String messageForDevelopment = ex.getCause().toString();
		List<Error> errors = Arrays.asList(new Error(messageForDevelopment, messageForUser));
		
		return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<Error> errors = createListError(ex.getBindingResult());
		
		return handleExceptionInternal(ex, errors, headers, HttpStatus.BAD_REQUEST, request);
	}
	
	
	@ExceptionHandler({ EmptyResultDataAccessException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ResponseEntity<Object> handleEmptyResultDataAcessException(EmptyResultDataAccessException ex, WebRequest request){
		String messageForUser = messageSource.getMessage("recurso.nao-encontrado", null, LocaleContextHolder.getLocale());
		String messageForDevelopment = ex.toString();
		List<Error> errors = Arrays.asList(new Error(messageForDevelopment, messageForUser));
		return handleExceptionInternal(ex, errors, new HttpHeaders(), HttpStatus.NOT_FOUND, request);
		
	}
	
	private List<Error> createListError(BindingResult bindingResult){
		List<Error> errors = new ArrayList<>();
		
		for(FieldError fieldError : bindingResult.getFieldErrors()){
			
			String messageForUser = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
			String messageForDevelopment = fieldError.toString();
		    errors.add(new Error(messageForUser, messageForDevelopment));
		
		}
		return errors;
	}
	
	public static class Error{
		private String messageForUser;
		private String messageForDevelopment;
		
		public Error(String messageForUser, String messageForDevelopment) {
			this.messageForUser = messageForUser;
			this.messageForDevelopment = messageForDevelopment;
		}
		
		
		public String getMessageForDevelopment() {
			return messageForDevelopment;
		}
		
		public String getMessageForUser() {
			return messageForUser;
		}
	
	}
}