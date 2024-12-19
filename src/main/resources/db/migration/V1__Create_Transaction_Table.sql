CREATE TABLE transactions (
      id UUID PRIMARY KEY NOT NULL,
      description VARCHAR(50),
      purchase_amount DECIMAL(12, 2),
      transaction_date DATE
);
