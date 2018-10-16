package lu.luxtrust.flowers.tools;

import com.safenetinc.luna.LunaSlotManager;
import lu.luxtrust.flowers.properties.KeyStoreProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Component
public class HsmHelper implements InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(HsmHelper.class);

    @Autowired(required = false)
    private List<KeyStoreWithConfig> keyStoresWithConfig;

    public HsmHelper() {
    }

    public void reconnect() throws Exception {
        try {
            generateSecureRandom();
            if (!LunaSlotManager.getInstance().isLoggedIn()) {
                this.reinitialize();
            }
        } catch (Throwable e) {
            reinitialize();
            generateSecureRandom();
        }
    }

    private void reinitialize() throws Exception {
        LOG.info("HSM is not logged in into HSM. Reinitializing connection and resigning in.");
        LunaSlotManager.getInstance().reinitialize();
        for (KeyStoreWithConfig props: this.keyStoresWithConfig) {
            if (props.getProperties().getType() == KeyStoreProperties.KeyStoreSpiType.HSM) {
                LOG.info("Signing in: HSM slot {}", props.getProperties().getHsm().getTokenLabel());
                LunaSlotManager slotManager = LunaSlotManager.getInstance();
                if (slotManager.login(props.getProperties().getHsm().getTokenLabel(), props.getProperties().getHsm().getPassword())) {
                    props.getKeyStore().load(new ByteArrayInputStream(("tokenlabel:" + props.getProperties().getHsm().getTokenLabel()).getBytes()),  props.getProperties().getHsm().getPassword().toCharArray());
                    LOG.info("Application is logged in into HSM slot {}", props.getProperties().getHsm().getTokenLabel());
                } else {
                    throw new Exception("Can't login into HSM for slot with label " + props.getProperties().getHsm().getTokenLabel());
                }
            }
        }
    }

    private void generateSecureRandom() throws Exception {
        LOG.info("Generating secure random value to test HSM availability");
        int randomInt = SecureRandom.getInstance("LunaRNG", "LunaProvider").nextInt();
        LOG.info("HSM generated secure random value: {}", randomInt);
    }

    @Override
    public void afterPropertiesSet() {
        if (this.keyStoresWithConfig == null) {
            this.keyStoresWithConfig = new ArrayList<>();
        }
    }
}
