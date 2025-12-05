package com.labeleven.repository;

import com.labeleven.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long> {
    
    List<Report> findByProjectId(Long projectId);
    
    @Query("SELECT r FROM Report r WHERE r.project.user.email = :email AND r.reportType = :reportType ORDER BY r.createdAt DESC")
    List<Report> findByProjectUserEmailAndReportTypeOrderByCreatedAtDesc(
            @Param("email") String email, 
            @Param("reportType") String reportType);
    
    @Query("SELECT r FROM Report r WHERE r.project.user.email = :email ORDER BY r.createdAt DESC")
    List<Report> findByProjectUserEmailOrderByCreatedAtDesc(@Param("email") String email);
}