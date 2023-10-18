package org.nurma.aqyndar.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Encoders;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class GenerateKeys {

    private GenerateKeys() {
    }

    public static void main(final String[] args) {
        log.info(generateKey());
        log.info(generateKey());
    }

    public static String generateKey() {
        return Encoders.BASE64.encode(Jwts.SIG.HS512.key().build().getEncoded());
    }

}
