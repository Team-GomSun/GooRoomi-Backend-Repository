package server.gooroomi.global.apiPayload.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import server.gooroomi.global.apiPayload.code.BaseErrorCode;
import server.gooroomi.global.apiPayload.code.ErrorReasonDTO;

@Getter
@AllArgsConstructor
public class GeneralException extends RuntimeException {
    private BaseErrorCode code;
    public ErrorReasonDTO getErrorReason() {
        return this.code.getReason();
    }
    public ErrorReasonDTO getErrorReasonHttpStatus(){
        return this.code.getReasonHttpStatus();
    }
}