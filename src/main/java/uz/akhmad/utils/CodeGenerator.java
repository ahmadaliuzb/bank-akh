package uz.akhmad.utils;

import com.mifmif.common.regex.Generex;

import static uz.akhmad.constants.constants.ACCOUNT_NUMBER_PATTERN_STRING;
import static uz.akhmad.constants.constants.SORT_CODE_PATTERN_STRING;

public class CodeGenerator {
    Generex sortCodeGenerex = new Generex(SORT_CODE_PATTERN_STRING);
    Generex accountNumberGenerex = new Generex(ACCOUNT_NUMBER_PATTERN_STRING);

    public CodeGenerator(){}

    public String generateSortCode() {
        return sortCodeGenerex.random();
    }

    public String generateAccountNumber() {
        return accountNumberGenerex.random();
    }
}
