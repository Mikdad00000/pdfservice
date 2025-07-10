package com.pacgem.pdfservice.service;

import com.pacgem.pdfservice.engine.PdfTransformationEngine;
import com.pacgem.pdfservice.model.dto.TransitStickerData;
import com.pacgem.pdfservice.model.entity.ProcessingJob;
import com.pacgem.pdfservice.model.dto.ProcessingRequest;
import com.pacgem.pdfservice.model.dto.ProcessingResult;
import com.pacgem.pdfservice.model.enums.ProcessingStatus;
import com.pacgem.pdfservice.repository.ProcessingJobRepository;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class PdfProcessingService {

    private final PdfTransformationEngine transformationEngine;
    private final StorageService storageService;
    private final ProcessingJobRepository jobRepository;
    private final MeterRegistry meterRegistry;

    @Async("pdfProcessingExecutor")
    public CompletableFuture<ProcessingResult> processAsync(
            String jobId, MultipartFile file, ProcessingRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            //   1. Save job metadata
            ProcessingJob job = createProcessingJob(jobId, request);
            jobRepository.save(job);

            // 2. Upload original file
            String originalFileKey = storageService.uploadFile(file, jobId);

            // 3. Transform PDF
            byte[] transformedPdf = transformationEngine.transform(file, request.getFormat());

            // 4. Upload transformed file
            String transformedFileKey = storageService.uploadProcessedFile(transformedPdf, jobId, request.getFormat());

            // 5. Update job status
            job.setStatus(ProcessingStatus.COMPLETED);
            job.setTransformedFileKey(transformedFileKey);
            jobRepository.save(job);

            sample.stop(Timer.builder("pdf.processing.duration")
                    .tag("format", request.getFormat())
                    .tag("status", "success")
                    .register(meterRegistry));

            return CompletableFuture.completedFuture(new ProcessingResult(jobId,""/*transformedFileKey*/));
        } catch (Exception e) {
            handleProcessingError(jobId, e);
            sample.stop(Timer.builder("pdf.processing.duration")
                    .tag("format", request.getFormat())
                    .tag("status", "error")
                    .register(meterRegistry));
            CompletableFuture<ProcessingResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
    @Async("pdfProcessingExecutor")
    public CompletableFuture<ProcessingResult> processAsync(
            String jobId, TransitStickerData data, ProcessingRequest request) {
        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            //   1. Save job metadata
            ProcessingJob job = createProcessingJob(jobId, request);
            jobRepository.save(job);

            // 3. Transform PDF
            byte[] transformedPdf = transformationEngine.transform(data, request.getFormat());

            Files.write(Paths.get("C:\\temp\\transit-sticker.pdf"), transformedPdf);

            // 4. Upload transformed file
            String transformedFileKey = storageService.uploadProcessedFile(transformedPdf, jobId, request.getFormat());

            // 5. Update job status
            job.setStatus(ProcessingStatus.COMPLETED);
            job.setTransformedFileKey(transformedFileKey);
            jobRepository.save(job);

            sample.stop(Timer.builder("pdf.processing.duration")
                    .tag("format", request.getFormat())
                    .tag("status", "success")
                    .register(meterRegistry));

            return CompletableFuture.completedFuture(new ProcessingResult(jobId,""/*transformedFileKey*/));
        } catch (Exception e) {
            handleProcessingError(jobId, e);
            sample.stop(Timer.builder("pdf.processing.duration")
                    .tag("format", request.getFormat())
                    .tag("status", "error")
                    .register(meterRegistry));
            CompletableFuture<ProcessingResult> failed = new CompletableFuture<>();
            failed.completeExceptionally(e);
            return failed;
        }
    }
    public List<CompletableFuture<ProcessingResult>> processBatch(List<MultipartFile> files, String format) {
        return files.stream()
                .map(file -> processAsync(
                        UUID.randomUUID().toString(),
                        file,
                        new ProcessingRequest(format)))
                .collect(Collectors.toList());
    }

    private ProcessingJob createProcessingJob(String jobId, ProcessingRequest request) {
        ProcessingJob job = new ProcessingJob();
        job.setId(jobId);
        job.setFormat(request.getFormat());
        job.setStatus(ProcessingStatus.PENDING);
        return job;
    }

    private void handleProcessingError(String jobId, Exception e) {
        log.error("Error processing PDF for job {}: {}", jobId, e.getMessage());
        ProcessingJob job = jobRepository.findById(jobId).orElse(null);
        if (job != null) {
            job.setStatus(ProcessingStatus.FAILED);
            job.setErrorMessage(e.getMessage());
            jobRepository.save(job);
        }
    }
    public ProcessingJob getJob(String jobId) {
        return jobRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }
    public ProcessingStatus getJobStatus(String jobId) {
        return jobRepository.findById(jobId)
                .map(ProcessingJob::getStatus)
                .orElseThrow(() -> new RuntimeException("Job not found: " + jobId));
    }

}
