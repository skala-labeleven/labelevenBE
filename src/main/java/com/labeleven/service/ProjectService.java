package com.labeleven.service;

import com.labeleven.dto.ProjectDTO;
import com.labeleven.entity.Project;
import com.labeleven.entity.User;
import com.labeleven.repository.ProjectRepository;
import com.labeleven.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProjectService {
    
    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;
    
    @Transactional
    public ProjectDTO.Response createProject(String email, ProjectDTO.CreateRequest request, MultipartFile file) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Project project = Project.builder()
                .title(request.getTitle())
                .country(request.getCountry())
                .status("PROCESSING")
                .user(user)
                .build();
        
        Project savedProject = projectRepository.save(project);
        
        // 파일 업로드 처리
        if (file != null && !file.isEmpty()) {
            fileStorageService.storeFile(file, savedProject.getId());
        }
        
        return convertToDTO(savedProject);
    }
    
    @Transactional(readOnly = true)
    public ProjectDTO.ListResponse getUserProjects(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        List<Project> projects = projectRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        
        List<ProjectDTO.Response> projectDTOs = projects.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        
        return ProjectDTO.ListResponse.builder()
                .projects(projectDTOs)
                .totalCount(projectDTOs.size())
                .build();
    }
    
    @Transactional(readOnly = true)
    public ProjectDTO.Response getProject(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
        
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        
        return convertToDTO(project);
    }
    
    @Transactional
    public void deleteProject(Long projectId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
        
        if (!project.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }
        
        projectRepository.delete(project);
    }
    
    private ProjectDTO.Response convertToDTO(Project project) {
        return ProjectDTO.Response.builder()
                .id(project.getId())
                .title(project.getTitle())
                .country(project.getCountry())
                .status(project.getStatus())
                .createdAt(project.getCreatedAt())
                .userId(project.getUser().getId())
                .build();
    }
}
