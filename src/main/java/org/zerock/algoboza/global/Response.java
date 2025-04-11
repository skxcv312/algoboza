package org.zerock.algoboza.global;

import lombok.*;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;


@Log4j2
public class Response<T> extends ResponseEntity<Response.ResponseBody<T>> {
    public record ResponseBody<T>(
            int statusCode,
            String massage,
            T data
    ) {
    }

    @Builder
    public Response(HttpStatusCode status, HttpHeaders headers, String massage, T data) {
        super(new ResponseBody<>(status.value(), massage, data), headers, status);
    }

}




