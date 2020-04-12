package com.indrasol.essbase.essbasesheetplugin;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.essbase.api.base.EssException;

import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.context.request.WebRequest;

/**
 * Handle all Exceptions in for All controllers
 */
@RestControllerAdvice
public class ExceptionHandlerController {

    @Bean
    public ErrorAttributes errorAttributes() {
        // Hide exception field in the return object
        return new DefaultErrorAttributes() {

            @Override
            public Map<String, Object> getErrorAttributes(WebRequest requestAttributes, boolean includeStackTrace) {
                Map<String, Object> errorAttributes = super.getErrorAttributes(requestAttributes, includeStackTrace);
                errorAttributes.remove("exception");
                return errorAttributes;
            }
        };
    }
    
    @ExceptionHandler(EssException.class) 
    public void handleEssException(Exception ex, HttpServletResponse res) throws IOException {
        res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(),ex.getMessage());
    }
    
    @ExceptionHandler(HttpServerErrorException.class)
    public void handleHttpServerErrorException(HttpServerErrorException ex, HttpServletResponse res) throws IOException {
        res.sendError(ex.getStatusCode().value(),ex.getMessage());
    }
    
    @ExceptionHandler(Exception.class)
    public void handleException(Exception ex, HttpServletResponse res) throws IOException {
        //LOGGER.error("Handled Internal Error Exception", ex);
        res.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage());
    }
    


}