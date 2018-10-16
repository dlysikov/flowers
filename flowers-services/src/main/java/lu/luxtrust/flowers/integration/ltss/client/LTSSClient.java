package lu.luxtrust.flowers.integration.ltss.client;

import lu.luxtrust.flowers.model.ESealCredentials;
import lu.luxtrust.ltss.dto.PublicKeyDTO;
import lu.luxtrust.ltss.dto.ResultDTO;

public interface LTSSClient {
    PublicKeyDTO generateKeys();
    ResultDTO destroyKeys(String keyId);
    ResultDTO activateESealAccount(ESealCredentials eSealCredentials);
}
