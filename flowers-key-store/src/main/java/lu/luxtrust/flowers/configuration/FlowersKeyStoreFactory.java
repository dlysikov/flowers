package lu.luxtrust.flowers.configuration;

import com.safenetinc.luna.LunaSlotManager;
import com.safenetinc.luna.provider.LunaProvider;
import lu.luxtrust.flowers.properties.KeyStoreProperties;
import org.apache.xml.security.Init;
import org.apache.xml.security.algorithms.JCEMapper;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.Security;

public class FlowersKeyStoreFactory {

    public KeyStore createKeyStore(KeyStoreProperties keyStoreProperties, KeyStoreProperties.KeyStoreSpiType keyStoreType) throws Exception {
        if (keyStoreType == KeyStoreProperties.KeyStoreSpiType.HSM) {
            if (Security.getProvider("LunaProvider") == null) {
                Security.addProvider(new LunaProvider());
            }
            LunaSlotManager slotManager = LunaSlotManager.getInstance();
            slotManager.login(keyStoreProperties.getHsm().getTokenLabel(), keyStoreProperties.getHsm().getPassword());
            KeyStore keyStore = KeyStore.getInstance("Luna", "LunaProvider");
            keyStore.load(new ByteArrayInputStream(("tokenlabel:" + keyStoreProperties.getHsm().getTokenLabel()).getBytes()),  keyStoreProperties.getHsm().getPassword().toCharArray());

            System.setProperty("flowers.keystore.type", "HSM");
            Init.init();

            JCEMapper.register("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p", new JCEMapper.Algorithm("", "RSA/ECB/OAEPWithSHA1AndMGF1Padding"));
            JCEMapper.setProviderId("LunaProvider");
            return keyStore;
        } else if (keyStoreType == KeyStoreProperties.KeyStoreSpiType.FILE){
            KeyStore keyStore = KeyStore.getInstance(keyStoreProperties.getFile().getKeystoreType());
            keyStore.load(new FileInputStream(keyStoreProperties.getFile().getKeystorePath()), keyStoreProperties.getFile().getKeystorePassword().toCharArray());
            System.setProperty("flowers.keystore.type", "FILE");
            return keyStore;
        } else {
            return null;
        }
    }
}
