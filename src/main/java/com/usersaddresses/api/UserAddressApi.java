package com.usersaddresses.api;

import com.usersaddresses.domain.Address;
import com.usersaddresses.domain.User;
import javax.validation.Valid;

import com.usersaddresses.service.UserAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(
        path = "/useraddress",
        produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE
)
@RequiredArgsConstructor
@Slf4j
public class UserAddressApi {
    private final UserAddressService service;

    @PostMapping(path = "/user")
    public ResponseEntity<Void> addNewUser(@Valid @RequestBody User user) {
        log.info("Request to create new user was received,. user data: {} ",user.toString());
        service.addNewUser(user);
       return ResponseEntity.ok().build();
    }

    @GetMapping(path = "/user/{login}")
    public ResponseEntity<User> getUser(@PathVariable String login) {
        log.info("Request to retrieve user data for login {} was received.",login);
       return ResponseEntity.ok(service.getUser(login));
    }

    @PostMapping(path = "/user/{login}")
    public ResponseEntity<Void> addAddressToUser(@PathVariable String login, @Valid @RequestBody Address address) {
        log.info("Request to add new address for user with login {} was received, address: {}.", login, address.toString());
        service.addAddressToUser(login, address);
        return ResponseEntity.ok().build();
    }
}
