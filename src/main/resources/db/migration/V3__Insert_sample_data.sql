-- V3__Insert_sample_data.sql
-- Sample data for development and testing

-- Insert sample users
INSERT INTO users (username, email, password_hash, first_name, last_name) VALUES
                                                                              ('admin', 'admin@pdfservice.com', '$2a$10$N.zmdr9k7OiMvDWmSnpvUOvYQGYzwsEVfZhJPkrJNLXqVJ9XF3.S2', 'Admin', 'User'),
                                                                              ('testuser', 'test@example.com', '$2a$10$N.zmdr9k7OiMvDWmSnpvUOvYQGYzwsEVfZhJPkrJNLXqVJ9XF3.S2', 'Test', 'User'),
                                                                              ('developer', 'dev@pdfservice.com', '$2a$10$N.zmdr9k7OiMvDWmSnpvUOvYQGYzwsEVfZhJPkrJNLXqVJ9XF3.S2', 'Developer', 'User');

-- Insert sample API keys
INSERT INTO api_keys (user_id, key_name, api_key, key_prefix, permissions, rate_limit_per_minute, rate_limit_per_hour, rate_limit_per_day) VALUES
                                                                                                                                               (1, 'Admin Key', 'a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2', 'a1b2c3d4',
                                                                                                                                                '["UPLOAD", "PROCESS", "DOWNLOAD", "DELETE", "ADMIN"]', 1000, 10000, 50000),
                                                                                                                                               (2, 'Test Key', 'b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2g3', 'b2c3d4e5',
                                                                                                                                                '["UPLOAD", "PROCESS", "DOWNLOAD"]', 60, 1000, 10000),
                                                                                                                                               (3, 'Dev Key', 'c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2w3x4y5z6a7b8c9d0e1f2g3h4', 'c3d4e5f6',
                                                                                                                                                '["UPLOAD", "PROCESS", "DOWNLOAD", "DELETE"]', 120, 2000, 15000);

-- Insert sample PDF documents
INSERT INTO pdf_documents (user_id, original_filename, file_size, file_hash, storage_path, storage_bucket,
                           title, page_count, processing_status, uploaded_at) VALUES
                                                                                  (2, 'sample_document.pdf', 1024000, 'abc123def456ghi789jkl012mno345pqr678stu901vwx234yz',
                                                                                   'documents/2024/01/sample_document.pdf', 'pdf-processing-bucket', 'Sample Document', 10, 'COMPLETED', NOW()),
                                                                                  (2, 'test_report.pdf', 2048000, 'def456ghi789jkl012mno345pqr678stu901vwx234yz567890abcd',
                                                                                   'documents/2024/01/test_report.pdf', 'pdf-processing-bucket', 'Test Report', 25, 'PROCESSING', NOW()),
                                                                                  (3, 'development_guide.pdf', 5120000, 'ghi789jkl012mno345pqr678stu901vwx234yz567890abcd',
                                                                                   'documents/2024/01/development_guide.pdf', 'pdf-processing-bucket', 'Development Guide', 50, 'COMPLETED', NOW());

-- Insert sample processing jobs
INSERT INTO pdf_processing_jobs (document_id, job_type, status, priority, job_parameters, queued_at) VALUES
                                                                                                         (1, 'EXTRACT_TEXT', 'COMPLETED', 1, '{"extract_formatting": true, "language": "en"}', NOW()),
                                                                                                         (1, 'EXTRACT_IMAGES', 'COMPLETED', 2, '{"min_size": 100, "formats": ["JPEG", "PNG"]}', NOW()),
                                                                                                         (1, 'EXTRACT_METADATA', 'COMPLETED', 1, '{}', NOW()),
                                                                                                         (2, 'EXTRACT_TEXT', 'PROCESSING', 1, '{"extract_formatting": true, "language": "en"}', NOW()),
                                                                                                         (2, 'EXTRACT_METADATA', 'QUEUED', 1, '{}', NOW()),
                                                                                                         (3, 'EXTRACT_TEXT', 'COMPLETED', 1, '{"extract_formatting": true, "language": "en"}', NOW()),
                                                                                                         (3, 'EXTRACT_IMAGES', 'COMPLETED', 2, '{"min_size": 50, "formats": ["JPEG", "PNG"]}', NOW());

-- Insert sample extracted text
INSERT INTO extracted_text (document_id, page_number, text_content, text_length, word_count, character_count) VALUES
                                                                                                                  (1, 1, 'This is the first page of the sample document. It contains some sample text for testing purposes.', 96, 18, 96),
                                                                                                                  (1, 2, 'This is the second page with more sample content. PDF processing is an important feature.', 88, 16, 88),
                                                                                                                  (1, 3, 'Third page continues with additional text content for comprehensive testing of the system.', 89, 15, 89),
                                                                                                                  (3, 1, 'Development Guide - Chapter 1: Introduction to PDF Processing Service', 67, 11, 67),
                                                                                                                  (3, 2, 'Chapter 2: Architecture Overview. The service uses Spring Boot and MySQL for data storage.', 90, 16, 90);

-- Insert sample extracted images
INSERT INTO extracted_images (document_id, page_number, image_index, image_format, image_width, image_height,
                              image_size, storage_path, storage_bucket) VALUES
                                                                            (1, 1, 1, 'JPEG', 800, 600, 45000, 'images/2024/01/doc1_page1_img1.jpg', 'pdf-processing-bucket'),
                                                                            (1, 5, 1, 'PNG', 1024, 768, 78000, 'images/2024/01/doc1_page5_img1.png', 'pdf-processing-bucket'),
                                                                            (3, 3, 1, 'JPEG', 1200, 900, 120000, 'images/2024/01/doc3_page3_img1.jpg', 'pdf-processing-bucket'),
                                                                            (3, 3, 2, 'PNG', 600, 400, 35000, 'images/2024/01/doc3_page3_img2.png', 'pdf-processing-bucket');

-- Insert sample audit logs
INSERT INTO audit_logs (user_id, entity_type, entity_id, action, ip_address, user_agent, api_key_id) VALUES
                                                                                                         (2, 'DOCUMENT', 1, 'UPLOAD', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 2),
                                                                                                         (2, 'JOB', 1, 'CREATE', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 2),
                                                                                                         (2, 'DOCUMENT', 2, 'UPLOAD', '192.168.1.100', 'Mozilla/5.0 (Windows NT 10.0; Win64; x64)', 2),
                                                                                                         (3, 'DOCUMENT', 3, 'UPLOAD', '192.168.1.101', 'Mozilla/5.0 (Mac OS X 10.15; rv:91.0)', 3),
                                                                                                         (1, 'USER', 2, 'VIEW', '192.168.1.1', 'Admin Dashboard', 1);

-- Update system configuration with environment-specific values
UPDATE system_config SET config_value = '50' WHERE config_key = 'max_file_size_mb';
UPDATE system_config SET config_value = '500' WHERE config_key = 'max_files_per_user';
UPDATE system_config SET config_value = 'true' WHERE config_key = 'enable_ocr';