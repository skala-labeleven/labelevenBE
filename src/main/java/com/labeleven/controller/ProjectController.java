package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.ProjectDTO;
import com.labeleven.service.ProjectService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "프로젝트 API", description = "라벨링 프로젝트 생성, 조회, 삭제 및 파일 업로드 관련 API")
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@SecurityRequirement(name = "Bearer Authentication")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @Operation(
        summary = "프로젝트 생성 및 파일 업로드",
        description = "새로운 라벨링 프로젝트를 생성하고 CSV/Excel 파일을 업로드합니다. " +
                     "파일에서 라벨 데이터를 추출하여 프로젝트에 저장합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "프로젝트 생성 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "잘못된 파일 형식 또는 필수 파라미터 누락"
        )
    })
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ProjectDTO.Response>> uploadFile(
            @Parameter(description = "프로젝트 제목", required = true, example = "2024년 겨울 의류 라벨링")
            @RequestParam("title") String title,
            
            @Parameter(description = "국가 코드", required = true, example = "KR")
            @RequestParam("country") String country,
            
            @Parameter(description = "업로드할 CSV 또는 Excel 파일", required = false)
            @RequestParam(value = "file", required = false) MultipartFile file,
            
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectDTO.CreateRequest request = ProjectDTO.CreateRequest.builder()
                    .title(title)
                    .country(country)
                    .build();
            
            ProjectDTO.Response response = projectService.createProject(email, request, file);
            return ResponseEntity.ok(ApiResponse.success("프로젝트 생성 성공", response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "사용자 프로젝트 목록 조회",
        description = "현재 로그인한 사용자가 생성한 모든 프로젝트 목록을 조회합니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "조회 성공"
        )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<ProjectDTO.ListResponse>> getProjects(
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectDTO.ListResponse response = projectService.getUserProjects(email);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "프로젝트 상세 조회",
        description = "특정 프로젝트의 상세 정보를 조회합니다. 프로젝트 ID가 필요합니다."
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
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO.Response>> getProject(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long id,
            
            Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectDTO.Response response = projectService.getProject(id, email);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "프로젝트 삭제",
        description = "특정 프로젝트를 삭제합니다. 프로젝트와 관련된 모든 데이터가 삭제됩니다."
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "삭제 성공"
        ),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "프로젝트를 찾을 수 없음"
        )
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
            @Parameter(description = "삭제할 프로젝트 ID", required = true, example = "1")
            @PathVariable Long id,
            
            Authentication authentication) {
        try {
            String email = authentication.getName();
            projectService.deleteProject(id, email);
            return ResponseEntity.ok(ApiResponse.success("프로젝트 삭제 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @Operation(
        summary = "프로젝트 이미지 업로드",
        description = "특정 프로젝트에 여러 이미지 파일을 업로드합니다. (현재 구현 예정)"
    )
    @ApiResponses({
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "업로드 성공"
        )
    })
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> uploadImages(
            @Parameter(description = "프로젝트 ID", required = true, example = "1")
            @PathVariable Long id,
            
            @Parameter(description = "업로드할 이미지 파일들", required = true)
            @RequestParam("files") MultipartFile[] files,
            
            Authentication authentication) {
        try {
            String email = authentication.getName();
            // 이미지 업로드 로직 구현
            return ResponseEntity.ok(ApiResponse.success("이미지 업로드 성공", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}