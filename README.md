# springboot-with-aws-lambda

### Build
```shell
./mvnw clean install

docker buildx build -t springboot-with-aws-lambda .
```

### Run & Test
```shell

docker run --rm -p 9000:8080 springboot-with-aws-lambda

curl "http://localhost:9000/2015-03-31/functions/function/invocations" \
  -d '{"version":"2.0","routeKey":"GET /ping","rawPath":"/ping","requestContext":{"http":{"method":"GET","path":"/ping"}}}'

# OR
python3 local-proxy/http-proxy.py

In the browser or any other http client visit `http://localhost:8080/ping`
```