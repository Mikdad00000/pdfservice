-- V2__Add_performance_optimizations.sql
-- Additional indexes and optimizations for better performance

-- Add composite indexes for common query patterns
CREATE INDEX idx_pdf_user_created ON pdf_documents(user_id, created_at DESC);
CREATE INDEX idx_pdf_user_status_created ON pdf_documents(user_id, processing_status, created_at DESC);
CREATE INDEX idx_pdf_status_created ON pdf_documents(processing_status, created_at DESC);

-- Add indexes for job processing queries
CREATE INDEX idx_jobs_status_priority_queued ON pdf_processing_jobs(status, priority ASC, queued_at ASC);
CREATE INDEX idx_jobs_document_status ON pdf_processing_jobs(document_id, status);
CREATE INDEX idx_jobs_type_status ON pdf_processing_jobs(job_type, status);

-- Add indexes for text search optimization
CREATE INDEX idx_text_document_page ON extracted_text(document_id, page_number ASC);
CREATE INDEX idx_text_word_count ON extracted_text(word_count DESC);

-- Add indexes for image queries
CREATE INDEX idx_images_document_page_index ON extracted_images(document_id, page_number ASC, image_index ASC);
CREATE INDEX idx_images_size ON extracted_images(image_size DESC);

-- Add indexes for API key lookups
CREATE INDEX idx_apikeys_user_active ON api_keys(user_id, is_active);
CREATE INDEX idx_apikeys_expires ON api_keys(expires_at);

-- Add indexes for audit log queries
CREATE INDEX idx_audit_user_created ON audit_logs(user_id, created_at DESC);
CREATE INDEX idx_audit_entity_created ON audit_logs(entity_type, entity_id, created_at DESC);
CREATE INDEX idx_audit_action_created ON audit_logs(action, created_at DESC);

-- Create a view for document statistics
CREATE VIEW document_stats AS
SELECT
    d.id,
    d.user_id,
    d.original_filename,
    d.file_size,
    d.page_count,
    d.processing_status,
    d.created_at,
    COUNT(DISTINCT j.id) as total_jobs,
    COUNT(DISTINCT CASE WHEN j.status = 'COMPLETED' THEN j.id END) as completed_jobs,
    COUNT(DISTINCT CASE WHEN j.status = 'FAILED' THEN j.id END) as failed_jobs,
    COUNT(DISTINCT et.id) as extracted_text_pages,
    COUNT(DISTINCT ei.id) as extracted_images,
    COALESCE(SUM(et.text_length), 0) as total_text_length,
    COALESCE(SUM(et.word_count), 0) as total_word_count
FROM pdf_documents d
         LEFT JOIN pdf_processing_jobs j ON d.id = j.document_id
         LEFT JOIN extracted_text et ON d.id = et.document_id
         LEFT JOIN extracted_images ei ON d.id = ei.document_id
GROUP BY d.id, d.user_id, d.original_filename, d.file_size, d.page_count, d.processing_status, d.created_at;

-- Create a view for user statistics
CREATE VIEW user_stats AS
SELECT
    u.id,
    u.username,
    u.email,
    u.created_at,
    COUNT(DISTINCT d.id) as total_documents,
    COUNT(DISTINCT CASE WHEN d.processing_status = 'COMPLETED' THEN d.id END) as completed_documents,
    COUNT(DISTINCT CASE WHEN d.processing_status = 'FAILED' THEN d.id END) as failed_documents,
    COALESCE(SUM(d.file_size), 0) as total_storage_used,
    COALESCE(AVG(d.file_size), 0) as avg_file_size,
    COUNT(DISTINCT j.id) as total_jobs,
    COUNT(DISTINCT ak.id) as total_api_keys,
    COUNT(DISTINCT CASE WHEN ak.is_active = TRUE THEN ak.id END) as active_api_keys,
    MAX(d.created_at) as last_upload_date
FROM users u
         LEFT JOIN pdf_documents d ON u.id = d.user_id
         LEFT JOIN pdf_processing_jobs j ON d.id = j.document_id
         LEFT JOIN api_keys ak ON u.id = ak.user_id
GROUP BY u.id, u.username, u.email, u.created_at;

-- Create a view for job queue status
CREATE VIEW job_queue_status AS
SELECT
    job_type,
    status,
    priority,
    COUNT(*) as job_count,
    MIN(queued_at) as oldest_job,
    MAX(queued_at) as newest_job,
    AVG(TIMESTAMPDIFF(MINUTE, queued_at, COALESCE(completed_at, NOW()))) as avg_processing_time_minutes
