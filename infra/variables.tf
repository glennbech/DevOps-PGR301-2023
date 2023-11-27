variable "service_name" {
  type = string
  description = "The name of this service"
}

variable "aws_account_number" {
  type = string
}

variable "aws_region" {
  type = string
  description = "The region of the ecr repository"
}

variable "ecr_repository" {
  type = string
  description = "the name of the ecr repository"
}

variable "ecr_tag" {
  type = string
  default = "latest"
}

variable "cloudwatch_namespace" {
  type = string
}