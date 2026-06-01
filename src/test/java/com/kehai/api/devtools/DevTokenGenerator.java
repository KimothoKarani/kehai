package com.kehai.api.devtools;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import java.util.Date;
import java.util.List;

public class DevTokenGenerator {

    private static final String SECRET =
            "kehai-local-dev-secret-that-is-long-enough-32ch";

    public static void main(String[] args) throws Exception {
        String tenantId = "test-tenant";
        String userId = "dev-user";
        List<String> roles = List.of("ROLE_ADMIN");
        long ttlMillis = 24L * 60 * 60 * 1000; // 24 hours

        JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.HS256)
                .type(JOSEObjectType.JWT)
                .build();

        Date now = new Date();
        Date expiry = new Date(now.getTime() + ttlMillis);

        JWTClaimsSet claims = new JWTClaimsSet.Builder()
                .subject(userId)
                .claim("tenant_id", tenantId)
                .claim("roles", roles)
                .issueTime(now)
                .expirationTime(expiry)
                .build();

        SignedJWT signedJWT = new SignedJWT(header, claims);
        signedJWT.sign(new MACSigner(SECRET.getBytes()));

        String token = signedJWT.serialize();

        System.out.println();
        System.out.println("=== Kehai Development Token ===");
        System.out.println("tenant_id : " + tenantId);
        System.out.println("user      : " + userId);
        System.out.println("roles     : " + roles);
        System.out.println("expires   : " + expiry);
        System.out.println();
        System.out.println("Authorization: Bearer " + token);
        System.out.println();
    }
}
