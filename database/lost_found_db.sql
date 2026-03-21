-- =============================================
-- Lost & Found Portal - Database Schema
-- =============================================
-- Run this script to create the database manually
-- (or let Spring Boot's JPA auto-create it via ddl-auto=update)
-- =============================================

CREATE DATABASE IF NOT EXISTS lost_found_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE lost_found_db;

-- Users Table
CREATE TABLE IF NOT EXISTS users (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    name        VARCHAR(100)    NOT NULL,
    email       VARCHAR(150)    UNIQUE NOT NULL,
    password    VARCHAR(255)    NOT NULL,
    phone       VARCHAR(20),
    role        ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
    created_at  DATETIME        DEFAULT CURRENT_TIMESTAMP
);

-- Items Table
CREATE TABLE IF NOT EXISTS items (
    id              BIGINT AUTO_INCREMENT PRIMARY KEY,
    title           VARCHAR(200)    NOT NULL,
    description     TEXT,
    type            ENUM('LOST', 'FOUND')   NOT NULL,
    status          ENUM('ACTIVE', 'RESOLVED', 'CLOSED') NOT NULL DEFAULT 'ACTIVE',
    category        VARCHAR(100),
    location        VARCHAR(255),
    date_lost_found DATE,
    image_url       VARCHAR(500),
    contact_info    VARCHAR(255),
    reported_by     BIGINT          NOT NULL,
    created_at      DATETIME        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (reported_by) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_type (type),
    INDEX idx_status (status),
    INDEX idx_category (category),
    INDEX idx_reported_by (reported_by),
    FULLTEXT INDEX ft_title_desc (title, description)
);

-- Messages Table
CREATE TABLE IF NOT EXISTS messages (
    id          BIGINT AUTO_INCREMENT PRIMARY KEY,
    content     TEXT            NOT NULL,
    sender_id   BIGINT          NOT NULL,
    receiver_id BIGINT          NOT NULL,
    item_id     BIGINT,
    is_read     TINYINT(1)      NOT NULL DEFAULT 0,
    sent_at     DATETIME        DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (sender_id)   REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (item_id)     REFERENCES items(id) ON DELETE SET NULL,
    INDEX idx_sender (sender_id),
    INDEX idx_receiver (receiver_id),
    INDEX idx_item (item_id)
);

-- =============================================
-- Sample Data (optional - for testing)
-- =============================================
INSERT INTO users (name, email, password, role)
VALUES ('Admin User', 'admin@lostfound.com',
        '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', -- password: admin123
        'ADMIN');
