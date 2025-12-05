package com.labeleven.service;

import com.labeleven.dto.LabelDataDTO;
import com.labeleven.entity.LabelData;
import com.labeleven.entity.Project;
import com.labeleven.repository.LabelDataRepository;
import com.labeleven.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LabelDataService {
    
    private final LabelDataRepository labelDataRepository;
    private final ProjectRepository projectRepository;
    
    @Transactional(readOnly = true)
    public LabelDataDTO.ListResponse getProjectLabelData(Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
        
        List<LabelData> labelDataList = labelDataRepository.findByProjectId(projectId);
        
        List<LabelDataDTO.Response> responseDTOs = labelDataList.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return LabelDataDTO.ListResponse.builder()
                .labelData(responseDTOs)
                .totalCount(responseDTOs.size())
                .build();
    }
    
    @Transactional(readOnly = true)
    public LabelDataDTO.Response getLabelData(Long id) {
        LabelData labelData = labelDataRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("라벨 데이터를 찾을 수 없습니다."));
        
        return convertToDTO(labelData);
    }
    
    private LabelDataDTO.Response convertToDTO(LabelData labelData) {
        return LabelDataDTO.Response.builder()
                .id(labelData.getId())
                .fieldName(labelData.getFieldName())
                .originalValue(labelData.getOriginalValue())
                .translatedValue(labelData.getTranslatedValue())
                .category(labelData.getCategory())
                .projectId(labelData.getProject().getId())
                .build();
    }
}
