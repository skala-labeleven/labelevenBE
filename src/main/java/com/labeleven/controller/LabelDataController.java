package com.labeleven.controller;

import com.labeleven.dto.ApiResponse;
import com.labeleven.dto.LabelDataDTO;
import com.labeleven.service.LabelDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/label-data")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class LabelDataController {
    
    private final LabelDataService labelDataService;
    
    @GetMapping("/project/{projectId}")
    public ResponseEntity<ApiResponse<LabelDataDTO.ListResponse>> getProjectLabelData(
            @PathVariable Long projectId) {
        try {
            LabelDataDTO.ListResponse response = labelDataService.getProjectLabelData(projectId);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<LabelDataDTO.Response>> getLabelData(@PathVariable Long id) {
        try {
            LabelDataDTO.Response response = labelDataService.getLabelData(id);
            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
