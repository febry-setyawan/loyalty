package com.example.loyalty.points.infrastructure.persistence.repositories;

import com.example.loyalty.points.domain.entities.EarningRule;
import com.example.loyalty.points.domain.enums.EarningType;
import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * In-memory implementation of EarningRuleRepository for initial development
 * TODO: Replace with JPA implementation once earning_rules table mapping is complete
 */
@Repository
public class InMemoryEarningRuleRepository implements EarningRuleRepository {
    
    private final List<EarningRule> rules = new ArrayList<>();
    
    public InMemoryEarningRuleRepository() {
        // Initialize with default purchase earning rule: 1 point = Rp 1,000
        EarningRule purchaseRule = new EarningRule(
            "Default Purchase Rule", 
            EarningType.PURCHASE, 
            BigDecimal.ONE,  // 1 point per unit
            "DOLLAR",        // per 1000 currency units
            BigDecimal.ONE   // no multiplier
        );
        rules.add(purchaseRule);
        
        // Referral rule: 500 points
        EarningRule referralRule = new EarningRule(
            "Referral Bonus Rule",
            EarningType.REFERRAL,
            BigDecimal.valueOf(500),
            "ACTION",
            BigDecimal.ONE
        );
        rules.add(referralRule);
    }

    @Override
    public EarningRule save(EarningRule rule) {
        rules.removeIf(r -> r.getId().equals(rule.getId()));
        rules.add(rule);
        return rule;
    }

    @Override
    public Optional<EarningRule> findById(UUID id) {
        return rules.stream()
                .filter(rule -> rule.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<EarningRule> findAll() {
        return new ArrayList<>(rules);
    }

    @Override
    public List<EarningRule> findActiveRules() {
        return rules.stream()
                .filter(EarningRule::isApplicable)
                .collect(Collectors.toList());
    }

    @Override
    public List<EarningRule> findByRuleType(EarningType ruleType) {
        return rules.stream()
                .filter(rule -> rule.getRuleType().equals(ruleType))
                .filter(EarningRule::isApplicable)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(UUID id) {
        rules.removeIf(rule -> rule.getId().equals(id));
    }
}