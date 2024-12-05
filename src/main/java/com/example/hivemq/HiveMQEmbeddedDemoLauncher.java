/*
 *    Copyright 2024-present Jan Haenel
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
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
