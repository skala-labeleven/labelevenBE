package com.labeleven.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class PipelineDTO {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ExecuteRequest {
        private Long reportId;
        private Map<String, Object> parameters;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Long reportId;
        private String status; // "RUNNING", "COMPLETED", "FAILED", "STOPPED"
        private Integer progress; // 0-100
        private List<StepStatus> steps;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StepStatus {
        private String stepName;
        private String status; // "PENDING", "RUNNING", "COMPLETED", "FAILED"
        private Integer progress;
        private String message;
        private LocalDateTime startedAt;
        private LocalDateTime completedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ResultResponse {
        private Long pipelineId;
        private Map<String, Object> schemaResult;
        private Map<String, Object> translationResult;
        private Map<String, Object> diagnosisResult;
        private Map<String, Object> checklistResult;
        private Map<String, Object> finalReportResult;
    }
}