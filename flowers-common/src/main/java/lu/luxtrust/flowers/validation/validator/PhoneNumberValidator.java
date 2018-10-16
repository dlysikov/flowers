package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.validation.annotation.PhoneNumber;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidatorContext;

public class PhoneNumberValidator extends AbstractConstraintValidator<PhoneNumber, String> {
    @Override
    public void initialize(PhoneNumber phoneNumber) {

    }

    @Override
    public boolean isValid(String phoneNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return true;
        }
        return checkPhoneNumberCodes(phoneNumber) == null;
    }

    private static String checkPhoneNumberCodes(String phoneNumber){
        String prefixLux = "+352";
        String acceptablesCellPhoneExtensions[] = { "+" };

        if (StringUtils.isEmpty(phoneNumber)){
            return "ERR_ORDER_INVALID_PHONE_NUMBER";
        }

        // non null and non empty phone number

        if (!phoneNumber.startsWith(prefixLux)){
            // check phone number's length - minimum 5, maximum 20 (with extension)
            if ( phoneNumber.length() >= 5 && phoneNumber.length() <= 20 ) {
                String ext = phoneNumber.substring(0,1);
                String num = phoneNumber.substring(1);

                boolean extOK = false;
                // if the extension was specified, check the extension
                if ( ext != "" ) {
                    for ( int i = 0; i < acceptablesCellPhoneExtensions.length; i++ ) {
                        if ( ext.equals(acceptablesCellPhoneExtensions[i] ) ) {
                            extOK = true;
                            break;
                        }
                    }
                    if ( !extOK ){
                        return "ERR_ORDER_INVALID_PHONE_NUMBER_EXTENSION";
                    }
                }

                // valid phone's extension (if specified) and phone's prefix
                // => check the number
                try {
                    Double.parseDouble(num);
                } catch (NumberFormatException ignored) {
                    return "ERR_ORDER_INVALID_PHONE_NUMBER";
                }

                // valid phone's extension and phone's number
                if (extOK) {
                    return null;
                }
            } else {
                return "ERR_ORDER_INVALID_PHONE_NUMBER";
            }
        }else{
            return checkPhoneNumberOTP(phoneNumber);
        }
        // valid phone's extension (if specified), phone's prefix and phone's number
        return null;
    }

    private static String checkPhoneNumberOTP(String phoneNumber){
        String acceptablesCellPhoneExtensions[] = { "+352" };
        String acceptablesCellPhonePrefix[] = { "621", "661", "671", "691" };

        if (StringUtils.isEmpty(phoneNumber)){
            return "ERR_ORDER_INVALID_PHONE_NUMBER";
        }

        // non null and non empty phone number
        // check phone number's length - 13 with extension, 9 without extension
        if ( phoneNumber.length() == 13 || phoneNumber.length() == 9 ) {
            String ext = ( phoneNumber.length() == 13 ? phoneNumber.substring(0,4) : "" );
            String prefix = ( phoneNumber.length() == 13 ? phoneNumber.substring(4, 7) : phoneNumber.substring(0, 3) );
            String num = ( phoneNumber.length() == 13 ? phoneNumber.substring(7) : phoneNumber.substring(3) );

            boolean extOK = false;
            boolean prefixOK = false;
            boolean numOK;

            // if the extension was specified, check the extension
            if ( ext != "" ) {
                for ( int i = 0; i < acceptablesCellPhoneExtensions.length; i++ ) {
                    if ( ext.equals(acceptablesCellPhoneExtensions[i] ) ) {
                        extOK = true;
                        break;
                    }
                }

                if ( !extOK ){
                    return "ERR_ORDER_INVALID_PHONE_NUMBER_EXTENSION";
                }
            }

            // check the number phone's prefix
            for ( int i = 0; i < acceptablesCellPhonePrefix.length; i++ ) {
                if ( prefix.equals( acceptablesCellPhonePrefix[i] ) ) {
                    prefixOK = true;
                    break;
                }
            }

            if ( !prefixOK ){
                return "ERR_ORDER_INVALID_PHONE_NUMBER_PREFIX";
            }

            // valid phone's extension (if specified) and phone's prefix
            // => check the number
            try {
                Integer.parseInt(num);
                numOK = true;
            } catch (NumberFormatException ignored) {
                return "ERR_ORDER_INVALID_PHONE_NUMBER";
            }

            // valid phone's extension (if specified), phone's prefix and phone's number
            if ( numOK && prefixOK && (ext != "" ? extOK : true)){
                return null;
            }
        } else {
            return "ERR_ORDER_INVALID_PHONE_NUMBER";
        }

        // valid phone's extension (if specified), phone's prefix and phone's number
        return null;
    }
}
