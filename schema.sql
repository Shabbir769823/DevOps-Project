-- MySQL Database Schema
-- Project: Automated Java Web App Deployment using CI/CD Pipeline

CREATE DATABASE IF NOT EXISTS devops_db;
USE devops_db;

-- 1. Create Roles Table
CREATE TABLE IF NOT EXISTS roles (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

-- 2. Create Users Table
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);

-- 3. Create User Roles Mapping Table
CREATE TABLE IF NOT EXISTS user_roles (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    PRIMARY KEY (user_id, role_id),
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES roles(id) ON DELETE CASCADE
);

-- 4. Create Employees Table
CREATE TABLE IF NOT EXISTS employees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    department VARCHAR(100) NOT NULL,
    designation VARCHAR(100) NOT NULL,
    salary DOUBLE NOT NULL
);

-- 4a. Create Tasks Table
CREATE TABLE IF NOT EXISTS tasks (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    assignee_id BIGINT NOT NULL,
    creator_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (assignee_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (creator_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 4b. Create Notifications Table
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    message VARCHAR(255) NOT NULL,
    read_status BOOLEAN DEFAULT FALSE,
    recipient_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (recipient_id) REFERENCES users(id) ON DELETE CASCADE
);

-- 5. Seed Data
-- Insert Roles (if not exist)
INSERT INTO roles (id, name) VALUES (1, 'ROLE_USER') ON DUPLICATE KEY UPDATE name=name;
INSERT INTO roles (id, name) VALUES (2, 'ROLE_ADMIN') ON DUPLICATE KEY UPDATE name=name;

-- Insert Default Users (Passwords are BCrypt hashed: 'admin123' and 'user123' respectively)
-- Hash for 'admin123': $2a$10$8.UnVuG9HHgffUDalk8qCOuyzryEXoMBR6apFA.H2HeH175.7mWJe
-- Hash for 'user123': $2a$10$Y1wH/h3aLhGkEqJexmY0K.9vAec1p5w2M79d86uB/f1XJj4kC.uDe
INSERT INTO users (id, username, password, email, first_name, last_name, enabled)
VALUES (1, 'admin', '$2a$10$8.UnVuG9HHgffUDalk8qCOuyzryEXoMBR6apFA.H2HeH175.7mWJe', 'admin@devops.com', 'System', 'Admin', TRUE)
ON DUPLICATE KEY UPDATE username=username;

INSERT INTO users (id, username, password, email, first_name, last_name, enabled)
VALUES (2, 'user', '$2a$10$Y1wH/h3aLhGkEqJexmY0K.9vAec1p5w2M79d86uB/f1XJj4kC.uDe', 'user@devops.com', 'Regular', 'User', TRUE)
ON DUPLICATE KEY UPDATE username=username;

-- Map Users to Roles
INSERT INTO user_roles (user_id, role_id) VALUES (1, 1) ON DUPLICATE KEY UPDATE user_id=user_id;
INSERT INTO user_roles (user_id, role_id) VALUES (1, 2) ON DUPLICATE KEY UPDATE user_id=user_id;
INSERT INTO user_roles (user_id, role_id) VALUES (2, 1) ON DUPLICATE KEY UPDATE user_id=user_id;

-- Seed Employee Data
INSERT INTO employees (id, first_name, last_name, email, department, designation, salary)
VALUES 
(1, 'Amit', 'Sharma', 'amit.sharma@company.com', 'Engineering', 'Senior Developer', 85000.00),
(2, 'Priya', 'Patel', 'priya.patel@company.com', 'Human Resources', 'HR Manager', 70000.00),
(3, 'Rahul', 'Verma', 'rahul.verma@company.com', 'Product Management', 'Product Owner', 95000.00),
(4, 'Sneha', 'Reddy', 'sneha.reddy@company.com', 'Operations', 'DevOps Specialist', 90000.00),
(5, 'Vikram', 'Singh', 'vikram.singh@company.com', 'Marketing', 'Digital Specialist', 60000.00)
ON DUPLICATE KEY UPDATE email=email;
