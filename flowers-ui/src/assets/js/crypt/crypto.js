/**
 * @return encrypt("dataB64")
 */
function encryptData(publicModulusB64, publicExponentB64, data) {
    var rsa = new RSAKey();
    var publicModulusHexa = b64ToHexa(publicModulusB64);
    var publicExponentHexa = b64ToHexa(publicExponentB64);
    var resHex = rsa.rsaPkcs1Encrypt(publicModulusHexa, publicExponentHexa, data);

    return hexaToB64(resHex);
}
