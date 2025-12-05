package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.PipelineDTO;
import com.labeleven.service.PipelineService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pipelines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PipelineController {
    
    private final PipelineService pipelineService;
    
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> executePipeline(
            @RequestBody PipelineDTO.ExecuteRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            PipelineDTO.Response response = pipelineService.executePipeline(email, request);
            return ResponseEntity.ok(ApiResponse.success("파이프라인 실행 시작", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> getPipelineStatus(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            PipelineDTO.Response response = pipelineService.getPipelineStatus(email, id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<PipelineDTO.ResultResponse>> getPipelineResult(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            PipelineDTO.ResultResponse response = pipelineService.getPipelineResult(email, id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<Void>> stopPipeline(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            pipelineService.stopPipeline(email, id);
            return ResponseEntity.ok(ApiResponse.success("파이프라인 중단 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @PostMapping("/{id}/reexecute")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> reExecutePipeline(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            PipelineDTO.Response response = pipelineService.reExecutePipeline(email, id);
            return ResponseEntity.ok(ApiResponse.success("파이프라인 재실행 시작", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}