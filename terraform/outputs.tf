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
