package responseModels;

import lombok.Getter;

/**
 * Created by Adebowale on 07/05/2019.
 */
public class ErrorResponse {
    private @Getter int errorCode;
    private @Getter String errorMessage;
}