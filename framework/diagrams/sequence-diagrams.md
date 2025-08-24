# Sequence Diagrams - Critical Business Flows

**Purpose:** Visual documentation of key business logic flows for developer implementation  
**Format:** Mermaid sequence diagrams  
**Usage:** Reference during development and code review  

---

## 1. User Registration Flow

```mermaid
sequenceDiagram
    participant Client as Web/Mobile App
    participant Gateway as API Gateway
    participant UserService as User Service
    participant Database as PostgreSQL
    participant Cache as Redis
    participant Queue as Message Queue
    participant EmailService as Email Service
    participant PointService as Point Service

    Client->>Gateway: POST /api/v1/users/register
    Gateway->>Gateway: Rate limiting check
    Gateway->>Gateway: JWT validation (optional)
    Gateway->>UserService: Forward request

    UserService->>UserService: Validate input data
    UserService->>Database: Check if email exists
    Database-->>UserService: User not found

    UserService->>UserService: Hash password
    UserService->>Database: Create user record
    Database-->>UserService: User created with ID

    UserService->>Cache: Cache user session
    Cache-->>UserService: Session cached

    UserService->>Queue: Publish user.registered event
    Queue-->>EmailService: Consume event
    EmailService->>EmailService: Send verification email
    EmailService-->>Client: Email sent

    Queue-->>PointService: Consume event
    PointService->>PointService: Create initial point balance (0)
    PointService->>Database: Save point balance
    Database-->>PointService: Balance saved

    UserService-->>Gateway: Registration successful
    Gateway-->>Client: HTTP 201 - User registered

    Note over Client,PointService: Async processes continue in background
    EmailService->>Client: Verification email delivered
```

---

## 2. Point Earning Flow

```mermaid
sequenceDiagram
    participant POS as POS System
    participant Gateway as API Gateway
    participant PointService as Point Service
    participant RuleEngine as Rule Engine
    participant Database as PostgreSQL
    participant Cache as Redis
    participant Queue as Message Queue
    participant UserService as User Service
    participant NotificationService as Notification Service

    POS->>Gateway: POST /api/v1/points/earn
    Note over POS,Gateway: Transaction data: user_id, amount, type

    Gateway->>Gateway: Rate limiting check
    Gateway->>Gateway: API key validation
    Gateway->>PointService: Forward request

    PointService->>PointService: Validate transaction data
    PointService->>UserService: Verify user exists and is active
    UserService-->>PointService: User validated

    PointService->>RuleEngine: Calculate earned points
    Note over PointService,RuleEngine: Consider: base rate, multipliers, bonuses
    RuleEngine->>Database: Get earning rules
    Database-->>RuleEngine: Current rules
    RuleEngine->>RuleEngine: Apply calculation logic
    RuleEngine-->>PointService: Points calculated (e.g., 100 points)

    PointService->>Database: Begin transaction
    PointService->>Database: Create point transaction record
    Database-->>PointService: Transaction saved

    PointService->>Cache: Get current balance
    Cache-->>PointService: Current balance (e.g., 500 points)
    PointService->>PointService: Calculate new balance (600 points)
    PointService->>Cache: Update cached balance
    Cache-->>PointService: Balance updated

    PointService->>Database: Commit transaction
    Database-->>PointService: Transaction committed

    PointService->>Queue: Publish points.earned event
    Queue-->>NotificationService: Consume event
    NotificationService->>NotificationService: Prepare notification
    NotificationService->>Client: Send push notification
    NotificationService->>Client: Send email notification

    PointService-->>Gateway: Points earned successfully
    Gateway-->>POS: HTTP 200 - Transaction processed

    Note over POS,NotificationService: Real-time processing < 3 seconds
```

---

## 3. Point Redemption Flow

