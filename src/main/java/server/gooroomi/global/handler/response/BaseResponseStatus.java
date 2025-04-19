package server.gooroomi.global.handler.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum BaseResponseStatus {

    /**
     * 200 : 요청 성공
     */
    SUCCESS(true, 200, HttpStatus.OK, "요청 성공"),

    /**
     * 400 BAD_REQUEST 잘못된 요청
     */
    BAD_REQUEST(false, 400, HttpStatus.BAD_REQUEST,"잘못된 요청"),
    VALIDATION_ERROR(false,40001, HttpStatus.BAD_REQUEST, "입력값을 확인해주세요."),
    MISSING_PATH_VARIABLE(false,40002, HttpStatus.BAD_REQUEST,"경로 변수가 누락되었습니다."),
    MISSING_REQUEST_PARAM(false,40003, HttpStatus.BAD_REQUEST,"쿼리 파라미터가 누락되었습니다."),
    MISSING_REQUEST_PART(false,40004, HttpStatus.BAD_REQUEST,"multipart/form-data 파일이 누락되었습니다."),
    REQ_BINDING_FAIL(false,40005, HttpStatus.BAD_REQUEST, "잘못된 request 입니다."),
    MISMATCH_PARAM_TYPE(false,40006, HttpStatus.BAD_REQUEST, "잘못된 파라미터 타입입니다."),
    FAILED_VALIDATION(false, 40007, HttpStatus.BAD_REQUEST,"입력값이 누락되었거나, 부적절한 입력 값이 있습니다."),

    /**
     * 401 UNAUTHORIZED 권한없음(인증 실패)
     */
    UNAUTHORIZED(false, 401, HttpStatus.UNAUTHORIZED, "인증 실패"),

    /**
     * 403 FORBIDDEN 권한없음
     */
    FORBIDDEN(false,403, HttpStatus.FORBIDDEN, "접근 권한이 없음"),

    /**
     * 404 NOT_FOUND 잘못된 리소스 접근
     */
    NOT_FOUND( false,404, HttpStatus.NOT_FOUND,"Not Found"),
    NOT_FOUND_USER(false,40401, HttpStatus.NOT_FOUND,"해당 User를 찾을 수 없습니다"),

    /**
     * 405 METHOD_NOT_ALLOWED 지원하지 않은 method 호출
     */
    METHOD_NOT_ALLOWED(false,405, HttpStatus.METHOD_NOT_ALLOWED,"해당 매서드는 지원하지 않는 메서드입니다."),

    /**
     * 406 NOT_ACCEPTABLE 인식할 수 없는 content type
     */
    NOT_ACCEPTABLE(false,406, HttpStatus.NOT_ACCEPTABLE, "인식할 수 없는 미디어 타입입니다."),

    /**
     * 409 CONFLICT 중복된 리소스
     */
    CONFLICT(false, 409, HttpStatus.CONFLICT, "중복된 리소스"),

    /**
     * 415 UNSUPPORTED_MEDIA_TYPE 지원하지 않는 content type
     */
    UNSUPPORTED_MEDIA_TYPE(false,415, HttpStatus.UNSUPPORTED_MEDIA_TYPE, "지원하지 않는 미디어 타입입니다."),

    /**
     * 5XX Error
     */
    INTERNAL_SERVER_ERROR( false,500, HttpStatus.INTERNAL_SERVER_ERROR,"서버 내부 에러");

    private final boolean isSuccess;
    private final int code;
    private final HttpStatus httpStatus;
    private final String message;
}
