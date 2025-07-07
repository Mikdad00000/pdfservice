package com.pacgem.pdfservice.repository;

import com.pacgem.pdfservice.model.entity.ProcessingJob;
import com.pacgem.pdfservice.model.enums.ProcessingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessingJobRepository extends JpaRepository<ProcessingJob, String> {
    long countByStatus(ProcessingStatus status);
    List<ProcessingJob> findByStatus(ProcessingStatus status);
}

