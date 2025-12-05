package com.labeleven.dto;

import lombok.*;
import java.util.List;

public class LabelDataDTO {
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Response {
        private Long id;
        private String fieldName;
        private String originalValue;
        private String translatedValue;
        private String category;
        private Long projectId;
    }
    
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ListResponse {
        private List<Response> labelData;
        private int totalCount;
    }
}
