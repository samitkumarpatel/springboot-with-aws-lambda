variable "aws_region" {
  description = "AWS region"
  type        = string
  default     = "eu-north-1"
}

variable "dynamodb_tables" {
  description = "Map of DynamoDB tables to create. Key is the table name."
  type = map(object({
    billing_mode  = optional(string, "PAY_PER_REQUEST")
    hash_key      = string
    range_key     = optional(string)
    ttl_attribute = optional(string)
    attributes = list(object({
      name = string
      type = string
    }))
  }))
  default = {
    "spring-sessions" = {
      hash_key      = "sessionId"
      ttl_attribute = "ttl"
      attributes = [
        { name = "sessionId", type = "S" }
      ]
    }
  }
}
