package com.usersaddresses;

import com.usersaddresses.domain.Address;
import com.usersaddresses.domain.User;
import org.apache.tomcat.util.codec.binary.Base64;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserAddressIntegrationTest {
    private static final String BASIC_AUTH_PASWORD = "admin";
    private static final String BASIC_AUTH_USER = "admin";
    private static final String NEW_USER_LOGIN = "NEW_USER_LOGIN";
    private static final String NEW_USER_NAME = "NEW_USER_NAME";
    private static final String NEW_USER_SURNAME = "NEW_USER_SURNAME";
    private static final String CITY_NAME = "city";
    private static final String COUNTRY_NAME = "country";
    private static final String ZIP_CODE = "12-345";
    private static final String STREET_NAME = "street";
    private static final String CHARSET_NAME = "US-ASCII";
    private static final String AUTHORIZATION = "Authorization";
    private static final String BASIC = "Basic ";
    private static final int USER_POSTFIX_MAX_VALUE = 100;
    private static Random RANDOM = new Random(USER_POSTFIX_MAX_VALUE);
    private static Address DEFAULT_CORRECT_ADDRESS = Address.builder().city(CITY_NAME).country(COUNTRY_NAME).zipCode(ZIP_CODE).street(STREET_NAME).build();

    @Autowired
    private TestRestTemplate restTemplate;

    private String createAuthHeader() {
        String credentials = BASIC_AUTH_USER + ":" + BASIC_AUTH_PASWORD;
        byte[] encodedCredentials = Base64.encodeBase64(credentials.getBytes(Charset.forName(CHARSET_NAME)) );
        return BASIC + new String(encodedCredentials);
    }

    private HttpEntity<User> createRequestEntity(User user) {
        return new HttpEntity<>(user, createHttpHeaders());
    }

    private HttpEntity createRequestEntity() {
        return new HttpEntity<>(null, createHttpHeaders());
    }

    private HttpEntity<Address> createRequestEntity(Address address) {
        return new HttpEntity<>(address, createHttpHeaders());
    }

    private HttpHeaders createHttpHeaders() {
        var httpHeaders = new HttpHeaders();
        httpHeaders.set(AUTHORIZATION, createAuthHeader() );
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        return httpHeaders;
    }

    private void assertStatusAfterAddingNewUSer(User user, HttpStatus status) {
        var responseEntity = restTemplate.postForEntity("/useraddress/user",createRequestEntity(user), Void.class);
        Assertions.assertThat(responseEntity .getStatusCode()).isEqualByComparingTo(status);
    }

    private User retrievedUser(String userLogin) {
        var responseEntity = restTemplate.exchange("/useraddress/user/" + userLogin, HttpMethod.GET, createRequestEntity(), User.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        return responseEntity.getBody();
    }

    private String createNewUserLogin() {
        return String.format("%s_%d", NEW_USER_LOGIN, RANDOM.nextInt());
    }

    @Test
    public void shouldReturnOkWhenUserDataAreCorrect() {
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.OK);
    }

    @Test
    public void shouldBeAbleToRetrieveNewAddedUse() {
        var userLogin = createNewUserLogin();
        var user = User.builder().login(userLogin).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.OK);
        Assertions.assertThat(retrievedUser(userLogin)).isEqualTo(user);
    }

    @Test
    public void shouldBeAbleToAddedManyUse() {
        var firstUserLogin = createNewUserLogin();
        var secondUserLogin = createNewUserLogin();

        var firstUser = User.builder().login(firstUserLogin).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        var secondUser = User.builder().login(secondUserLogin).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();

        assertStatusAfterAddingNewUSer(firstUser, HttpStatus.OK);
        assertStatusAfterAddingNewUSer(secondUser, HttpStatus.OK);

        Assertions.assertThat(retrievedUser(firstUserLogin)).isEqualTo(firstUser);
        Assertions.assertThat(retrievedUser(secondUserLogin)).isEqualTo(secondUser);
    }

    @Test
    public void shouldNotBeAbleToAddTwiceUserWIthTheSameLogin() {
        var userLogin = createNewUserLogin();
        var user = User.builder().login(userLogin).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.OK);
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldAddNewAddressToExistingUser() {
        var userLogin = createNewUserLogin();
        var user = User.builder().login(userLogin).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.OK);

        var newAddress = Address.builder().city(CITY_NAME).country(COUNTRY_NAME).street(STREET_NAME).zipCode(ZIP_CODE).build();
        var responseEntity = restTemplate.postForEntity("/useraddress/user/" + userLogin, createRequestEntity(newAddress), Void.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        Assertions.assertThat(retrievedUser(userLogin).getAddressList().stream().anyMatch(address -> address.equals(newAddress))).isTrue();
    }

    @Test
    public void shouldReturnErrorWhenAddNewAddersToNonExistingUser() {
        var userLogin = createNewUserLogin();
        var newAddress = Address.builder().city(CITY_NAME).country(COUNTRY_NAME).street(STREET_NAME).zipCode(ZIP_CODE).build();
        var responseEntity = restTemplate.postForEntity("/useraddress/user/" + userLogin, createRequestEntity(newAddress), Void.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnErrorWhenTryGetNonExistingUser() {
        var userLogin = createNewUserLogin();
        var responseEntity = restTemplate.exchange("/useraddress/user/" + userLogin, HttpMethod.GET, createRequestEntity(), User.class);
        Assertions.assertThat(responseEntity.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserDataHaveEmptyAddressList() {
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(new ArrayList<>()).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserAddressLackingStreet() {
        var addressWithOutStreet = Address.builder().city(CITY_NAME).country(COUNTRY_NAME).zipCode(ZIP_CODE).build();
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(addressWithOutStreet)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserAddressLackingCountry() {
        var addressWithOutCountry = Address.builder().city(CITY_NAME).zipCode(ZIP_CODE).street(STREET_NAME).build();
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(addressWithOutCountry)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserAddressLackingCity() {
        var addressWithOutCity = Address.builder().country(COUNTRY_NAME).zipCode(ZIP_CODE).street(STREET_NAME).build();
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(addressWithOutCity)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserAddressLackingPostCode() {
        var addressWithOutPostCode = Address.builder().city(CITY_NAME).country(COUNTRY_NAME).street(STREET_NAME).build();
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).surname(NEW_USER_SURNAME).addressList(List.of(addressWithOutPostCode)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }
    @Test
    public void shouldReturnBadRequestWhenUserDataLackingUserName() {
        var user = User.builder().login(NEW_USER_LOGIN).surname(NEW_USER_SURNAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserDataLackingUserSurname() {
        var user = User.builder().login(NEW_USER_LOGIN).name(NEW_USER_NAME).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserDataLackingUserNameNadUserSurname() {
        var user = User.builder().login(NEW_USER_LOGIN).addressList(List.of(DEFAULT_CORRECT_ADDRESS)).build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }

    @Test
    public void shouldReturnBadRequestWhenUserAllDataAreNull() {
        var user = User.builder().build();
        assertStatusAfterAddingNewUSer(user, HttpStatus.BAD_REQUEST);
    }
}
