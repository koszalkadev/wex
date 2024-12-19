CREATE TABLE IF NOT EXISTS transaction_retrieve_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    transaction_id UUID NOT NULL,
    currency VARCHAR(30) NOT NULL,
    exchange_rate DOUBLE NOT NULL,
    converted_amount NUMERIC(15, 2) NOT NULL,
    retrieve_timestamp TIMESTAMP,
    FOREIGN KEY (transaction_id) REFERENCES transaction(id)
);