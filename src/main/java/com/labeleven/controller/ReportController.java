package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.ReportDTO;
import com.labeleven.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "보고서 API", description = "라벨 정합성 체크 및 보고서 생성, 조회, 승인 관련 API")
@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class ReportController {
    
    private final ReportService reportService;
    
    @Operation(
        summary = "보고서 생성 요청 (U-REPORT-001)",
        description = "라벨 데이터의 정합성을 체크하고 보고서 생성을 요청합니다. " +
                     "AI 모델이 라벨 데이터를 검증하여 오류나 이상치를 찾아냅니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "보고서 생성 요청 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 데이터"
        )
    })
    @PostMapping
    public ResponseEntity<ApiResponse<ReportDTO.Response>> createReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "보고서 생성 요청 정보 (프로젝트 ID, 보고서 타입 등)"
            )
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
    
    @Operation(
        summary = "보고서 생성 상태 조회 (U-REPORT-002)",
        description = "보고서 생성 진행 상태를 조회합니다. " +
                     "상태: PENDING(대기중), PROCESSING(처리중), COMPLETED(완료), FAILED(실패)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "보고서를 찾을 수 없음"
        )
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ReportDTO.StatusResponse>> getReportStatus(
            @Parameter(description = "보고서 ID", required = true, example = "1")
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
    
    @Operation(
        summary = "생성된 보고서 조회 (U-REPORT-003)",
        description = "완성된 보고서의 전체 내용을 조회합니다. " +
                     "정합성 검증 결과, 발견된 이슈, 통계 정보 등이 포함됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "보고서를 찾을 수 없음"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> getReport(
            @Parameter(description = "보고서 ID", required = true, example = "1")
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
    
    @Operation(
        summary = "보고서 승인/거절 (U-REPORT-004)",
        description = "생성된 보고서를 검토 후 승인하거나 거절합니다. " +
                     "승인 시 보고서가 최종 확정되며, 거절 시 사유를 입력할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "처리 완료"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청"
        )
    })
    @PostMapping("/approval")
    public ResponseEntity<ApiResponse<ReportDTO.Response>> approveReport(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "승인/거절 요청 정보 (보고서 ID, 승인 여부, 사유)"
            )
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
    
    @Operation(
        summary = "보고서 목록 조회 (U-REPORT-005)",
        description = "사용자의 보고서 목록을 조회합니다. 보고서 타입으로 필터링 가능합니다. " +
                     "예: CONSISTENCY(정합성), QUALITY(품질), STATISTICS(통계)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ReportDTO.ListResponse>> getReports(
            @Parameter(description = "보고서 타입 필터 (선택사항)", example = "CONSISTENCY")
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
    
    @Operation(
        summary = "보고서 다운로드 (U-REPORT-006)",
        description = "보고서를 PDF 또는 Excel 형식으로 다운로드합니다. (현재 구현 예정)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "다운로드 URL 반환"
        )
    })
    @GetMapping("/{id}/download")
    public ResponseEntity<ApiResponse<String>> downloadReport(
            @Parameter(description = "보고서 ID", required = true, example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "다운로드 형식", example = "PDF")
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
    
    @Operation(
        summary = "보고서 삭제",
        description = "특정 보고서를 삭제합니다. 삭제된 보고서는 복구할 수 없습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "보고서를 찾을 수 없음"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteReport(
            @Parameter(description = "삭제할 보고서 ID", required = true, example = "1")
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