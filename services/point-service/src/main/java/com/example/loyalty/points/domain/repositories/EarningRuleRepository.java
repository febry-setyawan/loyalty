package com.example.loyalty.points.domain.repositories;

import com.example.loyalty.points.domain.entities.EarningRule;
import com.example.loyalty.points.domain.enums.EarningType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository interface for EarningRule domain entity
 */
public interface EarningRuleRepository {
    
    EarningRule save(EarningRule rule);
    
    Optional<EarningRule> findById(UUID id);
    
    List<EarningRule> findAll();
    
    List<EarningRule> findActiveRules();
    
    List<EarningRule> findByRuleType(EarningType ruleType);
    
    void deleteById(UUID id);
}