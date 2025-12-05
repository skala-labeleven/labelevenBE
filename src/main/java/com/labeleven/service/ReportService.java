package com.labeleven.service;

import com.labeleven.dto.ReportDTO;
import com.labeleven.entity.Report;
import com.labeleven.entity.Project;
import com.labeleven.repository.ReportRepository;
import com.labeleven.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReportService {
    
    private final ReportRepository reportRepository;
    private final ProjectRepository projectRepository;
    
    @Transactional
    public ReportDTO.Response createReport(String userEmail, ReportDTO.CreateRequest request) {
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다."));
        
        if (!project.getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        Report report = Report.builder()
                .project(project)
                .reportType(request.getReportType())
                .status("PENDING")
                .progress(0)
                .build();
        
        report = reportRepository.save(report);
        
        return convertToResponse(report);
    }
    
    @Transactional(readOnly = true)
    public ReportDTO.StatusResponse getReportStatus(Long reportId, String userEmail) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));
        
        if (!report.getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        return ReportDTO.StatusResponse.builder()
                .reportId(report.getId())
                .status(report.getStatus())
                .progress(report.getProgress())
                .currentStep(report.getCurrentStep())
                .build();
    }
    
    @Transactional(readOnly = true)
    public ReportDTO.Response getReport(Long reportId, String userEmail) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));
        
        if (!report.getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        return convertToResponse(report);
    }
    
    @Transactional
    public ReportDTO.Response approveReport(String userEmail, ReportDTO.ApprovalRequest request) {
        Report report = reportRepository.findById(request.getReportId())
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));
        
        if (!report.getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        // ⭐ 중요: 승인 상태 변경
        if (request.isApproved()) {
            report.setStatus("APPROVED");
        } else {
            report.setStatus("REJECTED");
            if (request.getFeedback() != null) {
                report.setErrorMessage(request.getFeedback());
            }
        }
        
        report = reportRepository.save(report);
        
        return convertToResponse(report);
    }
    
    @Transactional(readOnly = true)
    public ReportDTO.ListResponse getUserReports(String userEmail, String reportType) {
        List<Report> reports;
        
        if (reportType != null && !reportType.isEmpty()) {
            reports = reportRepository.findByProjectUserEmailAndReportTypeOrderByCreatedAtDesc(
                    userEmail, reportType);
        } else {
            reports = reportRepository.findByProjectUserEmailOrderByCreatedAtDesc(userEmail);
        }
        
        List<ReportDTO.Response> reportList = reports.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        
        return ReportDTO.ListResponse.builder()
                .reports(reportList)
                .total((int) reportList.size())
                .build();
    }
    
    @Transactional
    public void deleteReport(Long reportId, String userEmail) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new RuntimeException("보고서를 찾을 수 없습니다."));
        
        if (!report.getProject().getUser().getEmail().equals(userEmail)) {
            throw new RuntimeException("권한이 없습니다.");
        }
        
        reportRepository.delete(report);
    }
    
    private ReportDTO.Response convertToResponse(Report report) {
        return ReportDTO.Response.builder()
                .id(report.getId())
                .projectId(report.getProject().getId())
                .reportType(report.getReportType())
                .status(report.getStatus())
                .progress(report.getProgress())
                .currentStep(report.getCurrentStep())
                .content(report.getContent())
                .tagsJson(report.getTagsJson())
                .errorMessage(report.getErrorMessage())
                .createdAt(report.getCreatedAt())
                .updatedAt(report.getUpdatedAt())
                .build();
    }
}