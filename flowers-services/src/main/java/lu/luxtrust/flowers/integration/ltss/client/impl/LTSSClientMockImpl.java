package lu.luxtrust.flowers.integration.ltss.client.impl;

import lu.luxtrust.flowers.integration.ltss.client.LTSSClient;
import lu.luxtrust.flowers.model.ESealCredentials;
import lu.luxtrust.ltss.dto.PublicKeyDTO;
import lu.luxtrust.ltss.dto.ResultCodeDTO;
import lu.luxtrust.ltss.dto.ResultDTO;

public class LTSSClientMockImpl implements LTSSClient {
    @Override
    public PublicKeyDTO generateKeys() {
        PublicKeyDTO publicKeyDTO = new PublicKeyDTO();
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResultCode(ResultCodeDTO.OK);
        resultDTO.setMessage("Generation of keys is successful");
        publicKeyDTO.setExponent("exponent");
        publicKeyDTO.setModulus("modulus");
        publicKeyDTO.setKeyId("keyId");
        publicKeyDTO.setResult(resultDTO);
        return publicKeyDTO;
    }

    @Override
    public ResultDTO activateESealAccount(ESealCredentials eSealCredentials) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResultCode(ResultCodeDTO.OK);
        resultDTO.setMessage("Activation was done successful");
        return resultDTO;
    }

    @Override
    public ResultDTO destroyKeys(String keyId) {
        ResultDTO resultDTO = new ResultDTO();
        resultDTO.setResultCode(ResultCodeDTO.OK);
        resultDTO.setMessage("Destroying of keys was done successful");
        return resultDTO;
    }
}
