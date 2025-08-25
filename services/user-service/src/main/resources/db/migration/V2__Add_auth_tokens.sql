-- Add verification and password reset token columns to user_auth table
-- Version: V2__Add_auth_tokens.sql

ALTER TABLE user_auth ADD COLUMN verification_token VARCHAR(255);
ALTER TABLE user_auth ADD COLUMN verification_token_expires_at TIMESTAMP WITH TIME ZONE;
ALTER TABLE user_auth ADD COLUMN password_reset_token VARCHAR(255);
ALTER TABLE user_auth ADD COLUMN password_reset_token_expires_at TIMESTAMP WITH TIME ZONE;

-- Add indexes for token lookups
CREATE INDEX idx_user_auth_verification_token ON user_auth(verification_token);
CREATE INDEX idx_user_auth_password_reset_token ON user_auth(password_reset_token);