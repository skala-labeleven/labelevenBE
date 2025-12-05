package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.PipelineDTO;
import com.labeleven.service.PipelineService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Tag(name = "파이프라인 API", description = "AI 파이프라인 실행, 모니터링 및 제어 관련 API")
@RestController
@RequestMapping("/api/pipelines")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class PipelineController {
    
    private final PipelineService pipelineService;
    
    @Operation(
        summary = "파이프라인 실행",
        description = "AI 모델 파이프라인을 실행합니다. " +
                     "라벨 데이터를 처리하고 학습/검증/추론 작업을 수행합니다. " +
                     "프로젝트 ID와 실행할 파이프라인 타입을 지정해야 합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "파이프라인 실행 시작"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 요청 또는 리소스 부족"
        )
    })
    @PostMapping("/execute")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> executePipeline(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "파이프라인 실행 요청 정보 (프로젝트 ID, 파이프라인 타입, 설정 등)"
            )
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
    
    @Operation(
        summary = "파이프라인 상태 조회",
        description = "실행 중인 파이프라인의 현재 상태를 조회합니다. " +
                     "상태: PENDING(대기), RUNNING(실행중), COMPLETED(완료), FAILED(실패), STOPPED(중단됨). " +
                     "진행률(progress)과 현재 단계 정보도 함께 반환됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "파이프라인을 찾을 수 없음"
        )
    })
    @GetMapping("/{id}/status")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> getPipelineStatus(
            @Parameter(description = "파이프라인 ID", required = true, example = "1")
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
    
    @Operation(
        summary = "파이프라인 실행 결과 조회",
        description = "완료된 파이프라인의 실행 결과를 조회합니다. " +
                     "모델 성능 지표, 생성된 아티팩트, 로그, 오류 정보 등이 포함됩니다. " +
                     "정확도, 손실값, F1 스코어 등의 메트릭을 확인할 수 있습니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "파이프라인을 찾을 수 없음"
        )
    })
    @GetMapping("/{id}/result")
    public ResponseEntity<ApiResponse<PipelineDTO.ResultResponse>> getPipelineResult(
            @Parameter(description = "파이프라인 ID", required = true, example = "1")
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
    
    @Operation(
        summary = "파이프라인 중단",
        description = "실행 중인 파이프라인을 강제로 중단합니다. " +
                     "중단된 파이프라인은 현재까지의 작업을 저장하고 안전하게 종료됩니다. " +
                     "중단 후 재실행이 가능합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "중단 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "이미 완료되었거나 중단할 수 없는 상태"
        )
    })
    @PostMapping("/{id}/stop")
    public ResponseEntity<ApiResponse<Void>> stopPipeline(
            @Parameter(description = "중단할 파이프라인 ID", required = true, example = "1")
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
    
    @Operation(
        summary = "파이프라인 재실행",
        description = "이전에 실행했던 파이프라인을 동일한 설정으로 다시 실행합니다. " +
                     "실패했거나 중단된 파이프라인을 처음부터 다시 시작할 때 유용합니다. " +
                     "이전 설정과 파라미터가 그대로 유지됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "재실행 시작"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "파이프라인을 찾을 수 없음"
        )
    })
    @PostMapping("/{id}/reexecute")
    public ResponseEntity<ApiResponse<PipelineDTO.Response>> reExecutePipeline(
            @Parameter(description = "재실행할 파이프라인 ID", required = true, example = "1")
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