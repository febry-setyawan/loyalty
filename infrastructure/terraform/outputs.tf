# Outputs for Loyalty System Infrastructure

# VPC Outputs
output "vpc_id" {
  description = "ID of the VPC"
  value       = module.vpc.vpc_id
}

output "vpc_arn" {
  description = "ARN of the VPC"
  value       = module.vpc.vpc_arn
}

output "vpc_cidr_block" {
  description = "CIDR block of the VPC"
  value       = module.vpc.vpc_cidr_block
}

output "private_subnets" {
  description = "List of IDs of private subnets"
  value       = module.vpc.private_subnets
}

output "public_subnets" {
  description = "List of IDs of public subnets"
  value       = module.vpc.public_subnets
}

output "database_subnets" {
  description = "List of IDs of database subnets"
  value       = module.vpc.database_subnets
}

# EKS Outputs
output "cluster_id" {
  description = "EKS cluster ID"
  value       = module.eks.cluster_id
}

output "cluster_arn" {
  description = "EKS cluster ARN"
  value       = module.eks.cluster_arn
}

output "cluster_endpoint" {
  description = "EKS cluster endpoint"
  value       = module.eks.cluster_endpoint
}

output "cluster_security_group_id" {
  description = "EKS cluster security group ID"
  value       = module.eks.cluster_security_group_id
}

output "cluster_iam_role_name" {
  description = "IAM role name associated with EKS cluster"
  value       = module.eks.cluster_iam_role_name
}

output "cluster_iam_role_arn" {
  description = "IAM role ARN associated with EKS cluster"
  value       = module.eks.cluster_iam_role_arn
}

output "cluster_certificate_authority_data" {
  description = "Base64 encoded certificate data required to communicate with the cluster"
  value       = module.eks.cluster_certificate_authority_data
}

# RDS Outputs
output "db_instance_address" {
  description = "RDS instance hostname"
  value       = module.database.db_instance_address
  sensitive   = true
}

output "db_instance_arn" {
  description = "RDS instance ARN"
  value       = module.database.db_instance_arn
}

output "db_instance_endpoint" {
  description = "RDS instance endpoint"
  value       = module.database.db_instance_endpoint
  sensitive   = true
}

output "db_instance_port" {
  description = "RDS instance port"
  value       = module.database.db_instance_port
}

output "db_instance_name" {
  description = "RDS instance name"
  value       = module.database.db_instance_name
}

# Redis Outputs
output "redis_cluster_address" {
  description = "Redis cluster address"
  value       = module.redis.cluster_address
  sensitive   = true
}

output "redis_cluster_id" {
  description = "Redis cluster ID"
  value       = module.redis.cluster_id
}

output "redis_port" {
  description = "Redis port"
  value       = module.redis.port
}

# Security Outputs
output "database_security_group_id" {
  description = "Database security group ID"
  value       = module.database_security_group.security_group_id
}

output "redis_security_group_id" {
  description = "Redis security group ID"
  value       = module.redis_security_group.security_group_id
}

# KMS Outputs
output "kms_key_id" {
  description = "KMS key ID"
  value       = module.kms.key_id
}

output "kms_key_arn" {
  description = "KMS key ARN"
  value       = module.kms.key_arn
}

# Environment Information
output "environment" {
  description = "Environment name"
  value       = var.environment
}

output "region" {
  description = "AWS region"
  value       = var.aws_region
}