```mermaid
sequenceDiagram
    participant Client as Mobile/Web App
    participant Gateway as API Gateway
    participant PointService as Point Service
    participant RewardsService as Rewards Service
    participant FraudDetection as Fraud Detection
    participant Database as PostgreSQL
    participant Cache as Redis
    participant Queue as Message Queue
    participant PaymentGateway as Payment Gateway
    participant NotificationService as Notification Service

    Client->>Gateway: POST /api/v1/points/redeem
    Note over Client,Gateway: Redemption data: reward_id, points, payment_method

    Gateway->>Gateway: Rate limiting check
    Gateway->>Gateway: JWT authentication
    Gateway->>PointService: Forward request

    PointService->>PointService: Validate request data
    PointService->>Cache: Get user's current balance
    Cache-->>PointService: Current balance (e.g., 1000 points)

    PointService->>RewardsService: Get reward details
    RewardsService->>Database: Query reward info
    Database-->>RewardsService: Reward details
    RewardsService->>RewardsService: Check availability
    RewardsService-->>PointService: Reward available (cost: 800 points)

    PointService->>PointService: Validate sufficient balance
    PointService->>FraudDetection: Check for suspicious activity
    FraudDetection->>Database: Query recent redemption history
    Database-->>FraudDetection: Recent transactions
    FraudDetection->>FraudDetection: Analyze patterns
    FraudDetection-->>PointService: No fraud detected

    PointService->>Database: Begin transaction
    
    alt Partial Redemption (Points + Cash)
        PointService->>PaymentGateway: Process cash payment
        PaymentGateway-->>PointService: Payment successful
    end

    PointService->>Database: Create redemption transaction
    Database-->>PointService: Transaction created
    
    PointService->>Cache: Update balance (1000 - 800 = 200)
    Cache-->>PointService: Balance updated

    PointService->>RewardsService: Reserve reward inventory
    RewardsService->>Database: Update inventory count
    Database-->>RewardsService: Inventory updated
    RewardsService-->>PointService: Inventory reserved

    PointService->>Database: Commit transaction
    Database-->>PointService: Transaction committed

    PointService->>Queue: Publish points.redeemed event
    Queue-->>NotificationService: Consume event
    NotificationService->>Client: Send redemption confirmation
    Queue-->>RewardsService: Trigger reward fulfillment

    PointService-->>Gateway: Redemption successful
    Gateway-->>Client: HTTP 200 - Points redeemed

    Note over Client,NotificationService: Confirmation sent within 1 minute
```

---

## 4. Admin User Management Flow

```mermaid
sequenceDiagram
    participant Admin as Admin Dashboard
    participant Gateway as API Gateway
    participant AdminService as Admin Service
    participant UserService as User Service
    participant PointService as Point Service
    participant Database as PostgreSQL
    participant Cache as Redis
    participant Queue as Message Queue
    participant AuditService as Audit Service
    participant NotificationService as Notification Service

    Admin->>Gateway: POST /api/v1/admin/users/{userId}/adjust-points
    Note over Admin,Gateway: Point adjustment: +/-points, reason, approval_required

    Gateway->>Gateway: JWT authentication
    Gateway->>Gateway: Admin role verification
    Gateway->>AdminService: Forward request

    AdminService->>AdminService: Validate admin permissions
    AdminService->>AdminService: Check adjustment limits
    
    alt Requires Approval
        AdminService->>Database: Create pending approval request
        Database-->>AdminService: Request saved
        AdminService->>Queue: Publish approval.required event
        Queue-->>NotificationService: Notify super admin
        AdminService-->>Gateway: HTTP 202 - Approval required
        Gateway-->>Admin: Pending approval
    else Direct Adjustment
        AdminService->>UserService: Verify user exists
        UserService-->>AdminService: User validated
        
        AdminService->>PointService: Execute point adjustment
        PointService->>Database: Begin transaction
        PointService->>Database: Create adjustment transaction
        Database-->>PointService: Transaction created
        
        PointService->>Cache: Update user balance
        Cache-->>PointService: Balance updated
        
        PointService->>Database: Commit transaction
        Database-->>PointService: Transaction committed
        PointService-->>AdminService: Adjustment completed
        
        AdminService->>AuditService: Log admin action
        AuditService->>Database: Save audit log
        Database-->>AuditService: Log saved
        
        AdminService->>Queue: Publish points.adjusted event
        Queue-->>NotificationService: Notify user
        NotificationService->>User: Send adjustment notification
        
        AdminService-->>Gateway: HTTP 200 - Adjustment successful
        Gateway-->>Admin: Points adjusted successfully
    end

    Note over Admin,NotificationService: All admin actions are audited
```

---

## 5. Tier Upgrade Flow

