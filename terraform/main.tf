terraform {
  required_version = ">= 1.14"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.43"
    }
  }
  backend "s3" {
      bucket  = "tfpocbucket001"
      key     = "springboot-with-aws-lambda/terraform.tfstate"
      region  = "eu-north-1"
      encrypt = true
  }
}

provider "aws" {
  region = var.aws_region
}

locals {
  name = "springboot-with-aws-lambda"
}

# ---------------------------------------------------------------------------
# ECR
# ---------------------------------------------------------------------------

resource "aws_ecr_repository" "this" {
  name                 = local.name
  image_tag_mutability = "MUTABLE"
  force_delete         = true

  image_scanning_configuration {
    scan_on_push = true
  }
}

resource "aws_ecr_lifecycle_policy" "this" {
  repository = aws_ecr_repository.this.name

  policy = jsonencode({
    rules = [{
      rulePriority = 1
      description  = "Keep last 5 images"
      selection = {
        tagStatus   = "any"
        countType   = "imageCountMoreThan"
        countNumber = 5
      }
      action = { type = "expire" }
    }]
  })
}

resource "null_resource" "this" {
  triggers = {
    ecr_repo_url = aws_ecr_repository.this.repository_url
  }

  provisioner "local-exec" {
    command = <<-EOT
      aws ecr get-login-password --region ${var.aws_region} \
        | docker login --username AWS --password-stdin ${aws_ecr_repository.this.registry_id}.dkr.ecr.${var.aws_region}.amazonaws.com

      docker pull public.ecr.aws/lambda/java:25
      docker tag  public.ecr.aws/lambda/java:25 ${aws_ecr_repository.this.repository_url}:25
      docker push ${aws_ecr_repository.this.repository_url}:25
    EOT
  }
}

# ---------------------------------------------------------------------------
# IAM
# ---------------------------------------------------------------------------

data "aws_iam_policy_document" "assume_role" {
  statement {
    effect = "Allow"

    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }

    actions = ["sts:AssumeRole"]
  }
}

resource "aws_iam_role" "this" {
  name               = "${local.name}-role"
  assume_role_policy = data.aws_iam_policy_document.assume_role.json
}

resource "aws_iam_role_policy_attachment" "logs" {
  role       = aws_iam_role.this.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

# ---------------------------------------------------------------------------
# Lambda
# ---------------------------------------------------------------------------
variable "aws_secret_key" {}
variable "aws_access_key" {}

resource "aws_lambda_function" "this" {
  function_name = local.name
  role          = aws_iam_role.this.arn
  package_type  = "Image"
  image_uri     = "${aws_ecr_repository.this.repository_url}:25"

  memory_size = 512
  timeout     = 30

  architectures = ["x86_64"]

  environment {
    variables = {
      SECRET_KEY = var.aws_secret_key
      ACCESS_KEY = var.aws_access_key
    }
  }
  depends_on = [null_resource.this]

    lifecycle {
      ignore_changes = [image_uri]
    }
}

resource "aws_cloudwatch_log_group" "this" {
  name              = "/aws/lambda/${local.name}"
  retention_in_days = 5
}

# ---------------------------------------------------------------------------
# Lambda Function URL
# ---------------------------------------------------------------------------

resource "aws_lambda_function_url" "this" {
  function_name      = aws_lambda_function.this.function_name
  authorization_type = "NONE"

  cors {
    allow_credentials = false
    allow_origins     = ["*"]
    allow_methods     = ["*"]
    allow_headers     = ["*"]
    max_age           = 86400
  }
}
