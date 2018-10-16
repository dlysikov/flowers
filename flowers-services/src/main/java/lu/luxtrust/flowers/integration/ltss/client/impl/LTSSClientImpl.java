package lu.luxtrust.flowers.integration.ltss.client.impl;

import lu.luxtrust.flowers.integration.ltss.client.LTSSClient;
import lu.luxtrust.flowers.model.ESealCredentials;
import lu.luxtrust.ltss.dto.PublicKeyDTO;
import lu.luxtrust.ltss.dto.ResultDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;


public class LTSSClientImpl implements LTSSClient {


    @Value("${eseal.LTSS_URL}")
    private String lttsServiceURL;

    @Value("${eseal.generate_keys_url}")
    private String generateKeysURL;

    @Value("${eseal.destroy_keys_url}")
    private String destroyKeysURL;

    @Value("${eseal.activate_url}")
    private String activateURL;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public PublicKeyDTO generateKeys() {
        ResponseEntity<PublicKeyDTO> responseEntity = this.restTemplate.getForEntity(this.lttsServiceURL + this.generateKeysURL, PublicKeyDTO.class);
        return responseEntity.getBody();
    }

    @Override
    public ResultDTO destroyKeys(String keyId) {
        ResponseEntity<ResultDTO> responseEntity = this.restTemplate.getForEntity(this.lttsServiceURL + this.destroyKeysURL, ResultDTO.class);
        return responseEntity.getBody();
    }

    @Override
    public ResultDTO activateESealAccount(ESealCredentials eSealCredentials) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", MediaType.APPLICATION_FORM_URLENCODED_VALUE);
        MultiValueMap<String, String> map= new LinkedMultiValueMap<>();
        map.add("keyId", eSealCredentials.getKeyId());
        map.add("userId", eSealCredentials.getSealId());
        map.add("encryptedInitialPassword", eSealCredentials.getInitialPassword());
        map.add("encryptedNewPassword", eSealCredentials.getNewPassword());
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, httpHeaders);
        ResponseEntity<ResultDTO> responseEntity = this.restTemplate.postForEntity(this.lttsServiceURL + this.activateURL, request, ResultDTO.class);
        return responseEntity.getBody();
    }
}