```mermaid
sequenceDiagram
    participant System as System Scheduler
    participant UserService as User Service
    participant PointService as Point Service
    participant TierService as Tier Service
    participant Database as PostgreSQL
    participant Cache as Redis
    participant Queue as Message Queue
    participant NotificationService as Notification Service
    participant RewardsService as Rewards Service

    System->>System: Daily tier evaluation trigger
    System->>UserService: Get users for tier evaluation
    UserService->>Database: Query eligible users
    Database-->>UserService: List of users

    loop For each user
        UserService->>PointService: Get user's point history
        PointService->>Database: Query transactions (last 12 months)
        Database-->>PointService: Transaction history
        PointService-->>UserService: Point statistics

        UserService->>TierService: Evaluate tier eligibility
        TierService->>TierService: Calculate spending, frequency, points
        TierService->>Database: Get tier requirements
        Database-->>TierService: Tier rules
        TierService->>TierService: Apply tier logic
        TierService-->>UserService: New tier recommendation

        alt Tier Upgrade Required
            UserService->>Database: Begin transaction
            UserService->>Database: Update user tier
            Database-->>UserService: Tier updated
            
            UserService->>Cache: Update cached user data
            Cache-->>UserService: Cache updated
            
            UserService->>Queue: Publish tier.upgraded event
            Queue-->>NotificationService: Send congratulations
            NotificationService->>User: Tier upgrade notification
            
            Queue-->>RewardsService: Update reward availability
            RewardsService->>RewardsService: Enable tier-specific rewards
            RewardsService->>Database: Update user's available rewards
            Database-->>RewardsService: Rewards updated
            
            Queue-->>PointService: Apply tier benefits
            PointService->>PointService: Update earning multipliers
            PointService->>Database: Save new earning rules for user
            Database-->>PointService: Rules updated
            
            UserService->>Database: Commit transaction
            Database-->>UserService: Transaction committed
        end
    end

    System-->>System: Tier evaluation completed

    Note over System,RewardsService: Automated daily process
    Note over System,RewardsService: Ensures consistent tier benefits
```

---

## 6. Fraud Detection Flow

```mermaid
sequenceDiagram
    participant Transaction as Transaction Event
    participant FraudEngine as Fraud Detection Engine
    participant MLModel as ML Model
    participant Database as PostgreSQL
    participant Cache as Redis
    participant AdminService as Admin Service
    participant Queue as Message Queue
    participant NotificationService as Notification Service

    Transaction->>FraudEngine: Transaction event received
    Note over Transaction,FraudEngine: Point earning/redemption event

    FraudEngine->>Database: Get user's transaction history
    Database-->>FraudEngine: Recent transactions
    
    FraudEngine->>Cache: Get user's behavioral patterns
    Cache-->>FraudEngine: Cached patterns
    
    FraudEngine->>FraudEngine: Analyze transaction patterns
    Note over FraudEngine: Check: frequency, amounts, timing, location
    
    FraudEngine->>MLModel: Score transaction risk
    MLModel->>MLModel: Apply ML algorithms
    MLModel-->>FraudEngine: Risk score (0-100)
    
    alt High Risk Score (>80)
        FraudEngine->>Database: Flag transaction as suspicious
        Database-->>FraudEngine: Transaction flagged
        
        FraudEngine->>Queue: Publish fraud.detected event
        Queue-->>AdminService: Alert admin team
        AdminService->>AdminService: Create investigation case
        AdminService->>Database: Save fraud case
        Database-->>AdminService: Case saved
        
        Queue-->>NotificationService: Block further transactions
        NotificationService->>User: Account temporarily restricted
        
        FraudEngine->>Cache: Update user risk profile
        Cache-->>FraudEngine: Profile updated
        
    else Medium Risk Score (50-80)
        FraudEngine->>Database: Log for review
        Database-->>FraudEngine: Logged
        
        FraudEngine->>Queue: Publish review.required event
        Queue-->>AdminService: Queue for manual review
        
    else Low Risk Score (<50)
        FraudEngine->>Cache: Update behavioral patterns
        Cache-->>FraudEngine: Patterns updated
        
        FraudEngine->>FraudEngine: Allow transaction
    end

    FraudEngine-->>Transaction: Risk assessment completed

    Note over Transaction,NotificationService: Real-time fraud detection
    Note over Transaction,NotificationService: Protects system integrity
```

---

## 7. Analytics Data Collection Flow

