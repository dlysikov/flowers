package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.entity.common.Country;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifier;
import lu.luxtrust.flowers.entity.enrollment.CompanyIdentifierHolder;
import lu.luxtrust.flowers.exception.FlowersException;
import lu.luxtrust.flowers.validation.CountryVatDictionary;
import lu.luxtrust.flowers.validation.annotation.CompanyIdentifierValid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CompanyIdentifierValidator extends AbstractConstraintValidator<CompanyIdentifierValid, CompanyIdentifierHolder> {

    private static final String EMPTY = "org.hibernate.validator.constraints.NotEmpty.message";

    @Autowired
    CountryVatDictionary config;

    private CompanyIdentifierValid companyIdentifierValid;

    @Override
    public void initialize(CompanyIdentifierValid companyIdentifierValid) {
        this.companyIdentifierValid = companyIdentifierValid;
    }

    @Override
    public boolean isValid(CompanyIdentifierHolder company, ConstraintValidatorContext ctx) {
        CompanyIdentifier identifier = company.getIdentifier();
        Country country = company.getCountry();
        if (companyIdentifierValid.fullyEmptyAccepted() && (identifier == null || (identifier.getType() == null && StringUtils.isEmpty(identifier.getValue())))) {
            return true;
        }
        boolean valid = true;
        if (identifier == null || identifier.getType() == null) {
            addConstraintViolation("identifier.type", ctx, EMPTY);
            valid = false;
        }
        if (identifier == null || StringUtils.isEmpty(identifier.getValue())) {
            addConstraintViolation("identifier.value", ctx, EMPTY);
            valid = false;
        }
        if (!valid) {
            return Boolean.FALSE;
        }
        if (identifier.getValue() != null) {
            switch (identifier.getType()) {
                case VAT:
                    valid = checkVat(identifier.getValue());
                    break;
                case RCSL:
                    if (country == null) {
                        addConstraintViolation("country", ctx, EMPTY);
                        return false;
                    }
                    valid = checkRCSL(country, identifier.getValue());
                    break;
                case OTHER:
                    return true;
                default:
                    throw new FlowersException("unsupported company identifier type: " + identifier.getType());
            }
        }
        if (valid) {
            return true;
        }
        addConstraintViolation("identifier.value", ctx, "invalid");
        return false;
    }

    private boolean checkRCSL(Country country, String number) {
        if (!config.isRcslValid(country)) {
            return false;
        }
        if ("lu".equals(country.getCountryCode())) {
            String regex = "^[A-J]{1}[0-9]{1,7}$";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(number);
            return matcher.matches();
        }
        return true;
    }

    private boolean checkVat(String number) {
        if (number == null || number.length() < 2) {
            return false;
        }
        String countryFromNumber = number.substring(0, 2).toLowerCase();
        Pattern pattern = config.getVatPattern(countryFromNumber);
        if (pattern == null) {
            return false;
        }
        Matcher matcher = pattern.matcher(number);
        return matcher.matches() && checkDigitVatNumber(countryFromNumber, number);
    }

    private boolean checkDigitVatNumber(String countryCode, String vatNumber) {

        // Check modulo 89 + Avoid 000000
        if ("lu".equals(countryCode)) {
            if (vatNumber.startsWith("LU98") || vatNumber.startsWith("LU99")) {
                return false;
            }
            String number = vatNumber.substring(2, 8);
            String checkDigit = vatNumber.substring(8, 10);
            Integer num = new Integer(number);
            if (num == 0) {
                return false;
            } else {
                Integer digit = new Integer(checkDigit);
                Integer modulo = num % 89;
                return digit.intValue() == modulo.intValue();
            }
        }

        // Check modulo 97 + Avoid 000000
        if ("be".equals(countryCode)) {
            String number = vatNumber.substring(2, 10);
            String checkDigit = vatNumber.substring(10, 12);
            Integer num = new Integer(number);
            if (num == 0) {
                return false;
            } else {
                Integer digit = new Integer(checkDigit);
                Integer modulo = 97 - (num % 97);
                return digit.intValue() == modulo.intValue();
            }
        }

        // 'FR'+ 2 digits (as validation key ) + 9 digits (as SIREN),
        // the first and/or the second value can also be a character - e.g. FRXX999999999
        // The French key is calculated as follow :
        //	Key = [ 12 + 3 * ( SIREN modulo 97 ) ] modulo 97,
        // for example  :
        //	Key = [ 12 + 3 * ( 404,833,048 modulo 97 ) ] modulo 97
        //		= [12 + 3*56] modulo 97 = 180 modulo 97
        //		= 83 so the tax number for 404,833,048 is FR 83,404,833,048
        //	source from : www.insee.fr
        if ("fr".equals(countryCode)) {
            String siren = vatNumber.substring(4, 13);
            String checkDigit = vatNumber.substring(2, 4);
            Integer num = new Integer(siren);
            Integer digit = new Integer(checkDigit);
            Integer key = (12 + (3 * (num % 97))) % 97;
            return digit.intValue() == key.intValue();
        }

        return true;
    }

}