FROM pdf_processing_jobs
GROUP BY job_type, status, priority
ORDER BY job_type, status, priority;

-- Add triggers for automatic updates
DELIMITER //

-- Trigger to update document stats when jobs complete
CREATE TRIGGER update_document_on_job_completion
    AFTER UPDATE ON pdf_processing_jobs
    FOR EACH ROW
BEGIN
    IF OLD.status != NEW.status AND NEW.status = 'COMPLETED' THEN
        -- Update document status if all jobs are completed
    UPDATE pdf_documents
    SET processing_status = 'COMPLETED',
        processing_completed_at = NOW()
    WHERE id = NEW.document_id
      AND processing_status = 'PROCESSING'
      AND NOT EXISTS (
        SELECT 1 FROM pdf_processing_jobs
        WHERE document_id = NEW.document_id
          AND status IN ('QUEUED', 'PROCESSING')
    );
END IF;

IF OLD.status != NEW.status AND NEW.status = 'FAILED' THEN
        -- Update document status if any critical job fails
UPDATE pdf_documents
SET processing_status = 'FAILED',
    processing_error = NEW.error_message
WHERE id = NEW.document_id
  AND processing_status IN ('PENDING', 'PROCESSING')
  AND NEW.job_type IN ('EXTRACT_TEXT', 'EXTRACT_METADATA');
END IF;
END//

-- Trigger to update API key usage
CREATE TRIGGER update_api_key_usage
    AFTER INSERT ON audit_logs
    FOR EACH ROW
BEGIN
    IF NEW.api_key_id IS NOT NULL THEN
    UPDATE api_keys
    SET last_used_at = NOW(),
        usage_count = usage_count + 1
    WHERE id = NEW.api_key_id;
END IF;
END//

-- Trigger to automatically set document processing status
CREATE TRIGGER set_document_processing_status
    AFTER INSERT ON pdf_processing_jobs
    FOR EACH ROW
BEGIN
    UPDATE pdf_documents
    SET processing_status = 'PROCESSING',
        processing_started_at = COALESCE(processing_started_at, NOW())
    WHERE id = NEW.document_id
      AND processing_status = 'PENDING';
END//

DELIMITER ;

-- Create stored procedures for common operations
DELIMITER //

-- Procedure to clean up old audit logs
CREATE PROCEDURE CleanupAuditLogs(IN days_to_keep INT)
BEGIN
DELETE FROM audit_logs
WHERE created_at < DATE_SUB(NOW(), INTERVAL days_to_keep DAY);

SELECT ROW_COUNT() as deleted_rows;
END//

-- Procedure to get user document summary
CREATE PROCEDURE GetUserDocumentSummary(IN user_id_param BIGINT)
BEGIN
SELECT
    COUNT(*) as total_documents,
    COUNT(CASE WHEN processing_status = 'COMPLETED' THEN 1 END) as completed,
    COUNT(CASE WHEN processing_status = 'PROCESSING' THEN 1 END) as processing,
    COUNT(CASE WHEN processing_status = 'FAILED' THEN 1 END) as failed,
    COUNT(CASE WHEN processing_status = 'PENDING' THEN 1 END) as pending,
    SUM(file_size) as total_size,
    SUM(page_count) as total_pages,
    MAX(created_at) as last_upload
FROM pdf_documents
WHERE user_id = user_id_param;
END//

-- Procedure to get next job for processing
CREATE PROCEDURE GetNextJobForProcessing(IN job_types JSON, IN processing_node_name VARCHAR(100))
BEGIN
    DECLARE job_id BIGINT;
    DECLARE EXIT HANDLER FOR SQLEXCEPTION ROLLBACK;

START TRANSACTION;

SELECT id INTO job_id
FROM pdf_processing_jobs
WHERE status = 'QUEUED'
  AND (job_types IS NULL OR JSON_CONTAINS(job_types, CONCAT('"', job_type, '"')))
ORDER BY priority ASC, queued_at ASC
    LIMIT 1
    FOR UPDATE;

IF job_id IS NOT NULL THEN
UPDATE pdf_processing_jobs
SET status = 'PROCESSING',
    started_at = NOW(),
    processing_node = processing_node_name
WHERE id = job_id;

SELECT * FROM pdf_processing_jobs WHERE id = job_id;
ELSE
SELECT NULL as id;
END IF;

COMMIT;
END//

DELIMITER ;