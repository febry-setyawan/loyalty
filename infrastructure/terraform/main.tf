# Main Terraform configuration for Loyalty System Infrastructure
terraform {
  required_version = ">= 1.0"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.0"
    }
    kubernetes = {
      source  = "hashicorp/kubernetes"
      version = "~> 2.20"
    }
  }
}

provider "aws" {
  region = var.aws_region

  default_tags {
    tags = {
      Project     = "loyalty-system"
      Environment = var.environment
      ManagedBy   = "terraform"
      Owner       = "platform-team"
    }
  }
}

# Data sources
data "aws_availability_zones" "available" {
  state = "available"
}

data "aws_caller_identity" "current" {}

# Local values for common configurations
locals {
  name_prefix = "${var.project_name}-${var.environment}"
  
  # Networking
  vpc_cidr = var.vpc_cidr
  azs      = slice(data.aws_availability_zones.available.names, 0, min(length(data.aws_availability_zones.available.names), 3))
  
  # Subnets
  private_subnets = [
    cidrsubnet(local.vpc_cidr, 8, 1),
    cidrsubnet(local.vpc_cidr, 8, 2),
    cidrsubnet(local.vpc_cidr, 8, 3),
  ]
  
  public_subnets = [
    cidrsubnet(local.vpc_cidr, 8, 101),
    cidrsubnet(local.vpc_cidr, 8, 102),
    cidrsubnet(local.vpc_cidr, 8, 103),
  ]
  
  database_subnets = [
    cidrsubnet(local.vpc_cidr, 8, 201),
    cidrsubnet(local.vpc_cidr, 8, 202),
    cidrsubnet(local.vpc_cidr, 8, 203),
  ]
}

# VPC Module
module "vpc" {
  source = "terraform-aws-modules/vpc/aws"
  
  name = local.name_prefix
  cidr = local.vpc_cidr
  
  azs              = local.azs
  private_subnets  = local.private_subnets
  public_subnets   = local.public_subnets
  database_subnets = local.database_subnets
  
  enable_nat_gateway = true
  enable_vpn_gateway = false
  single_nat_gateway = var.environment != "production"
  
  enable_dns_hostnames = true
  enable_dns_support   = true
  
  # Database subnet group
  create_database_subnet_group = true
  create_database_subnet_route_table = true
  
  # VPC Flow Logs
  enable_flow_log                      = true
  create_flow_log_cloudwatch_iam_role  = true
  create_flow_log_cloudwatch_log_group = true
  
  tags = {
    Name = "${local.name_prefix}-vpc"
  }
}

# EKS Cluster Module
module "eks" {
  source = "./modules/eks"
  
  cluster_name    = "${local.name_prefix}-eks"
  cluster_version = var.eks_cluster_version
  
  vpc_id          = module.vpc.vpc_id
  subnet_ids      = module.vpc.private_subnets
  
  # Node groups configuration
  node_groups = {
    main = {
      desired_size = var.eks_node_group_desired_size
      max_size     = var.eks_node_group_max_size
      min_size     = var.eks_node_group_min_size
      
      instance_types = var.eks_node_group_instance_types
      capacity_type  = "ON_DEMAND"
      
      ami_type = "AL2_x86_64"
      disk_size = 50
    }
  }
  
  # RBAC
  map_roles = var.eks_map_roles
  map_users = var.eks_map_users
  
  tags = {
    Environment = var.environment
    Project     = "loyalty-system"
  }
}

# RDS PostgreSQL Module
module "database" {
  source = "./modules/rds"
  
  identifier = "${local.name_prefix}-db"
  
  # Engine options
  engine            = "postgres"
  engine_version    = var.rds_engine_version
  family            = "postgres15"
  major_engine_version = "15"
  
  # Instance specifications
  instance_class    = var.rds_instance_class
  allocated_storage = var.rds_allocated_storage
  max_allocated_storage = var.rds_max_allocated_storage
  
  # Storage
  storage_type      = "gp3"
  storage_encrypted = true
  kms_key_id       = module.kms.key_arn
  
  # Database settings
  db_name  = var.rds_db_name
  username = var.rds_username
  port     = 5432
  
  # Network & Security
  vpc_security_group_ids = [module.database_security_group.security_group_id]
  db_subnet_group_name   = module.vpc.database_subnet_group
  
  # Backup and maintenance
  backup_retention_period = var.environment == "production" ? 30 : 7
  backup_window          = "03:00-04:00"
  maintenance_window     = "Sun:04:00-Sun:05:00"
  
  deletion_protection = var.environment == "production"
  skip_final_snapshot = var.environment != "production"
  
  # Monitoring
  performance_insights_enabled = true
  monitoring_interval         = 60
  create_monitoring_role      = true
  
  # Logging
  enabled_cloudwatch_logs_exports = ["postgresql", "upgrade"]
  
  tags = {
    Name = "${local.name_prefix}-database"
  }
}

# ElastiCache Redis Module
module "redis" {
  source = "./modules/elasticache"
  
  cluster_id           = "${local.name_prefix}-redis"
  node_type            = var.redis_node_type
  num_cache_nodes      = var.redis_num_nodes
  parameter_group_name = "default.redis7"
  port                 = 6379
  
  # Network & Security
  subnet_group_name  = aws_elasticache_subnet_group.redis.name
  security_group_ids = [module.redis_security_group.security_group_id]
  
  # Encryption
  at_rest_encryption_enabled = true
  transit_encryption_enabled = true
  
  # Backup
  snapshot_retention_limit = var.environment == "production" ? 5 : 1
  snapshot_window         = "03:00-05:00"
  
  tags = {
    Name = "${local.name_prefix}-redis"
  }
}

# KMS Key for encryption
module "kms" {
  source = "./modules/kms"
  
  description = "KMS key for ${local.name_prefix}"
  
  tags = {
    Name = "${local.name_prefix}-kms-key"
  }
}

# Security Groups
module "database_security_group" {
  source = "terraform-aws-modules/security-group/aws"
  
  name        = "${local.name_prefix}-database-sg"
  description = "Security group for database"
  vpc_id      = module.vpc.vpc_id
  
  ingress_with_source_security_group_id = [
    {
      from_port                = 5432
      to_port                  = 5432
      protocol                 = "tcp"
      description              = "PostgreSQL from EKS"
      source_security_group_id = module.eks.cluster_primary_security_group_id
    }
  ]
  
  tags = {
    Name = "${local.name_prefix}-database-sg"
  }
}

module "redis_security_group" {
  source = "terraform-aws-modules/security-group/aws"
  
  name        = "${local.name_prefix}-redis-sg"
  description = "Security group for Redis"
  vpc_id      = module.vpc.vpc_id
  
  ingress_with_source_security_group_id = [
    {
      from_port                = 6379
      to_port                  = 6379
      protocol                 = "tcp"
      description              = "Redis from EKS"
      source_security_group_id = module.eks.cluster_primary_security_group_id
    }
  ]
  
  tags = {
    Name = "${local.name_prefix}-redis-sg"
  }
}

# ElastiCache subnet group
resource "aws_elasticache_subnet_group" "redis" {
  name       = "${local.name_prefix}-redis-subnet-group"
  subnet_ids = module.vpc.private_subnets
  
  tags = {
    Name = "${local.name_prefix}-redis-subnet-group"
  }
}