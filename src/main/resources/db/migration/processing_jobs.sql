CREATE TABLE processing_jobs
(
    id                   VARCHAR(255) NOT NULL,
    original_file_key    VARCHAR(255) NULL,
    transformed_file_key VARCHAR(255) NULL,
    status               VARCHAR(255) NULL,
    format               VARCHAR(255) NULL,
    created_at           datetime NULL,
    completed_at         datetime NULL,
    error_message        VARCHAR(255) NULL,
    CONSTRAINT pk_processing_jobs PRIMARY KEY (id)
);