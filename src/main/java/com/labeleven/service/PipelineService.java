package com.labeleven.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.labeleven.dto.PipelineDTO;
import com.labeleven.entity.Pipeline;
import com.labeleven.entity.Report;
import com.labeleven.repository.PipelineRepository;
import com.labeleven.repository.ReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class PipelineService {
    
    private final PipelineRepository pipelineRepository;
    private final ReportRepository reportRepository;
    private final ObjectMapper objectMapper;
    
    @Transactional
    public PipelineDTO.Response executePipeline(String userEmail, PipelineDTO.ExecuteRequest request) {
        Report report = reportRepository.findById(request.getReportId())
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));
        
        if (!report.getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        if (!"APPROVED".equals(report.getStatus())) {
            throw new RuntimeException("승인된 보고서만 파이프라인 실행이 가능합니다.");
        }
        
        // 파이프라인 생성
        Pipeline pipeline = Pipeline.builder()
                .report(report)
                .status("RUNNING")
                .progress(0)
                .startedAt(LocalDateTime.now())
                .build();
        
        // 초기 단계 상태 설정
        List<PipelineDTO.StepStatus> steps = initializeSteps();
        try {
            pipeline.setStepStatuses(objectMapper.writeValueAsString(steps));
        } catch (Exception e) {
            throw new RuntimeException("단계 초기화 실패", e);
        }
        
        pipeline = pipelineRepository.save(pipeline);
        
        // TODO: 비동기로 FastAPI 호출 (실제 구현은 별도로)
        // executePipelineAsync(pipeline.getId());
        
        return convertToResponse(pipeline);
    }
    
    @Transactional(readOnly = true)
    public PipelineDTO.Response getPipelineStatus(String userEmail, Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("파이프라인을 찾을 수 없습니다."));
        
        if (!pipeline.getReport().getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        return convertToResponse(pipeline);
    }
    
    @Transactional(readOnly = true)
    public PipelineDTO.ResultResponse getPipelineResult(String userEmail, Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("파이프라인을 찾을 수 없습니다."));
        
        if (!pipeline.getReport().getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        if (!"COMPLETED".equals(pipeline.getStatus())) {
            throw new RuntimeException("완료된 파이프라인만 결과 조회가 가능합니다.");
        }
        
        return PipelineDTO.ResultResponse.builder()
                .pipelineId(pipeline.getId())
                .schemaResult(parseJson(pipeline.getSchemaResult()))
                .translationResult(parseJson(pipeline.getTranslationResult()))
                .diagnosisResult(parseJson(pipeline.getDiagnosisResult()))
                .checklistResult(parseJson(pipeline.getChecklistResult()))
                .finalReportResult(parseJson(pipeline.getFinalReportResult()))
                .build();
    }
    
    @Transactional
    public void stopPipeline(String userEmail, Long pipelineId) {
        Pipeline pipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("파이프라인을 찾을 수 없습니다."));
        
        if (!pipeline.getReport().getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        if (!"RUNNING".equals(pipeline.getStatus())) {
            throw new RuntimeException("실행 중인 파이프라인만 중단할 수 있습니다.");
        }
        
        pipeline.setStatus("STOPPED");
        pipeline.setCompletedAt(LocalDateTime.now());
        pipelineRepository.save(pipeline);
    }
    
    @Transactional
    public PipelineDTO.Response reExecutePipeline(String userEmail, Long pipelineId) {
        Pipeline oldPipeline = pipelineRepository.findById(pipelineId)
                .orElseThrow(() -> new RuntimeException("파이프라인을 찾을 수 없습니다."));
        
        if (!oldPipeline.getReport().getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        // 새로운 파이프라인 생성
        PipelineDTO.ExecuteRequest request = PipelineDTO.ExecuteRequest.builder()
                .reportId(oldPipeline.getReport().getId())
                .build();
        
        return executePipeline(userEmail, request);
    }
    
    private List<PipelineDTO.StepStatus> initializeSteps() {
        List<PipelineDTO.StepStatus> steps = new ArrayList<>();
        String[] stepNames = {"스키마 추출", "번역", "진단", "체크리스트", "최종보고서"};
        
        for (String stepName : stepNames) {
            steps.add(PipelineDTO.StepStatus.builder()
                    .stepName(stepName)
                    .status("PENDING")
                    .progress(0)
                    .build());
        }
        
        return steps;
    }
    
    private PipelineDTO.Response convertToResponse(Pipeline pipeline) {
        List<PipelineDTO.StepStatus> steps = new ArrayList<>();
        try {
            if (pipeline.getStepStatuses() != null) {
                steps = objectMapper.readValue(
                        pipeline.getStepStatuses(),
                        objectMapper.getTypeFactory().constructCollectionType(List.class, PipelineDTO.StepStatus.class)
                );
            }
        } catch (Exception e) {
            // JSON 파싱 실패 시 빈 리스트
        }
        
        return PipelineDTO.Response.builder()
                .id(pipeline.getId())
                .reportId(pipeline.getReport().getId())
                .status(pipeline.getStatus())
                .progress(pipeline.getProgress())
                .steps(steps)
                .startedAt(pipeline.getStartedAt())
                .completedAt(pipeline.getCompletedAt())
                .build();
    }
    
    private Map<String, Object> parseJson(String json) {
        try {
            if (json == null || json.isEmpty()) {
                return new HashMap<>();
            }
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new HashMap<>();
        }
    }
}