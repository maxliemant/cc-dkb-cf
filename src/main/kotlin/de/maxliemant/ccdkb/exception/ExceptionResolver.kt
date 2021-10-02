package de.maxliemant.ccdkb.exception;

import mu.KLogging
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler
import javax.persistence.EntityNotFoundException

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
class InternalExceptionResolver : ResponseEntityExceptionHandler() {

    companion object : KLogging()

    @ExceptionHandler(Throwable::class)
    fun handleAllException(ex: Exception, request: WebRequest): ResponseEntity<Any>? {
        //The default handleException method can handle most exceptions. It throws an error if it encounters exceptions it
        //doesn't recognize, including transaction exceptions.
        try {
            return handleException(ex, request)
        } catch (ex: Exception) {
            return handleExceptionInternal(ex, null, HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR, request);
        }
    }

    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(ex: BadRequestException, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message)
        return handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.BAD_REQUEST, request)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(ex: BadRequestException, request: WebRequest): ResponseEntity<Any> {
        logger.error(ex.message)
        return handleExceptionInternal(ex, ex.message, HttpHeaders(), HttpStatus.NOT_FOUND, request)
    }

}
