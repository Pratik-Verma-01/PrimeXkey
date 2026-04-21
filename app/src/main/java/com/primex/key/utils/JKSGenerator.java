package com.primex.key.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.math.BigInteger;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.util.Date;

import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

public class JKSGenerator {

    public static boolean generateAndSave(
            String alias,
            String storePass,
            String keyPass,
            String commonName,
            String organization,
            String city,
            int validityYears,
            int keySize,
            String algo,
            File savePath) {

        try {
            // 1. RSA Key Pair Generate karna
            KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
            keyPairGen.initialize(keySize, new SecureRandom());
            KeyPair keyPair = keyPairGen.generateKeyPair();

            // 2. BouncyCastle se Certificate Generate karna
            X500Name issuer = new X500Name("CN=" + commonName + ", O=" + organization + ", L=" + city);
            BigInteger serial = new BigInteger(64, new SecureRandom());
            Date notBefore = new Date();
            long validityMillis = (long) validityYears * 365 * 24 * 60 * 60 * 1000L;
            Date notAfter = new Date(notBefore.getTime() + validityMillis);

            JcaX509v3CertificateBuilder certBuilder = new JcaX509v3CertificateBuilder(
                    issuer, serial, notBefore, notAfter, issuer, keyPair.getPublic()
            );

            ContentSigner signer = new JcaContentSignerBuilder(algo).build(keyPair.getPrivate());
            X509CertificateHolder certHolder = certBuilder.build(signer);
            Certificate certificate = new JcaX509CertificateConverter().getCertificate(certHolder);

            // 3. JKS Format mein KeyStore banakar save karna
            KeyStore keyStore = KeyStore.getInstance("JKS");
            keyStore.load(null, storePass.toCharArray());

            Certificate[] certChain = new Certificate[]{certificate};
            keyStore.setKeyEntry(alias, keyPair.getPrivate(), keyPass.toCharArray(), certChain);

            FileOutputStream fos = new FileOutputStream(savePath);
            keyStore.store(fos, storePass.toCharArray());
            fos.close();

            return true;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
