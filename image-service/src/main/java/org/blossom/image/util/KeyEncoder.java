package org.blossom.image.util;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

@Component
public class KeyEncoder {

    public String generateKey(Encoder encoder, Appender appender, String value) {
        return encode(encoder, value) + append(appender)
                .replace(" ", "-")
                .replace("/", "-")
                .replace(".", "-");
    }

    private String append(Appender appender) {
        if (Objects.requireNonNull(appender) == Appender.TIMESTAMP) {
            return Appender.TIMESTAMP.append();
        }
        return "";
    }

    private String encode(Encoder encoder, String value) {
        if (Objects.requireNonNull(encoder) == Encoder.BASE64) {
            return Encoder.BASE64.encode(value.getBytes());
        }
        return value;
    }


    public enum Encoder {
        BASE64(value -> Base64.getEncoder().encodeToString(value));

        final Function<byte[], String> encodeFunction;
        Encoder(Function<byte[], String> encodeFunction) {
            this.encodeFunction = encodeFunction;
        }

        public String encode(byte[] value) {
            return encodeFunction.apply(value);
        }
    }

    public enum Appender {
        TIMESTAMP(() -> String.valueOf(System.currentTimeMillis()));

        final Supplier<String> appendSupplier;
        Appender(Supplier<String> appendSupplier) {
            this.appendSupplier = appendSupplier;
        }

        public String append() {
            return appendSupplier.get();
        }
    }
}
