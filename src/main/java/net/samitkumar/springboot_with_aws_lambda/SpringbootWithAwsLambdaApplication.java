package net.samitkumar.springboot_with_aws_lambda;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.RouterFunctions;
import org.springframework.web.servlet.function.ServerResponse;
import org.springframework.web.util.HtmlUtils;

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

	@GetMapping(value = "/login", produces = MediaType.TEXT_HTML_VALUE)
	ResponseEntity<String> loginPage(
			@RequestParam(required = false) String error,
			@RequestParam(required = false) String logout,
			HttpServletRequest request) {
		CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
		String csrfField = csrf != null
				? "<input type=\"hidden\" name=\"%s\" value=\"%s\"/>".formatted(
						HtmlUtils.htmlEscape(csrf.getParameterName()),
						HtmlUtils.htmlEscape(csrf.getToken()))
				: "";
		String message = error != null ? "<p style=\"color:red\">Invalid username or password</p>"
				: logout != null ? "<p style=\"color:green\">Logged out</p>" : "";
		return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body("""
				<!DOCTYPE html>
				<html lang="en">
				<head><meta charset="UTF-8"><title>Login</title></head>
				<body>
				  <h1>Sign In</h1>
				  %s
				  <form method="post" action="/login">
				    %s
				    <p><label>Username: <input type="text" name="username" autocomplete="username"/></label></p>
				    <p><label>Password: <input type="password" name="password" autocomplete="current-password"/></label></p>
				    <button type="submit">Sign in</button>
				  </form>
				</body>
				</html>
				""".formatted(message, csrfField));
	}
}