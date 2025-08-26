-- Sample earning rules data
-- Insert default earning rules for testing and initial setup

INSERT INTO earning_rules (id, name, rule_type, points_per_unit, unit_type, multiplier, min_amount, max_points, tier_restrictions, start_date, end_date, is_active, created_at, updated_at) VALUES
-- Default purchase rule: 1 point per Rp 1,000
('550e8400-e29b-41d4-a716-446655440000', 'Default Purchase Rule', 'PURCHASE', 1.0, 'DOLLAR', 1.0, 1000.0, NULL, NULL, NOW(), NULL, true, NOW(), NOW()),

-- Referral bonus rule: 500 points per referral
('550e8400-e29b-41d4-a716-446655440001', 'Referral Bonus Rule', 'REFERRAL', 500.0, 'ACTION', 1.0, NULL, NULL, NULL, NOW(), NULL, true, NOW(), NOW()),

-- Signup bonus rule: 1000 points for new members
('550e8400-e29b-41d4-a716-446655440002', 'Signup Bonus Rule', 'SIGNUP', 1000.0, 'ACTION', 1.0, NULL, NULL, NULL, NOW(), NULL, true, NOW(), NOW()),

-- Review bonus rule: 50 points per review
('550e8400-e29b-41d4-a716-446655440003', 'Review Bonus Rule', 'REVIEW', 50.0, 'ACTION', 1.0, NULL, 500, NULL, NOW(), NULL, true, NOW(), NOW()),

-- Special event multiplier: 2x points for limited time
('550e8400-e29b-41d4-a716-446655440004', 'Double Points Event', 'BONUS_EVENT', 1.0, 'DOLLAR', 2.0, 1000.0, NULL, NULL, NOW(), NOW() + INTERVAL '30 days', true, NOW(), NOW());