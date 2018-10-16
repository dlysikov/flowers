package lu.luxtrust.flowers.security.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import lu.luxtrust.flowers.model.CertificateDetails;
import lu.luxtrust.flowers.properties.JWTProperties;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
public class JWTServiceImpl implements JWTService {

    private static final Logger LOGGER  = LoggerFactory.getLogger(JWTServiceImpl.class);

    private final JWTProperties props;
    private final ObjectMapper objectMapper;

    @Autowired
    public JWTServiceImpl(JWTProperties props, ObjectMapper objectMapper) {
        this.props = props;
        this.objectMapper = objectMapper;
    }

    @Override
    public void addTokenToResponse(HttpServletResponse response, RestAuthenticationToken token) {
        try {
            LocalDateTime jwtExpirationDate = LocalDateTime.now().plus(props.getTtl(), ChronoUnit.MILLIS);
            String jwt = Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(token.getDetails()))
                    .setExpiration(new Date(jwtExpirationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .signWith(props.getSignatureAlg(), props.getSecret().getBytes())
                    .compact();
            response.addHeader(props.getAuthHeader(), jwt);
        } catch (Exception e) {
            LOGGER.error("Cant generate JWT token: {}", e.getMessage());
        }
    }

    @Override
    public void addRefreshTokenToResponse(HttpServletResponse response, RestAuthenticationToken token) {
        try {
            LocalDateTime jwtRefreshExpirationDate = LocalDateTime.now().plus(props.getRefreshTokenTtl(), ChronoUnit.MILLIS);

            String refreshJwt = Jwts.builder()
                    .setSubject(objectMapper.writeValueAsString(token.getDetails()))
                    .setExpiration(new Date(jwtRefreshExpirationDate.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()))
                    .signWith(props.getSignatureAlg(), props.getRefreshTokenSecret().getBytes())
                    .compact();

            response.addHeader(props.getRefreshTokenHeader(), refreshJwt);
        } catch (Exception e) {
            LOGGER.error("Cant generate Refresh JWT token: {}", e.getMessage());
        }
    }

    @Override
    public String getToken(HttpServletResponse response) {
        return response.getHeader(props.getAuthHeader());
    }

    @Override
    public String getRefreshToken(HttpServletResponse response) {
        return response.getHeader(props.getRefreshTokenHeader());
    }

    @Override
    public String getToken(HttpServletRequest request) {
        return request.getHeader(props.getAuthHeader());
    }

    @Override
    public String getRefreshToken(HttpServletRequest request) {
        return request.getHeader(props.getRefreshTokenHeader());
    }

    @Override
    public CertificateDetails retrieveUserCertificateDetailsFromToken(HttpServletRequest request) {
        try {
            String json =  Jwts.parser()
                    .setSigningKey(props.getSecret().getBytes())
                    .parseClaimsJws(request.getHeader(props.getAuthHeader()))
                    .getBody()
                    .getSubject();
            return objectMapper.readValue(json, CertificateDetails.class);
        } catch (Exception e) {
            LOGGER.error("Cant parse JWT token {}, msg: {}",request.getHeader(props.getAuthHeader()),  e.getMessage());
            return null;
        }
    }

    @Override
    public CertificateDetails retrieveUserCertificateDetailsFromRefreshToken(HttpServletRequest request) {
        try {
            String json =  Jwts.parser()
                    .setSigningKey(props.getRefreshTokenSecret().getBytes())
                    .parseClaimsJws(request.getHeader(props.getRefreshTokenHeader()))
                    .getBody()
                    .getSubject();
            return objectMapper.readValue(json, CertificateDetails.class);
        } catch (Exception e) {
            LOGGER.error("Cant parse Refresh JWT token {}, msg: {}",request.getHeader(props.getRefreshTokenHeader()),  e.getMessage());
            return null;
        }
    }
}
