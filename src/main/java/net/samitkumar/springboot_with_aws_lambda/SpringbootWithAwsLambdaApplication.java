package net.samitkumar.springboot_with_aws_lambda;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;

import java.util.Map;

@SpringBootApplication
public class SpringbootWithAwsLambdaApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWithAwsLambdaApplication.class, args);
	}

	@Bean
	RouterFunction<ServerResponse> routerFunction() {
		return RouterFunctions
				.route()
				.GET("/functional/ping", request -> ServerResponse.ok().body(Map.of("message", "pong function")))
				.build();
	}

}

@RestController
class WebController {
	@GetMapping("/ping")
	Map<String, String> ping() {
		return Map.of("message", "pong");
	}
}