package org.test.wex.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.test.wex.domain.Transaction;
import org.test.wex.dto.TransactionRequestDTO;
import org.test.wex.dto.TransactionResponseDTO;
import org.test.wex.repository.TransactionRepository;

@Service
@Slf4j
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repository;
    private final ModelMapper modelMapper;

    @Autowired
    public TransactionServiceImpl(TransactionRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionResponseDTO persistPurchaseTransaction(TransactionRequestDTO dto) throws Exception {
        log.info(
            "Class=TransactionServiceImpl Method=persistPurchaseTransaction amount={} description=\"{}\" transactionDate={}",
            dto.amount, dto.description, dto.transactionDate
        );

        Transaction transaction = repository.save(modelMapper.map(dto, Transaction.class));

        return modelMapper.map(transaction, TransactionResponseDTO.class);
    }

}
