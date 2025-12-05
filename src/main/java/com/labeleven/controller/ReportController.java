package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.ReportDTO;
import com.labeleven.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReportController {
    
    private final ReportService reportService;
    
    // U-REPORT-001: 정합성 체크 및 보고서 생성 요청
    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO.Response>> createReport(
            @RequestBody ReportDTO.CreateRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ReportDTO.Response response = reportService.createReport(email, request);
            return ResponseEntity.ok(ApiResponse.success("보고서 생성 요청 완료", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // U-REPORT-002: 보고서 생성 상태 조회
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReportDTO.StatusResponse>> getReportStatus(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ReportDTO.StatusResponse response = reportService.getReportStatus(id, email);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // U-REPORT-003: 생성된 보고서 조회
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> getReport(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ReportDTO.Response response = reportService.getReport(id, email);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // U-REPORT-004: 보고서 승인/거절
    @PostMapping("/approval")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> approveReport(
            @RequestBody ReportDTO.ApprovalRequest request,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ReportDTO.Response response = reportService.approveReport(email, request);
            return ResponseEntity.ok(ApiResponse.success("처리 완료", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // U-REPORT-005: 보고서 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<ReportDTO.ListResponse>> getReports(
            @RequestParam(required = false) String reportType,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ReportDTO.ListResponse response = reportService.getUserReports(email, reportType);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    // U-REPORT-006: 보고서 다운로드
    @GetMapping("/{id}/download")
    public ResponseEntity<ApiResponse<String>> downloadReport(
            @PathVariable Long id,
            @RequestParam(defaultValue = "PDF") String format,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            // TODO: PDF/Excel 다운로드 구현
            return ResponseEntity.ok(ApiResponse.success("다운로드 URL", "/downloads/report-" + id + ".pdf"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @PathVariable Long id,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            reportService.deleteReport(id, email);
            return ResponseEntity.ok(ApiResponse.success("보고서 삭제 완료", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}