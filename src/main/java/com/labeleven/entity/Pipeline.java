package com.labeleven.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "pipelines")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pipeline {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    private Report report;
    
    @Column(nullable = false, length = 20)
    private String status; // RUNNING, COMPLETED, FAILED, STOPPED
    
    @Column(nullable = false)
    private Integer progress = 0; // 0-100
    
    @Column(columnDefinition = "TEXT")
    private String stepStatuses; // JSON으로 저장
    
    @Column(columnDefinition = "TEXT")
    private String schemaResult; // JSON
    
    @Column(columnDefinition = "TEXT")
    private String translationResult; // JSON
    
    @Column(columnDefinition = "TEXT")
    private String diagnosisResult; // JSON
    
    @Column(columnDefinition = "TEXT")
    private String checklistResult; // JSON
    
    @Column(columnDefinition = "TEXT")
    private String finalReportResult; // JSON
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}