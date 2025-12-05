package com.labeleven.repository;

import com.labeleven.entity.Pipeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PipelineRepository extends JpaRepository<Pipeline, Long> {
    
    Optional<Pipeline> findByReportId(Long reportId);
    
    List<Pipeline> findByReportProjectUserId(Long userId);
    
    List<Pipeline> findByStatus(String status);
    
    Optional<Pipeline> findFirstByReportIdOrderByCreatedAtDesc(Long reportId);
}