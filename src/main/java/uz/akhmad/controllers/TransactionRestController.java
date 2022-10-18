package uz.akhmad.controllers;

import uz.akhmad.constants.ACTION;
import uz.akhmad.models.Account;
import uz.akhmad.services.AccountService;
import uz.akhmad.services.TransactionService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import uz.akhmad.constants.constants;
import uz.akhmad.utils.DepositInput;
import uz.akhmad.utils.InputValidator;
import uz.akhmad.utils.TransactionInput;
import uz.akhmad.utils.WithdrawInput;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class TransactionRestController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TransactionRestController.class);

    private final AccountService accountService;
    private final TransactionService transactionService;

    @Autowired
    public TransactionRestController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostMapping(value = "/transactions",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> makeTransfer(
            @Valid @RequestBody TransactionInput transactionInput) {
        if (InputValidator.isSearchTransactionValid(transactionInput)) {
//            new Thread(() -> transactionService.makeTransfer(transactionInput));
            boolean isComplete = transactionService.makeTransfer(transactionInput);
            return new ResponseEntity<>(isComplete, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(constants.INVALID_TRANSACTION, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping(value = "/withdraw",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> withdraw(
            @Valid @RequestBody WithdrawInput withdrawInput) {
        LOGGER.debug("Triggered AccountRestController.withdrawInput");

        // Validate input
        if (InputValidator.isSearchCriteriaValid(withdrawInput)) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(
                    withdrawInput.getSortCode(), withdrawInput.getAccountNumber());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(constants.NO_ACCOUNT_FOUND, HttpStatus.OK);
            } else {
                if (transactionService.isAmountAvailable(withdrawInput.getAmount(), account.getCurrentBalance())) {
                    transactionService.updateAccountBalance(account, withdrawInput.getAmount(), ACTION.WITHDRAW);
                    return new ResponseEntity<>(constants.SUCCESS, HttpStatus.OK);
                }
                return new ResponseEntity<>(constants.INSUFFICIENT_ACCOUNT_BALANCE, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(constants.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }


    @PostMapping(value = "/deposit",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deposit(
            @Valid @RequestBody DepositInput depositInput) {
        LOGGER.debug("Triggered AccountRestController.depositInput");

        // Validate input
        if (InputValidator.isAccountNoValid(depositInput.getTargetAccountNo())) {
            // Attempt to retrieve the account information
            Account account = accountService.getAccount(depositInput.getTargetAccountNo());

            // Return the account details, or warn that no account was found for given input
            if (account == null) {
                return new ResponseEntity<>(constants.NO_ACCOUNT_FOUND, HttpStatus.OK);
            } else {
                transactionService.updateAccountBalance(account, depositInput.getAmount(), ACTION.DEPOSIT);
                return new ResponseEntity<>(constants.SUCCESS, HttpStatus.OK);
            }
        } else {
            return new ResponseEntity<>(constants.INVALID_SEARCH_CRITERIA, HttpStatus.BAD_REQUEST);
        }
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(
            MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }
}
