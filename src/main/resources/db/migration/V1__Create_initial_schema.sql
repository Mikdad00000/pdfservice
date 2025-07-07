-- V1__Create_initial_schema.sql
-- Initial database schema for PDF processing service
CREATE DATABASE if not exists pdfservice;

USE pdfservice;
-- Create users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       username VARCHAR(50) NOT NULL UNIQUE,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password_hash VARCHAR(255) NOT NULL,
                       first_name VARCHAR(100),
                       last_name VARCHAR(100),
                       is_active BOOLEAN DEFAULT TRUE,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                       INDEX idx_users_username (username),
                       INDEX idx_users_email (email),
                       INDEX idx_users_active (is_active)
);

-- Create PDF documents table
CREATE TABLE pdf_documents (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               user_id BIGINT NOT NULL,
                               original_filename VARCHAR(255) NOT NULL,
                               file_size BIGINT NOT NULL,
                               mime_type VARCHAR(100) DEFAULT 'application/pdf',
                               file_hash VARCHAR(64) NOT NULL, -- SHA-256 hash
                               storage_path VARCHAR(500) NOT NULL, -- S3 key or local path
                               storage_bucket VARCHAR(100), -- S3 bucket name

    -- PDF metadata
                               title VARCHAR(500),
                               author VARCHAR(255),
                               subject VARCHAR(500),
                               keywords TEXT,
                               creator VARCHAR(255),
                               producer VARCHAR(255),
                               creation_date TIMESTAMP NULL,
                               modification_date TIMESTAMP NULL,

    -- Document stats
                               page_count INT DEFAULT 0,
                               is_encrypted BOOLEAN DEFAULT FALSE,
                               is_signed BOOLEAN DEFAULT FALSE,
                               pdf_version VARCHAR(10),

    -- Processing status
                               processing_status ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'DELETED') DEFAULT 'PENDING',
                               processing_error TEXT,
                               processing_started_at TIMESTAMP NULL,
                               processing_completed_at TIMESTAMP NULL,

    -- Audit fields
                               uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                               INDEX idx_pdf_user_id (user_id),
                               INDEX idx_pdf_status (processing_status),
                               INDEX idx_pdf_hash (file_hash),
                               INDEX idx_pdf_created (created_at),
                               INDEX idx_pdf_user_status (user_id, processing_status)
);

-- Create PDF processing jobs table
CREATE TABLE pdf_processing_jobs (
                                     id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                     document_id BIGINT NOT NULL,
                                     job_type ENUM('EXTRACT_TEXT', 'EXTRACT_IMAGES', 'EXTRACT_METADATA', 'CONVERT_TO_IMAGE', 'MERGE', 'SPLIT', 'COMPRESS', 'ENCRYPT', 'DECRYPT') NOT NULL,
                                     status ENUM('QUEUED', 'PROCESSING', 'COMPLETED', 'FAILED', 'CANCELLED') DEFAULT 'QUEUED',
                                     priority INT DEFAULT 5, -- 1 = highest, 10 = lowest

    -- Job parameters (JSON format)
                                     job_parameters JSON,

    -- Results
                                     result_data JSON,
                                     output_file_path VARCHAR(500),
                                     output_file_size BIGINT,

    -- Processing details
                                     processing_node VARCHAR(100), -- Which server/instance processed this
                                     error_message TEXT,
                                     error_details JSON,

    -- Timing
                                     queued_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     started_at TIMESTAMP NULL,
                                     completed_at TIMESTAMP NULL,

    -- Audit
                                     created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                     updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                     FOREIGN KEY (document_id) REFERENCES pdf_documents(id) ON DELETE CASCADE,

                                     INDEX idx_jobs_document_id (document_id),
                                     INDEX idx_jobs_status (status),
                                     INDEX idx_jobs_type (job_type),
                                     INDEX idx_jobs_priority (priority),
                                     INDEX idx_jobs_queued (queued_at),
                                     INDEX idx_jobs_status_priority (status, priority)
);

-- Create extracted text table
CREATE TABLE extracted_text (
                                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                document_id BIGINT NOT NULL,
                                page_number INT NOT NULL,
                                text_content LONGTEXT NOT NULL,
                                text_length INT NOT NULL,
                                extraction_method VARCHAR(50) DEFAULT 'PDFBOX',
                                confidence_score DECIMAL(3,2), -- 0.00 to 1.00

    -- Text analysis
                                language_detected VARCHAR(10),
                                word_count INT,
                                character_count INT,

    -- Positioning data (JSON format for bounding boxes, etc.)
                                position_data JSON,

                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                                FOREIGN KEY (document_id) REFERENCES pdf_documents(id) ON DELETE CASCADE,

                                INDEX idx_text_document_id (document_id),
                                INDEX idx_text_page (document_id, page_number),
                                INDEX idx_text_length (text_length),

    -- Full-text search index
                                FULLTEXT idx_text_content (text_content)
);

