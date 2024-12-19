package org.test.wex.unit.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.test.wex.controller.TransactionController;
import org.test.wex.domain.Transaction;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.dto.TransactionRetrieveResponseDTO;
import org.test.wex.exception.DefaultExceptionHandler;
import org.test.wex.exception.ReportingRatesNotFoundException;
import org.test.wex.exception.TransactionNotFoundException;
import org.test.wex.exception.ValidationExceptionHandler;
import org.test.wex.service.TransactionService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.test.wex.constants.ErrorMessages.*;

@ExtendWith(MockitoExtension.class)
public class TransactionControllerTest {

    private MockMvc mockMvc;

    @Mock
    private TransactionService service;

    @InjectMocks
    private TransactionController controller;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new DefaultExceptionHandler(), new ValidationExceptionHandler())
                .build();
    }

    @Test
    void testPurchaseTransaction() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );
        String transactionId = UUID.randomUUID().toString();
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transactionId,
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now()
        );

        when(service.persistPurchaseTransaction(any(TransactionRequestDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(transactionId))
                .andReturn();
    }



    @Test
    void testGetPurchaseTransactionById() throws Exception {
        String transactionId = UUID.randomUUID().toString();
        TransactionResponseDTO responseDTO = new TransactionResponseDTO(
                transactionId,
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now()
        );

        when(service.getPurchaseTransactionById(transactionId)).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/transaction/purchase/{id}", transactionId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("id").value(transactionId))
                .andReturn();
    }

    @Test
    void testPurchaseTransactionWithInternalServerError() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );

        doThrow(new RuntimeException("Simulated error"))
                .when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andReturn();
    }

    @Test
    void testGetPurchaseTransactionByIdWithInternalServerError() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        doThrow(new RuntimeException("Simulated error"))
                .when(service).getPurchaseTransactionById(transactionId);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/transaction/purchase/{id}", transactionId))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.INTERNAL_SERVER_ERROR.value()))
                .andReturn();
    }

    @Test
    void testGetPurchaseTransactionByIdWithTransactionNotFoundError() throws Exception {
        String transactionId = UUID.randomUUID().toString();

        doThrow(new TransactionNotFoundException())
                .when(service).getPurchaseTransactionById(transactionId);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/transaction/purchase/{id}", transactionId))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_TRANSACTION_NOT_FOUND_ERROR))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerSpecificBindException() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                null,
                "Test description",
                LocalDate.now()
        );

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(TRANSACTION_AMOUNT_NOT_NULL_MESSAGE))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerDefaultBindEmptyErrorsException() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(20.25),
                "Test description",
                LocalDate.now()
        );

        BindException bindException = new BindException(requestDTO, "purchaseTransactionRequestDTO");
        doThrow(bindException).when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_VALIDATION_ERROR))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerDefaultBindException() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(20.25),
                "Test description",
                LocalDate.now()
        );

        // Inject a mock BindException to simulate a validation error
        BindException bindException = new BindException(requestDTO, "purchaseTransactionRequestDTO");
        bindException.addError(new FieldError("purchaseTransactionRequestDTO", "amount", DEFAULT_VALIDATION_ERROR));

        doThrow(bindException).when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_VALIDATION_ERROR))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerDefaultBindWithNullMessageException() throws Exception {
        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(20.25),
                "Test description",
                LocalDate.now()
        );

        // Inject a mock BindException to simulate a validation error
        BindException bindException = new BindException(requestDTO, "purchaseTransactionRequestDTO");
        bindException.addError(new FieldError("purchaseTransactionRequestDTO", "amount", null));

        doThrow(bindException).when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));


        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_VALIDATION_ERROR))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerSpecificConstraintViolation() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );
        Transaction mockedSavedTransaction = new Transaction(
                UUID.randomUUID(),
                null,
                "Test description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );
        Set<ConstraintViolation<Transaction>> violations = validator.validate(mockedSavedTransaction);

        ConstraintViolationException exception = new ConstraintViolationException(new HashSet<>(violations));
        doThrow(exception).when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(TRANSACTION_AMOUNT_NOT_NULL_MESSAGE))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerDefaultConstraintViolation() throws Exception {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();

        TransactionRequestDTO requestDTO = new TransactionRequestDTO(
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now()
        );
        Transaction mockedSavedTransaction = new Transaction(
                UUID.randomUUID(),
                BigDecimal.valueOf(21.25),
                "Test description",
                LocalDate.now(),
                LocalDateTime.now(),
                null
        );
        Set<ConstraintViolation<Transaction>> violations = validator.validate(mockedSavedTransaction);

        ConstraintViolationException exception = new ConstraintViolationException(new HashSet<>(violations));
        doThrow(exception).when(service).persistPurchaseTransaction(any(TransactionRequestDTO.class));

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(requestDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_VALIDATION_ERROR))
                .andReturn();
    }

    @Test
    void testValidationExceptionHandlerDateTimeParseException() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .post("/transaction/purchase")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(
                                "{\"amount\":21.25,\"description\":\"Test description\", \"transactionDate\":\"2023/12/01\"}"
                        ))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_DATE_FORMAT_ERROR))
                .andReturn();
    }

    @Test
    void testRetrieveTransaction() throws Exception {
        String transactionId = UUID.randomUUID().toString();
        String request = "/transaction/retrieve?".concat("id="+transactionId)
                .concat("&currency=").concat("Real");
        TransactionRetrieveResponseDTO responseDTO = new TransactionRetrieveResponseDTO(
                transactionId,
                "Test description",
                LocalDate.now(),
                BigDecimal.valueOf(21.25),
                5.033,
                BigDecimal.valueOf(21.25 * 5.033).setScale(2, RoundingMode.HALF_EVEN)
        );

        when(service.retrievePurchaseTransaction(anyString(), anyString(), any())).thenReturn(responseDTO);

        mockMvc.perform(MockMvcRequestBuilders
                        .get(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("transactionId").value(transactionId))
                .andExpect(jsonPath("convertedAmount").value(106.95))
                .andReturn();
    }

    @Test
    void testRetrieveTransactionMissingParameterException() throws Exception {
        String request = "/transaction/retrieve?".concat("currency=").concat("Real");

        mockMvc.perform(MockMvcRequestBuilders
                        .get(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value("id parameter is missing."))
                .andReturn();
    }

    @Test
    void testRetrieveTransactionMissingParameterNoParamException() throws Exception {
        String transactionId = UUID.randomUUID().toString();
        String request = "/transaction/retrieve?".concat("id="+transactionId)
                .concat("&currency=").concat("Real");
        TransactionRetrieveResponseDTO responseDTO = new TransactionRetrieveResponseDTO(
                transactionId,
                "Test description",
                LocalDate.now(),
                BigDecimal.valueOf(21.25),
                5.033,
                BigDecimal.valueOf(21.25 * 5.033).setScale(2, RoundingMode.HALF_EVEN)
        );

        MissingServletRequestParameterException exception = new MissingServletRequestParameterException("","");

        doThrow(exception).when(service).retrievePurchaseTransaction(anyString(), anyString(), any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_MISSING_PARAMETERS_ERROR))
                .andReturn();
    }


    @Test
    void testRetrieveTransactionExchangeRatesNotFoundException() throws Exception {
        String transactionId = UUID.randomUUID().toString();
        String request = "/transaction/retrieve?".concat("id="+transactionId)
                .concat("&currency=").concat("Real");
        TransactionRetrieveResponseDTO responseDTO = new TransactionRetrieveResponseDTO(
                transactionId,
                "Test description",
                LocalDate.now(),
                BigDecimal.valueOf(21.25),
                5.033,
                BigDecimal.valueOf(21.25 * 5.033).setScale(2, RoundingMode.HALF_EVEN)
        );

        ReportingRatesNotFoundException exception = new ReportingRatesNotFoundException();
        doThrow(exception).when(service).retrievePurchaseTransaction(anyString(), anyString(), any());

        mockMvc.perform(MockMvcRequestBuilders
                        .get(request)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
                .andExpect(jsonPath("status").value(HttpStatus.NOT_FOUND.value()))
                .andExpect(jsonPath("detail").value(DEFAULT_NO_REPORTING_RATES_ERROR))
                .andReturn();
    }
}

