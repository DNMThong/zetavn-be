package com.zetavn.api.payload.response;


import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
@Builder @ToString
public class Paginate<T> {
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean lastPage;
    private T data;
}
