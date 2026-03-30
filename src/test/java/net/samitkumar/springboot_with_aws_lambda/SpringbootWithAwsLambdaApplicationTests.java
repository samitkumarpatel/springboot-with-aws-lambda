package net.samitkumar.springboot_with_aws_lambda;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.client.RestTestClient;

@SpringBootTest
@AutoConfigureRestTestClient
class SpringbootWithAwsLambdaApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	@DisplayName("Controller Test")
	void controllerTest(@Autowired RestTestClient restTestClient) {

		restTestClient
				.get()
				.uri("/ping")
				.exchange()
				.expectStatus().isOk()
						.expectBody().json("""
						{
							"message": "pong"
						}
						""");

		restTestClient
				.get()
				.uri("/functional/ping")
				.exchange()
				.expectStatus()
				.isOk()
				.expectBody()
				.json("""
						{
							"message": "pong function"
						}
						""");

	}
}
