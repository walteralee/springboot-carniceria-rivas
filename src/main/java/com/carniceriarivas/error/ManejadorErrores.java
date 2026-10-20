package com.carniceriarivas.error;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * Convierte los errores en respuestas JSON { "message": "..." } con su código HTTP.
 * Así los servicios solo lanzan excepciones y los controladores no repiten try/catch.
 */
@RestControllerAdvice
public class ManejadorErrores {

    public record ErrorRespuesta(String message) {
    }

    @ExceptionHandler(RecursoNoEncontrado.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorRespuesta noEncontrado(RecursoNoEncontrado e) {
        return new ErrorRespuesta(e.getMessage());
    }

    @ExceptionHandler(PeticionNoValida.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta peticionNoValida(PeticionNoValida e) {
        return new ErrorRespuesta(e.getMessage());
    }

    // Falla una validación de @Valid (carrito vacío, cantidad fuera de rango...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta validacion(MethodArgumentNotValidException e) {
        String mensaje = e.getBindingResult().getAllErrors().stream()
                .map(error -> error.getDefaultMessage())
                .findFirst()
                .orElse("La petición no es válida.");
        return new ErrorRespuesta(mensaje);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta validacionDeParametros(HandlerMethodValidationException e) {
        return new ErrorRespuesta("La petición no es válida.");
    }

    // JSON mal escrito o con tipos incorrectos
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta cuerpoIlegible(HttpMessageNotReadableException e) {
        return new ErrorRespuesta("El cuerpo de la petición no es un JSON válido.");
    }

    // Parámetro con un valor no permitido, por ejemplo ?categoria=pescado
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorRespuesta parametroNoValido(MethodArgumentTypeMismatchException e) {
        return new ErrorRespuesta("Valor no válido para '" + e.getName() + "'.");
    }
}
