package lu.luxtrust.flowers.validation.validator;

import lu.luxtrust.flowers.validation.annotation.TokenSerialNumber;

import javax.validation.ConstraintValidatorContext;

public class TokenSerialNumberContentValidator extends AbstractConstraintValidator<TokenSerialNumber, String> {


    @Override
    public void initialize(TokenSerialNumber tokenSerialNumber) {

    }

    @Override
    public boolean isValid(String tokenSerialNumber, ConstraintValidatorContext constraintValidatorContext) {
        if (tokenSerialNumber == null || tokenSerialNumber.isEmpty()) {
            return true;
        }
        if (tokenSerialNumber.length() > 13) {
            return false;
        }
        String[] parts = tokenSerialNumber.split("-");
        return parts.length == 3 && checkToken(parts[0], parts[1], parts[2]);
    }

    private boolean checkToken(String token1, String token2, String token3){
        String token = token1 + token2 + token3;
        int sumOdd = 0;
        int sumEven = 0;

        try {
            for ( int i = 0; i < token.length()-1; i++ ){
                if ( i%2 == 0 ){
                    sumOdd = sumOdd + Integer.parseInt( String.valueOf(token.charAt(i) ) );
                } else {
                    sumEven = sumEven + Integer.parseInt( String.valueOf(token.charAt(i) ) );
                }
            }

            int sum = (sumOdd * 3) + sumEven;
            sum = 10 - (sum % 10);

            if(sum == 10){
                sum = 0;
            }

            return sum == (Integer.parseInt(token3));
        } catch ( NumberFormatException ignored ) {
            return false;
        }
    }
}
