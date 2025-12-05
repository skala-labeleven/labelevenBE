package com.labeleven.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "label_data")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LabelData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "field_name", nullable = false, length = 255)
    private String fieldName;
    
    @Column(name = "original_value", nullable = false, columnDefinition = "TEXT")
    private String originalValue;
    
    @Column(name = "translated_value", columnDefinition = "TEXT")
    private String translatedValue;
    
    @Column(nullable = false, length = 255)
    private String category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    private Project project;
}
