package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.LabelDataDTO;
import com.labeleven.service.LabelDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "라벨 데이터 API", description = "라벨링 데이터 조회 및 관리 API")
@RestController
@RequestMapping("/api/label-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class LabelDataController {
    
    private final LabelDataService labelDataService;
    
    @Operation(
        summary = "프로젝트의 라벨 데이터 목록 조회",
        description = "특정 프로젝트에 속한 모든 라벨 데이터를 조회합니다. " +
                     "업로드된 CSV/Excel 파일에서 추출된 라벨 정보들이 반환됩니다. " +
                     "각 데이터에는 라벨 텍스트, 카테고리, 상태 정보 등이 포함됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "프로젝트를 찾을 수 없음"
        )
    })
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<LabelDataDTO.ListResponse>> getProjectLabelData(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long projectId) {
        try {
            LabelDataDTO.ListResponse response = labelDataService.getProjectLabelData(projectId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "특정 라벨 데이터 상세 조회",
        description = "개별 라벨 데이터의 상세 정보를 조회합니다. " +
                     "라벨 ID를 통해 특정 라벨의 전체 속성과 메타데이터를 확인할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "라벨 데이터를 찾을 수 없음"
        )
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelDataDTO.Response>> getLabelData(
            @Parameter(description = "라벨 데이터 ID", required = true, example = "1")
            @PathVariable Long id) {
        try {
            LabelDataDTO.Response response = labelDataService.getLabelData(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}