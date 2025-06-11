package com.example.petshopbackend.service;
import java.math.BigDecimal;
public interface WalletService {
    void recharge(String username, BigDecimal amount);
    BigDecimal getBalance(String username);
}
