package lu.luxtrust.flowers.service.impl;

import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.service.OrderRandomsGeneratorGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.Security;

@Service
public class OrderRandomsGeneratorGeneratorImpl implements OrderRandomsGeneratorGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRandomsGeneratorGeneratorImpl.class);

    private static final int HASH_LENGTH = 32;

    public OrderRandomsGeneratorGeneratorImpl(@Value("${security.securerandom.source}") String secureRandomSource) {
        if (!StringUtils.isEmpty(secureRandomSource)) {
            LOGGER.info("Secure random source is {}", secureRandomSource);
            Security.setProperty("securerandom.source", secureRandomSource);
        }
    }

    @Override
    public String hash() {
        try {
            SecureRandom sha1PRNG = SecureRandom.getInstance("SHA1PRNG");

            StringBuilder sb = new StringBuilder();
            for (int i = 0;i < HASH_LENGTH;i++) {
                sb.append(Integer.toHexString(sha1PRNG.nextInt()).substring(1, 3));
            }

            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new FlowersException(e);
        }
    }

    @Override
    public String codeWithDigits(int length) {
        try {
            SecureRandom sha1PRNG = SecureRandom.getInstance("SHA1PRNG");
            StringBuilder sb = new StringBuilder();
            while (length-- > 0) {
                sb.append(Integer.toString(sha1PRNG.nextInt(10)));
            }
            return sb.toString();
        }catch (NoSuchAlgorithmException e) {
            throw new FlowersException(e);
        }
    }
}
