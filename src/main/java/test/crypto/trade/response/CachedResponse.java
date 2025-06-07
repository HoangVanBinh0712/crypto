package test.crypto.trade.response;

import lombok.Data;

@Data
public class CachedResponse {
    private final int status;
    private final String contentType;
    private final String body;

}
