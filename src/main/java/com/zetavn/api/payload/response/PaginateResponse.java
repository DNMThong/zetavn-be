package com.zetavn.api.payload.response;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder @ToString
public class PaginateResponse<T> {

    private T data;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
}
