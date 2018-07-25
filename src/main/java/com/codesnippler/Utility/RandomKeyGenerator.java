package com.codesnippler.Utility;

import javax.xml.bind.DatatypeConverter;
import java.security.SecureRandom;
import java.util.Base64;


public class RandomKeyGenerator {
    public static String generateApiKey(final int length) {
        SecureRandom random = new SecureRandom();
        byte bytes[] = new byte[length];
        random.nextBytes(bytes);
        Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();
        return encoder.encodeToString(bytes);
    }
}
