# springboot-with-aws-lambda

This is how you can run a Spring Boot application in AWS Lambda using the AWS Serverless Java Container library. This example uses the `Spring Web MVC framework`, but you can also use `Spring WebFlux` or any other supported framework with `Spring Security`.

### Build

```shell
./mvnw clean install && \
  docker buildx build -t springboot-with-aws-lambda .
```

### Run & Test

```shell

docker run --rm -p 9000:8080 -e SECRET_KEY=$AWS_SECRET_ACCESS_KEY -e ACCESS_KEY=$AWS_ACCESS_KEY_ID springboot-with-aws-lambda

python3 local-proxy/http-proxy.py

In the browser or any other http client visit `http://localhost:8080`
username: user
password: password
```