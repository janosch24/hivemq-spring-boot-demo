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

import com.hivemq.extension.sdk.api.ExtensionMain;
import com.hivemq.extension.sdk.api.annotations.NotNull;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundInput;
import com.hivemq.extension.sdk.api.interceptor.publish.parameter.PublishOutboundOutput;
import com.hivemq.extension.sdk.api.packets.general.Qos;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStartOutput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopInput;
import com.hivemq.extension.sdk.api.parameter.ExtensionStopOutput;
import com.hivemq.extension.sdk.api.services.CompletableScheduledFuture;
import com.hivemq.extension.sdk.api.services.Services;
import com.hivemq.extension.sdk.api.services.admin.LifecycleStage;
import com.hivemq.extension.sdk.api.services.builder.Builders;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
public class MyEmbeddedExtension implements ExtensionMain {

    private static final String topicBase = "ping";


    private final int number;
    private final Ping ping = new Ping();
    private final String topic;

    CompletableScheduledFuture<?> task = null;

    public MyEmbeddedExtension(int number) {
        this.number = number;
        this.topic = String.join("/", topicBase, String.valueOf(this.number));
    }

    @Override
    public void extensionStart(@NotNull ExtensionStartInput extensionStartInput, @NotNull ExtensionStartOutput extensionStartOutput) {
        log.info("Starting embedded extension #{} ...", this.number);

        if (this.number > 1) {
            try {
                Services.initializerRegistry()
                        .setClientInitializer((initializerInput, clientContext) ->
                                clientContext.addPublishOutboundInterceptor(this::onOutboundPublish));
            } catch (Exception e) {
                log.error("Exception thrown at extension start: ", e);
            }
        }

        // Finally start ...
        start();
    }

    @Override
    public void extensionStop(@NotNull ExtensionStopInput extensionStopInput, @NotNull ExtensionStopOutput extensionStopOutput) {
        log.info("Stopping embedded extension #{} ...", this.number);

        Optional.ofNullable(this.task)
                .ifPresent(task -> task.cancel(false));
    }

    private void start() {
        this.task =
                Services.extensionExecutorService()
                        .schedule(() -> {
                            if (Services.adminService().getCurrentStage() == LifecycleStage.STARTED_SUCCESSFULLY) {
                                pingOrWhatever();
                            } else {
                                // Try later ...
                                start();
                            }
                        }, 1, TimeUnit.SECONDS);
    }

    private void pingOrWhatever() {
        if (this.number == 1) {
            Services.extensionExecutorService()
                    .scheduleAtFixedRate(() -> Services.publishService()
                            .publish(Builders.publish()
                                    .topic(topic)
                                    .payload(ByteBuffer.wrap(this.ping.next().getBytes(StandardCharsets.UTF_8)))
                                    .qos(Qos.AT_LEAST_ONCE)
                                    .build()), 100, 100, TimeUnit.MILLISECONDS);
        }
    }

    private void onOutboundPublish(final @NotNull PublishOutboundInput input, final @NotNull PublishOutboundOutput output) {
        // As we do nat have a ConsumerService in HiveMQ-CE,
        // we cannot do much more than fiddling around with
        // messages that go in and out ...
        if (input.getPublishPacket().getTopic().startsWith(topicBase)) {
            if (input.getPublishPacket().getTopic().equals(this.topic))
                return;

            // If this message does not have our topic,
            // then we just generate a new one with manipulated topic/payload
            Services.publishService()
                    .publish(Builders.publish()
                            .topic(topic)
                            .payload(ByteBuffer.wrap(input.getPublishPacket()
                                            .getPayload()
                                            .map(this::pong)
                                            .orElse("".getBytes())))
                            .build())
                    .whenComplete(this::onPublishComplete);
        }
    }

    private byte[] pong(ByteBuffer buffer) {
        byte[] bytes = new byte[buffer.clear().capacity()];
        buffer.get(bytes);

        return new String(bytes, StandardCharsets.UTF_8)
                .replace("ping", "pong")
                .getBytes(StandardCharsets.UTF_8);
    }

    private void onPublishComplete(Void o, Throwable th) {
        if (th == null)
            log.trace("Completed publish for ping#{}.", this.number);
        else
            log.error("Publish failed for ping#{}.", this.number, th);
    }

    @Data
    private static class Ping
    {
        @Setter(AccessLevel.NONE)
        int ping = 0;

        public String next() {
            return """
                {
                   "ping": %d
                }
                """.formatted(++this.ping);
        }
    }
}
