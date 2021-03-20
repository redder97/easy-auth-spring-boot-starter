package com.supplyfy.core.domain;

import java.util.Optional;

import com.supplyfy.core.domain.model.WebAccount;
import org.springframework.data.repository.CrudRepository;


public interface WebAccountRepository extends CrudRepository<WebAccount, Long> {

    Optional<WebAccount> findByEmail(String email);

    Boolean existsByEmail(String email);
}
