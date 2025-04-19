package server.gooroomi.global.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import server.gooroomi.global.handler.response.BaseException;
import server.gooroomi.global.handler.response.BaseResponse;
import server.gooroomi.global.handler.response.BaseResponseStatus;

@Slf4j
@RestControllerAdvice(annotations = RestController.class)
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * BaseException 처리
     */
    @ExceptionHandler(BaseException.class)
    public ResponseEntity<BaseResponse> handleBaseException(BaseException e) {
        log.info(e.getStatus().toString());
        return ResponseEntity.status(e.getStatus().getHttpStatus())
                .body(BaseResponse.fail(e.getStatus()));
    }

    /**
     * 경로는 있으나 지원하지 않는 http method로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(BaseResponse.fail(BaseResponseStatus.METHOD_NOT_ALLOWED));
    }

    /**
     * 지원하지 않는 content type으로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(BaseResponse.fail(BaseResponseStatus.UNSUPPORTED_MEDIA_TYPE));
    }

    /**
     * 인식할 수 없는 content type으로 요청 시
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
                .body(BaseResponse.fail(BaseResponseStatus.NOT_ACCEPTABLE));
    }

    /**
     * PathVariable 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.MISSING_PATH_VARIABLE));
    }

    /***
     * RequestParam 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PARAM));
    }

    /**
     * RequestPart 값 누락 시
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.MISSING_REQUEST_PART));
    }

    /**
     * 요청 값 바인딩 처리에 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.REQ_BINDING_FAIL));
    }

    /**
     *	request @Valid 유효성 체크를 통과하지 못한 경우
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.FAILED_VALIDATION));
    }

    /**
     * Dispatcher Servlet에서 핸들러를 찾지 못한 경우
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(BaseResponse.fail(BaseResponseStatus.NOT_FOUND));
    }

    /**
     * 파라미터 타입 불일치가 발생한 경우
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.MISMATCH_PARAM_TYPE));
    }

    /**
     * 적절하지 않은 RequestBody 때문에 HttpMessageConverter.read 메소드 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(BaseResponse.fail(BaseResponseStatus.BAD_REQUEST));
    }

    /**
     * 직렬화 실패한 경우
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }

    @ExceptionHandler
    public ResponseEntity<BaseResponse> handleException(Exception e) {
        log.error("Exception 발생: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(BaseResponse.fail(BaseResponseStatus.INTERNAL_SERVER_ERROR));
    }
}