```mermaid
sequenceDiagram
    participant UserAction as User Action
    participant Service as Microservice
    participant EventStream as Event Stream
    participant DataLake as Data Lake
    participant ETL as ETL Pipeline
    participant Analytics as Analytics DB
    participant Dashboard as Admin Dashboard
    participant ML as ML Pipeline

    UserAction->>Service: User performs action
    Note over UserAction,Service: Registration, purchase, redemption, etc.

    Service->>Service: Process business logic
    Service->>EventStream: Publish domain event
    Note over Service,EventStream: Structured event with metadata

    EventStream->>DataLake: Stream event data
    Note over EventStream,DataLake: Real-time data ingestion

    EventStream->>ETL: Trigger data processing
    ETL->>DataLake: Read raw event data
    DataLake-->>ETL: Event data
    
    ETL->>ETL: Transform and aggregate data
    Note over ETL: Calculate metrics, sessions, cohorts
    
    ETL->>Analytics: Store processed metrics
    Analytics-->>ETL: Data stored
    
    ETL->>ML: Send data for model training
    ML->>ML: Update prediction models
    Note over ML: User behavior, fraud detection, recommendations
    
    Dashboard->>Analytics: Query dashboard metrics
    Analytics-->>Dashboard: Real-time metrics
    Dashboard->>Dashboard: Display KPIs
    Note over Dashboard: User engagement, revenue, retention

    ML->>Service: Update real-time scoring
    Note over ML,Service: Personalization, risk scoring

    Note over UserAction,ML: End-to-end data pipeline
    Note over UserAction,ML: Enables data-driven decisions
```

---

## 8. System Health Monitoring Flow

```mermaid
sequenceDiagram
    participant Service as Microservice
    participant HealthCheck as Health Check
    participant Metrics as Metrics Collector
    participant AlertManager as Alert Manager
    participant Monitoring as Monitoring Dashboard
    participant OnCall as On-Call Engineer
    participant Queue as Message Queue

    Service->>HealthCheck: Periodic health check
    HealthCheck->>HealthCheck: Check database connectivity
    HealthCheck->>HealthCheck: Check external dependencies
    HealthCheck->>HealthCheck: Check memory/CPU usage
    HealthCheck-->>Service: Health status

    Service->>Metrics: Emit performance metrics
    Note over Service,Metrics: Response times, error rates, throughput

    Metrics->>Monitoring: Store metrics
    Monitoring->>Monitoring: Analyze trends and thresholds

    alt Critical Issue Detected
        Monitoring->>AlertManager: Trigger critical alert
        AlertManager->>OnCall: Page on-call engineer
        AlertManager->>Queue: Send alert to Slack/Teams
        Queue-->>Team: Notify team channels
        
        OnCall->>Service: Investigate issue
        OnCall->>Service: Apply emergency fix
        Service->>Monitoring: Issue resolved
        Monitoring->>AlertManager: Clear alert
        
    else Warning Threshold Exceeded
        Monitoring->>AlertManager: Send warning notification
        AlertManager->>Queue: Queue notification for business hours
        
    else Normal Operation
        Monitoring->>Monitoring: Continue monitoring
    end

    Note over Service,OnCall: 24/7 system reliability
    Note over Service,OnCall: Proactive issue detection
```

---

## Usage Guidelines for Developers

### 1. Understanding the Diagrams
- **Participants:** Represent system components, services, or actors
- **Messages:** Show the flow of data and control between components
- **Notes:** Provide additional context and business rules
- **Alt blocks:** Show conditional logic and different paths

### 2. Implementation Reference
- Use these diagrams during development to understand the complete flow
- Ensure all steps are implemented according to the sequence
- Pay attention to error handling and alternative flows
- Consider the timing and performance requirements noted

### 3. Code Review Guide
- Verify that implemented code follows the documented sequence
- Check that all events are published and consumed as shown
- Ensure error handling covers the scenarios depicted
- Validate that the performance requirements are met

### 4. Testing Strategy
- Create test cases that cover the main flow and alternative paths
- Use the diagrams to identify integration test scenarios
- Ensure that all external dependencies are properly mocked
- Test the error conditions and edge cases shown

---

**Next Steps:**
1. Study the relevant diagrams before implementing features
2. Keep these diagrams updated as the system evolves
3. Use them during code reviews and system design discussions
4. Reference them when troubleshooting production issues