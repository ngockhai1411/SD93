package com.web.gpay.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Slf4j
@Component
public class SignatureUtils {

    public static void main(String[] args) throws Exception {
        String testInput = "merchant_code=MC001&request_id=20241020-007&amount=10000&customer_id=1&payment_method=BANK_ATM,INTERNATIONAL_CARD&embed_data=additional_data_if_needed";
        String privateKeyStr = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAMBgVcNeVIGqEysGfQjX7ZJzOgC3j1X4IPbZ2uUXjNdAkLLwidOvvrdofDwWP6smVkR6+ot+rMNWFgJ9o9mqeKCb4ZvJlD+MEpne0i4o2ptA9l8lzGWFaE9a2zlBfr1uVEBYqM86hybavlj6lg+au+P1htFxoeLX2syMTzMWbf09AgMBAAECgYEAparQcJytfbRvKWA/EPyStMnSK4foQgqaqLpXzW4Idd4+PjrzFn+EaAHs6vnl/ofXRsX5OWPvd5CNB7wW/H3XvkjIGnM6wGfg1aG3g7N9mSA2T+MKGNdPUsa3fn/+A7801Nre6pUPqPXHtaOpUtrcBg8STQ4BYNr8kkQDw5YXAekCQQDwjOLXmzem+UrHka5pirOcXKweZsjRVitENx2Dwb8bBYeL9JLkZttOPxLATO+4G0PBRfPh6Cp3xizQ6Uk9gzwbAkEAzLtf3wjFscOwyJyLkD2/yny5JXPDzALIJ1KzXwsc38GNwTqoEw51rBUeGLAiN0X7+HMkMaE2jgOiu28rKmqRhwJAOHDGFNKdhzb83cCHzMm3hNuVovh46+0s76+C5FREU8HDYQo8oQylLkHMU4R5xpfXGPUh2QKRXFMKVuY1whIVuwJBAKqOarb+stt94Fev+qCmoJGKyB9IStQ5eQ8xWVcThfFuaK6SNBT0FdnPGeyNR2PuvJPlMEDcQfubUcV+PmKutx8CQAzCCXrPpd3CvcYIMd0EFGfces2MwYrwLk3m2CZoSoWeHp5Tq3QP0qr0YFAIAu26muXRSCqifbZe33sJ/22g9eI=";

        String signature = generateSignature(testInput, privateKeyStr);
        System.out.println("Test Input: " + testInput);
        System.out.println("Generated Signature: " + signature);
    }

    public static String generateSignature(String input, String privateKeyBase64) throws Exception {
        try {
            byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyBase64);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initSign(privateKey);
            signature.update(input.getBytes());

            byte[] signatureBytes = signature.sign();
            return Base64.getEncoder().encodeToString(signatureBytes);
        } catch (Exception e) {
            log.error("Error generating signature", e);
            throw e;
        }
    }

    public static boolean verifySignature(String data, String signatureStr, String publicKeyBase64) throws Exception {
        try {
            byte[] publicKeyBytes = Base64.getDecoder().decode(publicKeyBase64);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(keySpec);

            Signature signature = Signature.getInstance("SHA256withRSA");
            signature.initVerify(publicKey);
            signature.update(data.getBytes());

            byte[] signatureBytes = Base64.getDecoder().decode(signatureStr);
            return signature.verify(signatureBytes);
        } catch (Exception e) {
            log.error("Error verifying signature", e);
            throw e;
        }
    }
}