package com.algaworks.algatransito.exceptionhandler;

import com.algaworks.algatransito.domain.exception.EntidadeNaoEncontradaException;
import com.algaworks.algatransito.domain.exception.NegocioException;
import lombok.AllArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.Map;
import java.util.stream.Collectors;

@AllArgsConstructor
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    private final MessageSource messageSource;

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatus(status);
        problemDetail.setTitle("Um ou mais campos inválidos");
        problemDetail.setType(URI.create("http://algatransito.com/erros/campos-invalidos"));

        Map<String, String> fields = ex.getBindingResult().getAllErrors()
                .stream()
                .collect(Collectors.toMap(
                        objectError -> ((FieldError) objectError).getField(),
                        //DefaultMessageSourceResolvable::getDefaultMessage
                        objetctError -> messageSource.getMessage(objetctError, LocaleContextHolder.getLocale() )
                        )
                );

        problemDetail.setProperty("fields, ", fields);


        return handleExceptionInternal(ex, problemDetail, headers, status, request);
    }

   /* @ExceptionHandler(NegocioException.class)
    public ResponseEntity<String> capturar(NegocioException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }*/
   @ExceptionHandler(NegocioException.class)
   public ProblemDetail handleNegocio(NegocioException e){
       ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
       problemDetail.setTitle(e.getMessage());
       problemDetail.setType(URI.create("http://algatransito.com/erros/regra-de-negocio"));
       return problemDetail;
   }

    @ExceptionHandler(EntidadeNaoEncontradaException.class)
    public ProblemDetail handleNegocio(EntidadeNaoEncontradaException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
        problemDetail.setTitle(e.getMessage());
        problemDetail.setType(URI.create("http://algatransito.com/erros/nao-encontrado"));
        return problemDetail;
    }

   @ExceptionHandler(DataIntegrityViolationException.class)
   ProblemDetail handleDataIntegrity(DataIntegrityViolationException e){
        ProblemDetail problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
        problemDetail.setTitle("Recurso está em uso");
        problemDetail.setType(URI.create("http://algatransito.com/erros/recurso-em-uso"));
        return problemDetail;
   }
}
