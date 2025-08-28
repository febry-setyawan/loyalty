package com.example.loyalty.points.interfaces;

import com.example.loyalty.points.application.dto.EarnPointsDTO;
import com.example.loyalty.points.application.dto.PointBalanceDTO;
import com.example.loyalty.points.application.usecases.EarnPointsUseCase;
import com.example.loyalty.points.application.usecases.GetBalanceUseCase;
import com.example.loyalty.points.domain.entities.EarningRule;
import com.example.loyalty.points.domain.repositories.EarningRuleRepository;
import com.example.loyalty.common.response.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

/**
 * REST Controller for point operations
 */
@RestController
@RequestMapping("/api/v1/points")
public class PointController {
    
    private final EarnPointsUseCase earnPointsUseCase;
    private final GetBalanceUseCase getBalanceUseCase;
    private final EarningRuleRepository earningRuleRepository;

    public PointController(EarnPointsUseCase earnPointsUseCase,
                          GetBalanceUseCase getBalanceUseCase,
                          EarningRuleRepository earningRuleRepository) {
        this.earnPointsUseCase = earnPointsUseCase;
        this.getBalanceUseCase = getBalanceUseCase;
        this.earningRuleRepository = earningRuleRepository;
    }

    /**
     * POST /api/v1/points/earn
     * Earn points based on transaction
     */
    @PostMapping("/earn")
    public ResponseEntity<ApiResponse<EarnPointsDTO.Response>> earnPoints(
            @Valid @RequestBody EarnPointsDTO.Request request) {
        
        try {
            EarnPointsDTO.Response response = earnPointsUseCase.execute(request);
            return ResponseEntity.ok(
                ApiResponse.success(response, "Points earned successfully")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_REQUEST", "Invalid request: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("INTERNAL_ERROR", "Failed to earn points: " + e.getMessage())
            );
        }
    }

    /**
     * GET /api/v1/points/balance/{userId}
     * Get user point balance
     */
    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<PointBalanceDTO.Response>> getBalance(
            @PathVariable UUID userId) {
        
        try {
            PointBalanceDTO.Response response = getBalanceUseCase.execute(userId);
            return ResponseEntity.ok(
                ApiResponse.success(response, "Balance retrieved successfully")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_REQUEST", "Invalid request: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("INTERNAL_ERROR", "Failed to get balance: " + e.getMessage())
            );
        }
    }

    /**
     * GET /api/v1/points/earning-rules
     * Get current earning rules
     */
    @GetMapping("/earning-rules")
    public ResponseEntity<ApiResponse<List<EarningRule>>> getEarningRules() {
        
        try {
            List<EarningRule> rules = earningRuleRepository.findActiveRules();
            return ResponseEntity.ok(
                ApiResponse.success(rules, "Earning rules retrieved successfully")
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("INTERNAL_ERROR", "Failed to get earning rules: " + e.getMessage())
            );
        }
    }

    /**
     * POST /api/v1/points/referral
     * Award referral points
     */
    @PostMapping("/referral")
    public ResponseEntity<ApiResponse<EarnPointsDTO.Response>> awardReferralPoints(
            @RequestBody ReferralRequest referralRequest) {
        
        try {
            // Map ReferralRequest to EarnPointsDTO.Request
            EarnPointsDTO.Request earnRequest = new EarnPointsDTO.Request();
            earnRequest.setUserId(referralRequest.getUserId());
            earnRequest.setEarningType("REFERRAL");
            earnRequest.setDescription("Referral bonus points");
            earnRequest.setPoints(referralRequest.getPoints());
            // Optionally, if EarnPointsDTO.Request has a referrerId field, set it as well
            // earnRequest.setReferrerId(referralRequest.getReferrerId());

            EarnPointsDTO.Response response = earnPointsUseCase.execute(earnRequest);
            return ResponseEntity.ok(
                ApiResponse.success(response, "Referral points awarded successfully")
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                ApiResponse.error("INVALID_REQUEST", "Invalid request: " + e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponse.error("INTERNAL_ERROR", "Failed to award referral points: " + e.getMessage())
            );
        }
    }
}