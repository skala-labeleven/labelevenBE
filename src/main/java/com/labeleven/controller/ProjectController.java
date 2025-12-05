package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.ProjectDTO;
import com.labeleven.service.ProjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProjectController {
    
    private final ProjectService projectService;
    
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<ProjectDTO.Response>> uploadFile(
            @RequestParam("title") String title,
            @RequestParam("country") String country,
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
    
    @GetMapping
    public ResponseEntity<ApiResponse<ProjectDTO.ListResponse>> getProjects(Authentication authentication) {
        try {
            String email = authentication.getName();
            ProjectDTO.ListResponse response = projectService.getUserProjects(email);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProjectDTO.Response>> getProject(
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
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteProject(
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
    
    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<Void>> uploadImages(
            @PathVariable Long id,
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
