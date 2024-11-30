package com.example.hivemq;

import com.example.hivemq.boot.starter.services.HiveMQEmbeddedStarter;
import com.hivemq.embedded.EmbeddedExtension;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class HiveMQEmbeddedDemoLauncher {

	@Bean
	public CommandLineRunner startup(final ApplicationContext ctx, final HiveMQEmbeddedStarter hiveMQ) {
		return args -> {
			// Do what ever we want ...
			// ...

			// Now start HiveMQ
			hiveMQ.startup();

			// Do other stuff ...
			// ...
		};
	}

	@Bean
	public EmbeddedExtension embeddedExtension1() {
		return EmbeddedExtension.builder()
				.withId("my-embedded-extension 1")
				.withName("My Embedded Extension #1")
				.withVersion("1.0.0")
				.withPriority(0)
				.withStartPriority(1000)
				.withAuthor("Me")
				.withExtensionMain(new MyEmbeddedExtension(1))
				.build();
	}

	@Bean
	public EmbeddedExtension embeddedExtension2() {
		return EmbeddedExtension.builder()
				.withId("my-embedded-extension 2")
				.withName("My Embedded Extension #2")
				.withVersion("1.0.0")
				.withPriority(10)
				.withStartPriority(2000)
				.withAuthor("Me")
				.withExtensionMain(new MyEmbeddedExtension(2))
				.build();
	}

	public static void main(String[] args) {
		SpringApplication.run(HiveMQEmbeddedDemoLauncher.class, args);
	}
}
