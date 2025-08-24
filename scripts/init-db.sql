-- Loyalty System Database Initialization Script
-- This script sets up the basic database schema for development environment

-- Create database if not exists (handled by Docker environment variables)
-- Set timezone and basic configuration
SET timezone = 'UTC';

-- Create extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- Create schemas for different services
CREATE SCHEMA IF NOT EXISTS users;
CREATE SCHEMA IF NOT EXISTS points;
CREATE SCHEMA IF NOT EXISTS rewards;
CREATE SCHEMA IF NOT EXISTS admin;
CREATE SCHEMA IF NOT EXISTS audit;

-- Grant permissions to loyalty_user
GRANT USAGE ON SCHEMA users TO loyalty_user;
GRANT USAGE ON SCHEMA points TO loyalty_user;
GRANT USAGE ON SCHEMA rewards TO loyalty_user;
GRANT USAGE ON SCHEMA admin TO loyalty_user;
GRANT USAGE ON SCHEMA audit TO loyalty_user;

GRANT CREATE ON SCHEMA users TO loyalty_user;
GRANT CREATE ON SCHEMA points TO loyalty_user;
GRANT CREATE ON SCHEMA rewards TO loyalty_user;
GRANT CREATE ON SCHEMA admin TO loyalty_user;
GRANT CREATE ON SCHEMA audit TO loyalty_user;

-- Create basic tables for health checks and service connectivity

-- Users table (minimal for service startup)
CREATE TABLE IF NOT EXISTS users.users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Points table (minimal for service startup)
CREATE TABLE IF NOT EXISTS points.point_transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    points_amount INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'PROCESSED',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Rewards table (minimal for service startup)
CREATE TABLE IF NOT EXISTS rewards.rewards (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(255) NOT NULL,
    description TEXT,
    points_required INTEGER NOT NULL,
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Admin users table (minimal for service startup)
CREATE TABLE IF NOT EXISTS admin.admin_users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(100) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) DEFAULT 'ADMIN',
    status VARCHAR(20) DEFAULT 'ACTIVE',
    created_at TIMESTAMP WITH TIME ZONE DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Audit log table
CREATE TABLE IF NOT EXISTS audit.audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    action VARCHAR(50) NOT NULL,
    user_id UUID,
    changes JSONB,
    timestamp TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

-- Grant table permissions
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA users TO loyalty_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA points TO loyalty_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA rewards TO loyalty_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA admin TO loyalty_user;
GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA audit TO loyalty_user;

GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA users TO loyalty_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA points TO loyalty_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA rewards TO loyalty_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA admin TO loyalty_user;
GRANT USAGE, SELECT ON ALL SEQUENCES IN SCHEMA audit TO loyalty_user;

-- Insert sample data for health checks
INSERT INTO users.users (email, first_name, last_name) VALUES 
    ('health.check@loyalty.com', 'Health', 'Check')
ON CONFLICT (email) DO NOTHING;

INSERT INTO rewards.rewards (name, description, points_required) VALUES 
    ('Health Check Reward', 'Test reward for service health checks', 100)
ON CONFLICT DO NOTHING;

-- Create indexes for better performance
CREATE INDEX IF NOT EXISTS idx_users_email ON users.users (email);
CREATE INDEX IF NOT EXISTS idx_users_status ON users.users (status);
CREATE INDEX IF NOT EXISTS idx_point_transactions_user_id ON points.point_transactions (user_id);
CREATE INDEX IF NOT EXISTS idx_point_transactions_created_at ON points.point_transactions (created_at);
CREATE INDEX IF NOT EXISTS idx_rewards_status ON rewards.rewards (status);
CREATE INDEX IF NOT EXISTS idx_admin_users_username ON admin.admin_users (username);
CREATE INDEX IF NOT EXISTS idx_audit_logs_entity ON audit.audit_logs (entity_type, entity_id);
CREATE INDEX IF NOT EXISTS idx_audit_logs_timestamp ON audit.audit_logs (timestamp);

-- Log successful initialization
INSERT INTO audit.audit_logs (entity_type, action, changes) VALUES 
    ('SYSTEM', 'DATABASE_INITIALIZATION', '{"status": "completed", "message": "Development database initialized successfully"}');

COMMIT;