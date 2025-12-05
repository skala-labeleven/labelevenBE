package com.labeleven.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

public class ProjectDTO {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class CreateRequest {
        private String title;
        private String country;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String title;
        private String country;
        private String status;
        private LocalDateTime createdAt;
        private Long userId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private List<Response> projects;
        private int totalCount;
    }
}
