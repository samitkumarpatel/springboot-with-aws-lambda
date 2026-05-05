output "ecr_repository_url" {
  description = "ECR repository URL — use this as the push target in CI"
  value       = aws_ecr_repository.this.repository_url
}

output "lambda_function_name" {
  value = aws_lambda_function.this.function_name
}

output "lambda_function_arn" {
  value = aws_lambda_function.this.arn
}

output "lambda_function_url" {
  description = "Public HTTPS endpoint for the Lambda"
  value       = aws_lambda_function_url.this.function_url
}

output "dynamodb_table_names" {
  description = "Names of all provisioned DynamoDB tables"
  value       = [for t in aws_dynamodb_table.this : t.name]
}

output "dynamodb_table_arns" {
  description = "ARNs of all provisioned DynamoDB tables"
  value       = { for k, t in aws_dynamodb_table.this : k => t.arn }
}