-- Create extracted images table
CREATE TABLE extracted_images (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  document_id BIGINT NOT NULL,
                                  page_number INT NOT NULL,
                                  image_index INT NOT NULL, -- Index of image on the page
                                  image_format VARCHAR(10) NOT NULL, -- JPEG, PNG, etc.
                                  image_width INT,
                                  image_height INT,
                                  image_size BIGINT,

    -- Storage information
                                  storage_path VARCHAR(500) NOT NULL,
                                  storage_bucket VARCHAR(100),

    -- Image metadata
                                  dpi INT,
                                  color_space VARCHAR(20),
                                  compression VARCHAR(50),

    -- Positioning in PDF
                                  x_position DECIMAL(10,2),
                                  y_position DECIMAL(10,2),
                                  width DECIMAL(10,2),
                                  height DECIMAL(10,2),

                                  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                                  FOREIGN KEY (document_id) REFERENCES pdf_documents(id) ON DELETE CASCADE,

                                  INDEX idx_images_document_id (document_id),
                                  INDEX idx_images_page (document_id, page_number),
                                  INDEX idx_images_format (image_format)
);

-- Create API keys table for authentication
CREATE TABLE api_keys (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          user_id BIGINT NOT NULL,
                          key_name VARCHAR(100) NOT NULL,
                          api_key VARCHAR(64) NOT NULL UNIQUE, -- SHA-256 hash of the actual key
                          key_prefix VARCHAR(8) NOT NULL, -- First 8 chars for identification

                          permissions JSON, -- What operations this key can perform
                          rate_limit_per_minute INT DEFAULT 60,
                          rate_limit_per_hour INT DEFAULT 1000,
                          rate_limit_per_day INT DEFAULT 10000,

                          last_used_at TIMESTAMP NULL,
                          usage_count BIGINT DEFAULT 0,

                          is_active BOOLEAN DEFAULT TRUE,
                          expires_at TIMESTAMP NULL,

                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                          FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,

                          INDEX idx_apikeys_user_id (user_id),
                          INDEX idx_apikeys_key (api_key),
                          INDEX idx_apikeys_prefix (key_prefix),
                          INDEX idx_apikeys_active (is_active)
);

-- Create audit log table
CREATE TABLE audit_logs (
                            id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            user_id BIGINT,
                            entity_type VARCHAR(50) NOT NULL, -- 'USER', 'DOCUMENT', 'JOB', etc.
                            entity_id BIGINT,
                            action VARCHAR(50) NOT NULL, -- 'CREATE', 'UPDATE', 'DELETE', 'DOWNLOAD', etc.
                            old_values JSON,
                            new_values JSON,
                            ip_address VARCHAR(45),
                            user_agent TEXT,
                            api_key_id BIGINT,

                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
                            FOREIGN KEY (api_key_id) REFERENCES api_keys(id) ON DELETE SET NULL,

                            INDEX idx_audit_user_id (user_id),
                            INDEX idx_audit_entity (entity_type, entity_id),
                            INDEX idx_audit_action (action),
                            INDEX idx_audit_created (created_at)
);

-- Create system configuration table
CREATE TABLE system_config (
                               id BIGINT AUTO_INCREMENT PRIMARY KEY,
                               config_key VARCHAR(100) NOT NULL UNIQUE,
                               config_value TEXT NOT NULL,
                               config_type ENUM('STRING', 'INTEGER', 'BOOLEAN', 'JSON') DEFAULT 'STRING',
                               description TEXT,
                               is_encrypted BOOLEAN DEFAULT FALSE,

                               created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                               updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

                               INDEX idx_config_key (config_key)
);

-- Insert default system configuration
INSERT INTO system_config (config_key, config_value, config_type, description) VALUES
                                                                                   ('max_file_size_mb', '100', 'INTEGER', 'Maximum file size allowed for upload in MB'),
                                                                                   ('max_files_per_user', '1000', 'INTEGER', 'Maximum number of files per user'),
                                                                                   ('retention_days', '365', 'INTEGER', 'Number of days to retain processed files'),
                                                                                   ('processing_timeout_minutes', '30', 'INTEGER', 'Timeout for processing jobs in minutes'),
                                                                                   ('enable_ocr', 'false', 'BOOLEAN', 'Enable OCR processing for scanned PDFs'),
                                                                                   ('s3_bucket_name', 'pdf-processing-bucket', 'STRING', 'Default S3 bucket for file storage'),
                                                                                   ('supported_formats', '["pdf"]', 'JSON', 'List of supported file formats');