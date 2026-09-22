package br.com.paulosedassari.votacao.interfaces.web.error;

import br.com.paulosedassari.votacao.domain.elegibilidade.exception.CpfInvalidoException;
import br.com.paulosedassari.votacao.domain.pauta.exception.PautaNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.DuracaoSessaoInvalidaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEncerradaException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoEmAndamentoException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoJaExistenteException;
import br.com.paulosedassari.votacao.domain.sessao.exception.SessaoNaoEncontradaException;
import br.com.paulosedassari.votacao.domain.voto.exception.VotoDuplicadoException;
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

    @ExceptionHandler(CpfInvalidoException.class)
    ResponseEntity<ErroResponse> tratarCpfInvalido(
            CpfInvalidoException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "CPF_INVALIDO",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(PautaNaoEncontradaException.class)
    ResponseEntity<ErroResponse> tratarPautaNaoEncontrada(
            PautaNaoEncontradaException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "PAUTA_NAO_ENCONTRADA",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoNaoEncontradaException.class)
    ResponseEntity<ErroResponse> tratarSessaoNaoEncontrada(
            SessaoNaoEncontradaException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.NOT_FOUND,
                "SESSAO_NAO_ENCONTRADA",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoEncerradaException.class)
    ResponseEntity<ErroResponse> tratarSessaoEncerrada(
            SessaoEncerradaException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.CONFLICT,
                "SESSAO_ENCERRADA",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoEmAndamentoException.class)
    ResponseEntity<ErroResponse> tratarSessaoEmAndamento(
            SessaoEmAndamentoException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.CONFLICT,
                "SESSAO_EM_ANDAMENTO",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(VotoDuplicadoException.class)
    ResponseEntity<ErroResponse> tratarVotoDuplicado(
            VotoDuplicadoException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.CONFLICT,
                "VOTO_JA_REGISTRADO",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(SessaoJaExistenteException.class)
    ResponseEntity<ErroResponse> tratarSessaoJaExistente(
            SessaoJaExistenteException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.CONFLICT,
                "SESSAO_JA_EXISTENTE",
                exception.getMessage(),
                request
        );
    }

    @ExceptionHandler(DuracaoSessaoInvalidaException.class)
    ResponseEntity<ErroResponse> tratarDuracaoInvalida(
            DuracaoSessaoInvalidaException exception,
            HttpServletRequest request
    ) {
        return resposta(
                HttpStatus.BAD_REQUEST,
                "DURACAO_INVALIDA",
                exception.getMessage(),
                request
        );
    }

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
