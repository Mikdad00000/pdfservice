package com.pacgem.pdfservice.controller;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;
import com.pacgem.pdfservice.model.dto.*;
import com.pacgem.pdfservice.model.entity.ProcessingJob;
import com.pacgem.pdfservice.model.enums.ProcessingStatus;
import com.pacgem.pdfservice.service.PdfProcessingService;
import com.pacgem.pdfservice.service.StorageService;
//import jakarta.validation.Valid;
import com.pacgem.pdfservice.util.FontInfoExtractor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/pdf")
@Slf4j
@RequiredArgsConstructor
public class PdfController {

    private final PdfProcessingService processingService;
    private final StorageService storageService;

    @PostMapping("/process")
    public ResponseEntity<ProcessingResponse> processPdf(
            @RequestParam("file") /*@Valid*/ MultipartFile file,
            @RequestParam("format") String format,
            @RequestParam(value = "async", defaultValue = "false") boolean async
    ) {
        validatePdfFile(file);

        String jobId = UUID.randomUUID().toString();
        ProcessingRequest request = new ProcessingRequest(format);

        if (async) {
            processingService.processAsync(jobId, file, request);
            return ResponseEntity.accepted()
                    .body(new ProcessingResponse(jobId, ProcessingStatus.PROCESSING));
        } else {
            try {
                ProcessingResult result = processingService
                        .processAsync(jobId, file, request)
                        .get(30, TimeUnit.SECONDS);

                return ResponseEntity.ok(
                        new ProcessingResponse(jobId, ProcessingStatus.COMPLETED, result.getTransformedFileKey())
                );
            } catch (TimeoutException e) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(new ProcessingResponse(jobId, ProcessingStatus.TIMEOUT));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ProcessingResponse(jobId, ProcessingStatus.FAILED, e.getMessage()));
            }
        }
    }

    @PostMapping("/process-json")
    public ResponseEntity<ProcessingResponse> processJson(
            @RequestBody TransitStickerData data,
            @RequestParam("format") String format,
            @RequestParam(value = "async", defaultValue = "false") boolean async
    ) throws IOException {
        String jobId = UUID.randomUUID().toString();




//        String html = "<html>" +
//                "<head></head>" +
//                "<body>" +
//                "<h1>Invoice</h1>" +
//                "<p>Total: $100</p>" +
//                "</body></html>";
//
//        String filePath = "C:/temp/output.pdf"; // ✅ Use absolute path
//
//        FileOutputStream outputStream = new FileOutputStream(filePath);
//
//        PdfRendererBuilder builder = new PdfRendererBuilder();
//        builder.useFastMode();
//        builder.withHtmlContent(html, null);
//        builder.toStream(outputStream);
//        builder.run();
//
//        outputStream.close();
//        System.out.println("PDF generated successfully at: " + filePath);
        ProcessingRequest request = new ProcessingRequest(format);

        if (async) {
            processingService.processAsync(jobId, data, request);
            return ResponseEntity.accepted()
                    .body(new ProcessingResponse(jobId, ProcessingStatus.PROCESSING));
        } else {
            try {
                ProcessingResult result = processingService
                        .processAsync(jobId, data, request)
                        .get(30, TimeUnit.SECONDS);

                return ResponseEntity.ok(
                        new ProcessingResponse(jobId, ProcessingStatus.COMPLETED, result.getTransformedFileKey())
                );
            } catch (TimeoutException e) {
                return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT)
                        .body(new ProcessingResponse(jobId, ProcessingStatus.TIMEOUT));
            } catch (Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(new ProcessingResponse(jobId, ProcessingStatus.FAILED, e.getMessage()));
            }
        }
    }


    @PostMapping("/batch")
    public ResponseEntity<BatchProcessingResponse> processBatch(
            @RequestParam("files") List<MultipartFile> files,
            @RequestParam("format") String format
    ) {
        List<CompletableFuture<ProcessingResult>> futures = processingService.processBatch(files, format);
        String batchId = UUID.randomUUID().toString();
        return ResponseEntity.accepted()
                .body(new BatchProcessingResponse(batchId, futures.size()));
    }

    @GetMapping("/status/{jobId}")
    public ResponseEntity<ProcessingStatus> getStatus(@PathVariable String jobId) {
        // for now, assume a status check directly from repository
        // can later refactor to PdfProcessingService
        ProcessingStatus status = processingService
                .getJobStatus(jobId);
        return ResponseEntity.ok(status);
    }

    @GetMapping("/download/{jobId}")
    public ResponseEntity<Resource> downloadResult(@PathVariable String jobId) {
        ProcessingJob job = processingService.getJob(jobId);

        if (job.getStatus() != ProcessingStatus.COMPLETED) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = storageService.downloadFile(job.getTransformedFileKey());

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + jobId + ".pdf\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }

    private void validatePdfFile(MultipartFile file) {
        if (file.isEmpty() || !file.getContentType().equals("application/pdf")) {
            throw new IllegalArgumentException("Invalid PDF file");
        }
    }
}
