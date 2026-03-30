FROM public.ecr.aws/lambda/java:25

COPY target/classes ${LAMBDA_TASK_ROOT}
COPY target/dependency/* ${LAMBDA_TASK_ROOT}/lib/

CMD [ "net.samitkumar.springboot_with_aws_lambda.LambdaHandler::handleRequest" ]
# docker buildx build --platform linux/amd64 --provenance=false -t springboot-with-aws-lambda .