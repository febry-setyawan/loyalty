# Point Service - API Documentation

## Overview

The Point Service handles all point-related operations in the loyalty system including earning, tracking, and managing point balances according to business rules.

## Story 3.1 Implementation: Point Earning System

### ✅ Completed Features

- **✅ Point Earning Engine**: Calculates points based on transaction amounts (1 point = Rp 1,000)
- **✅ Bonus Multiplier System**: Supports 2x, 3x, 5x multipliers for special events
- **✅ Referral Point System**: Awards 500 points per successful referral
- **✅ Real-time Calculation**: Point calculation with < 3 second latency
- **✅ Configurable Rules**: Earning rules can be configured without downtime
- **✅ Event Publishing**: Real-time event publishing for notifications
- **✅ Comprehensive Testing**: Unit and integration tests implemented

### API Endpoints

#### 1. Earn Points
**POST** `/api/v1/points/earn`

Earn points based on transaction amount and earning type.

**Request Body:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "transactionAmount": 5000.0,
  "earningType": "PURCHASE",
  "description": "Purchase from store",
  "referenceId": "TXN-123456",
  "userTier": "GOLD",
  "bonusMultiplier": 2.0
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "transactionId": "550e8400-e29b-41d4-a716-446655440001",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "pointsEarned": 10,
    "newBalance": 1510,
    "message": "Points earned successfully",
    "earningType": "PURCHASE",
    "transactionAmount": 5000.0
  },
  "message": "Points earned successfully",
  "timestamp": "2024-12-01T10:30:00"
}
```

**Earning Types:**
- `PURCHASE` - Points from purchase (1 point = Rp 1,000)
- `REFERRAL` - Points from referral (500 points)
- `SIGNUP` - Points from signup bonus
- `REVIEW` - Points from review
- `BONUS_EVENT` - Special event bonus points

#### 2. Get Point Balance
**GET** `/api/v1/points/balance/{userId}`

Get current point balance for a user.

**Response:**
```json
{
  "success": true,
  "data": {
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "totalPoints": 1510,
    "availablePoints": 1510,
    "pendingPoints": 0,
    "lifetimeEarned": 2000,
    "lifetimeSpent": 490,
    "lastUpdated": "2024-12-01T10:30:00"
  },
  "message": "Balance retrieved successfully",
  "timestamp": "2024-12-01T10:30:00"
}
```

#### 3. Get Earning Rules
**GET** `/api/v1/points/earning-rules`

Get current active earning rules configuration.

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "id": "550e8400-e29b-41d4-a716-446655440000",
      "name": "Default Purchase Rule",
      "ruleType": "PURCHASE",
      "pointsPerUnit": 1.0,
      "unitType": "DOLLAR",
      "multiplier": 1.0,
      "minAmount": 1000.0,
      "active": true
    }
  ],
  "message": "Earning rules retrieved successfully",
  "timestamp": "2024-12-01T10:30:00"
}
```

#### 4. Award Referral Points
**POST** `/api/v1/points/referral`

Award referral points to a user.

**Request Body:**
```json
{
  "userId": "550e8400-e29b-41d4-a716-446655440000",
  "transactionAmount": 1,
  "referenceId": "REF-123456"
}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "transactionId": "550e8400-e29b-41d4-a716-446655440001",
    "userId": "550e8400-e29b-41d4-a716-446655440000",
    "pointsEarned": 500,
    "newBalance": 2010,
    "message": "Referral points awarded successfully"
  },
  "message": "Referral points awarded successfully",
  "timestamp": "2024-12-01T10:30:00"
}
```

### Business Rules

#### Point Calculation Rules
1. **Basic Purchase**: 1 point = Rp 1,000 transaction value
2. **Referral Bonus**: Fixed 500 points per successful referral
3. **Bonus Multipliers**: 2x, 3x, 5x multipliers for special events
4. **Minimum Transaction**: Minimum Rp 1,000 to earn points
5. **Real-time Processing**: Points calculated and awarded within 3 seconds

#### Point Balance Management
- **Total Points**: All points earned by the user
- **Available Points**: Points available for redemption
- **Pending Points**: Points awaiting confirmation
- **Lifetime Stats**: Track total earned and spent points

### Error Handling

The API returns standard error responses:

```json
{
  "success": false,
  "error": {
    "code": "INVALID_REQUEST",
    "message": "Transaction amount must be positive"
  },
  "timestamp": "2024-12-01T10:30:00"
}
```

**Common Error Codes:**
- `INVALID_REQUEST` - Request validation failed
- `INSUFFICIENT_POINTS` - Not enough points for operation
- `USER_NOT_FOUND` - User does not exist
- `INTERNAL_ERROR` - Internal server error

### Performance Characteristics

- **Point Calculation**: < 3 seconds latency
- **Real-time Events**: Published immediately after transaction
- **High Availability**: Designed for concurrent operations
- **Scalable Design**: Clean architecture supports scaling

### Testing

Run unit tests:
```bash
mvn test
```

Run integration tests:
```bash
mvn verify
```

### Database Schema

The service uses the following tables:
- `points_balance` - User point balances
- `points_transactions` - Point transaction history
- `earning_rules` - Configurable earning rules
- `points_expiry` - Point expiration tracking

### Event Publishing

The service publishes the following events:
- `POINTS_EARNED` - When points are earned
- `POINTS_SPENT` - When points are redeemed
- `REFERRAL_POINTS_EARNED` - When referral points are awarded
- `POINTS_EXPIRED` - When points expire