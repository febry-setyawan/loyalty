-- Initial schema for Point Service
-- Version: V1__Create_point_tables.sql

-- Points balance table
CREATE TABLE points_balance (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    total_points BIGINT NOT NULL DEFAULT 0,
    available_points BIGINT NOT NULL DEFAULT 0,
    pending_points BIGINT NOT NULL DEFAULT 0,
    lifetime_earned BIGINT NOT NULL DEFAULT 0,
    lifetime_spent BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

-- Points transactions table
CREATE TABLE points_transactions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    transaction_type VARCHAR(20) NOT NULL, -- EARN, SPEND, EXPIRE, REFUND, BONUS
    points_amount BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    source VARCHAR(50) NOT NULL, -- PURCHASE, REFERRAL, BONUS, REDEMPTION, etc.
    source_id UUID, -- Reference to purchase, referral, etc.
    description TEXT,
    metadata JSONB,
    expiry_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING' -- PENDING, PROCESSED, FAILED, CANCELLED
);

-- Points expiry tracking
CREATE TABLE points_expiry (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    points_amount BIGINT NOT NULL,
    earned_date TIMESTAMP WITH TIME ZONE NOT NULL,
    expiry_date TIMESTAMP WITH TIME ZONE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- ACTIVE, EXPIRED, CONSUMED
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Points earning rules
CREATE TABLE earning_rules (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    rule_type VARCHAR(30) NOT NULL, -- PURCHASE, REFERRAL, SIGNUP, REVIEW, etc.
    points_per_unit DECIMAL(10,2) NOT NULL,
    unit_type VARCHAR(20) NOT NULL, -- DOLLAR, ITEM, ACTION
    multiplier DECIMAL(5,2) NOT NULL DEFAULT 1.0,
    min_amount DECIMAL(10,2),
    max_points BIGINT,
    tier_restrictions JSONB, -- Which tiers can use this rule
    start_date TIMESTAMP WITH TIME ZONE NOT NULL,
    end_date TIMESTAMP WITH TIME ZONE,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE UNIQUE INDEX idx_points_balance_user_id ON points_balance(user_id);
CREATE INDEX idx_points_transactions_user_id ON points_transactions(user_id);
CREATE INDEX idx_points_transactions_type ON points_transactions(transaction_type);
CREATE INDEX idx_points_transactions_status ON points_transactions(status);
CREATE INDEX idx_points_transactions_created_at ON points_transactions(created_at);
CREATE INDEX idx_points_expiry_user_id ON points_expiry(user_id);
CREATE INDEX idx_points_expiry_expiry_date ON points_expiry(expiry_date);
CREATE INDEX idx_points_expiry_status ON points_expiry(status);
CREATE INDEX idx_earning_rules_active ON earning_rules(is_active, start_date, end_date);

-- Trigger function to update updated_at automatically
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to update updated_at automatically
CREATE TRIGGER update_points_balance_updated_at BEFORE UPDATE ON points_balance 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_points_expiry_updated_at BEFORE UPDATE ON points_expiry 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_earning_rules_updated_at BEFORE UPDATE ON earning_rules 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();