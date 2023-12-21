package org.blossom.image.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KeyEncoderTest {

    @Test
    public void testGenerateKey() {
        KeyEncoder keyEncoder = new KeyEncoder();

        String generatedKey = keyEncoder.generateKey(
                KeyEncoder.Encoder.BASE64,
                KeyEncoder.Appender.TIMESTAMP,
                "inputValue"
        );

        String expectedKey = KeyEncoder.Encoder.BASE64.encode("inputValue".getBytes()) +
                KeyEncoder.Appender.TIMESTAMP.append()
                        .replace(" ", "-")
                        .replace("/", "-")
                        .replace(".", "-");

        Assertions.assertEquals(expectedKey, generatedKey);
    }
}