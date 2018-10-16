package lu.luxtrust.flowers.tools;

import lu.luxtrust.flowers.properties.KeyStoreProperties;

import java.security.KeyStore;

public class KeyStoreWithConfig {
    private final KeyStore keyStore;
    private final KeyStoreProperties properties;

    public KeyStoreWithConfig(KeyStore keyStore, KeyStoreProperties properties) {
        this.keyStore = keyStore;
        this.properties = properties;
    }

    public KeyStore getKeyStore() {
        return keyStore;
    }

    public KeyStoreProperties getProperties() {
        return properties;
    }
}
