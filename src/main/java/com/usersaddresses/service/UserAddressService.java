package com.usersaddresses.service;

import com.usersaddresses.domain.Address;
import com.usersaddresses.domain.User;
import com.usersaddresses.errorhandling.UserNotExistsException;
import com.usersaddresses.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.h2.jdbc.JdbcSQLIntegrityConstraintViolationException;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserAddressService {
    private final UserRepository repository;

    @Transactional
    public void addNewUser(User user) {
        log.info("New user will be added {}",user.toString());
        repository.save(user);
    }

    public User getUser(String login) {
        log.info("Get user data for login{}",login);
        return repository.findUserByLogin(login).orElseThrow(() -> {throw new UserNotExistsException("User does not exists");});
    }

    public void addAddressToUser(String login, Address address) {
        log.info("New address for user with login {} will be added, address: {}.", login, address.toString());
        repository.findUserByLogin(login).ifPresentOrElse(
                user1 -> {user1.getAddressList().add(address);
                repository.save(user1);},
            () -> {throw new UserNotExistsException("User does not exists");});
    }
}

