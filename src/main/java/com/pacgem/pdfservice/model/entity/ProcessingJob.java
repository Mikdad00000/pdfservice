package com.pacgem.pdfservice.model.entity;


import com.pacgem.pdfservice.model.enums.ProcessingStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "processing_jobs")
@Data
@NoArgsConstructor
public class ProcessingJob {

    @Id
    private String id;

    @Column(name = "original_file_key")
    private String originalFileKey;

    @Column(name = "transformed_file_key")
    private String transformedFileKey;

    @Enumerated(EnumType.STRING)
    private ProcessingStatus status;

    @Column(name = "format")
    private String format;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) {
            this.status = ProcessingStatus.PENDING;
        }
    }
}

