package com.labeleven.repository;

import com.labeleven.entity.LabelData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LabelDataRepository extends JpaRepository<LabelData, Long> {
    List<LabelData> findByProjectId(Long projectId);
    List<LabelData> findByProjectIdAndCategory(Long projectId, String category);
}
