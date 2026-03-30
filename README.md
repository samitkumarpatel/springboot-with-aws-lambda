# springboot-with-aws-lambda

### Build
```shell
./mvnw clean install

docker buildx build \
  --platform linux/amd64 \
  --provenance=false \
  --push \
  -t $ECR_REGISTRY/springboot-with-aws-lambda:main
```

### Lambda configuration
Lambda configuration Not needed - as it's already describe in the docker file and the code has a handler to deal with what type of trigger it can support like http or gateway.

### Deploy in aws lambda
Deploy it via `aws cli`, `aws web` ,`github action` or `terraform` or any other way. 

### Configure API Gateway to expose this lambda as a REST API


### Test the API
