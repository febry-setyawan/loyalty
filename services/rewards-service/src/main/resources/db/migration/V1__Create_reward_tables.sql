-- Initial schema for Rewards Service  
-- Version: V1__Create_reward_tables.sql

-- Rewards catalog table
CREATE TABLE rewards_catalog (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(200) NOT NULL,
    description TEXT,
    category VARCHAR(50) NOT NULL,
    subcategory VARCHAR(50),
    points_cost BIGINT NOT NULL,
    original_price DECIMAL(10,2),
    discount_percentage DECIMAL(5,2),
    brand VARCHAR(100),
    image_urls JSONB,
    terms_and_conditions TEXT,
    tier_restrictions JSONB, -- Which tiers can redeem this reward
    availability_count INTEGER, -- NULL means unlimited
    max_per_user INTEGER DEFAULT 1,
    tags JSONB,
    metadata JSONB,
    is_active BOOLEAN NOT NULL DEFAULT true,
    is_featured BOOLEAN NOT NULL DEFAULT false,
    start_date TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    end_date TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version INTEGER NOT NULL DEFAULT 0
);

-- Reward redemptions table
CREATE TABLE reward_redemptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL,
    reward_id UUID NOT NULL REFERENCES rewards_catalog(id),
    points_used BIGINT NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING', -- PENDING, CONFIRMED, DELIVERED, CANCELLED, EXPIRED
    redemption_code VARCHAR(50),
    delivery_method VARCHAR(30), -- DIGITAL, PHYSICAL, EMAIL
    delivery_address JSONB,
    delivery_status VARCHAR(20), -- PENDING, SHIPPED, DELIVERED, FAILED
    tracking_number VARCHAR(100),
    expiry_date TIMESTAMP WITH TIME ZONE,
    redeemed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    delivered_at TIMESTAMP WITH TIME ZONE,
    cancelled_at TIMESTAMP WITH TIME ZONE,
    cancellation_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Reward inventory tracking
CREATE TABLE reward_inventory (
    reward_id UUID PRIMARY KEY REFERENCES rewards_catalog(id),
    total_count INTEGER NOT NULL DEFAULT 0,
    available_count INTEGER NOT NULL DEFAULT 0,
    reserved_count INTEGER NOT NULL DEFAULT 0,
    redeemed_count INTEGER NOT NULL DEFAULT 0,
    low_stock_threshold INTEGER DEFAULT 10,
    restock_alert_sent BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Reward categories lookup
CREATE TABLE reward_categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100) NOT NULL,
    description TEXT,
    icon_url VARCHAR(500),
    sort_order INTEGER NOT NULL DEFAULT 0,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- User reward preferences
CREATE TABLE user_reward_preferences (
    user_id UUID PRIMARY KEY,
    preferred_categories JSONB,
    notification_preferences JSONB,
    delivery_address JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Indexes for performance
CREATE INDEX idx_rewards_catalog_category ON rewards_catalog(category);
CREATE INDEX idx_rewards_catalog_active ON rewards_catalog(is_active);
CREATE INDEX idx_rewards_catalog_featured ON rewards_catalog(is_featured);
CREATE INDEX idx_rewards_catalog_points_cost ON rewards_catalog(points_cost);
CREATE INDEX idx_rewards_catalog_start_end_date ON rewards_catalog(start_date, end_date);
CREATE INDEX idx_reward_redemptions_user_id ON reward_redemptions(user_id);
CREATE INDEX idx_reward_redemptions_status ON reward_redemptions(status);
CREATE INDEX idx_reward_redemptions_redeemed_at ON reward_redemptions(redeemed_at);
CREATE INDEX idx_reward_inventory_available_count ON reward_inventory(available_count);

-- Trigger function to update updated_at automatically
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Trigger to update updated_at automatically
CREATE TRIGGER update_rewards_catalog_updated_at BEFORE UPDATE ON rewards_catalog 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reward_redemptions_updated_at BEFORE UPDATE ON reward_redemptions 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reward_inventory_updated_at BEFORE UPDATE ON reward_inventory 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_reward_categories_updated_at BEFORE UPDATE ON reward_categories 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_user_reward_preferences_updated_at BEFORE UPDATE ON user_reward_preferences 
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();