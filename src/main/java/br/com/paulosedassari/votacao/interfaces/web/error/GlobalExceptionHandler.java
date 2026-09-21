package br.com.paulosedassari.votacao.interfaces.web.error;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ErroResponse> tratarValidacao(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ViolacaoCampo> violacoes = exception.getBindingResult().getFieldErrors().stream()
                .map(erro -> new ViolacaoCampo(erro.getField(), erro.getDefaultMessage()))
                .toList();

        return resposta(
                HttpStatus.BAD_REQUEST,
                "REQUISICAO_INVALIDA",
                "Um ou mais campos são inválidos",
                request,
                violacoes
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    ResponseEntity<ErroResponse> tratarParametroObrigatorio(
            MissingServletRequestParameterException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "PARAMETRO_OBRIGATORIO",
                "O parâmetro '%s' é obrigatório".formatted(exception.getParameterName()),
                request
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    ResponseEntity<ErroResponse> tratarTipoDeParametro(
            MethodArgumentTypeMismatchException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "PARAMETRO_INVALIDO",
                "O parâmetro '%s' possui valor inválido".formatted(exception.getName()),
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    ResponseEntity<ErroResponse> tratarMetodo(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METODO_NAO_PERMITIDO",
                "O método HTTP utilizado não é permitido para este recurso",
                request
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<ErroResponse> tratarRecursoInexistente(
            NoResourceFoundException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "RECURSO_NAO_ENCONTRADO",
                "Recurso não encontrado", request
        );
    }

    private ResponseEntity<ErroResponse> resposta(
            HttpStatus status,
            String codigo,
            String mensagem,
            HttpServletRequest request
    ) {
        return resposta(
                status,
                codigo,
                mensagem,
                request,
                List.of()
        );
    }

    private ResponseEntity<ErroResponse> resposta(
            HttpStatus status,
            String codigo,
            String messagem,
            HttpServletRequest request,
            List<ViolacaoCampo> violacoes
    ) {
        ErroResponse erro = new ErroResponse(
                Instant.now(),
                status.value(),
                codigo,
                messagem,
                request.getRequestURI(),
                violacoes
        );

        log.warn(
                "Requisição rejeitada. status={} codigo={} caminho={}",
                status.value(),
                codigo,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(erro);
    }
}
