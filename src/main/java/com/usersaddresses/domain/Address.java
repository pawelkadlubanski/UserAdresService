package com.usersaddresses.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@Entity
@Table(name = "address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonIgnore
    private Long id;

    @JsonProperty("street")
    @NotBlank
    @Column(name = "address_street")
    private String street;

    @JsonProperty("city")
    @NotBlank
    @Column(name = "address_city")
    private String city;

    @JsonProperty("zipCode")
    @NotBlank
    @Column(name = "address_zip_code")
    private String zipCode;

    @JsonProperty("country")
    @NotBlank
    @Column(name = "address_country")
    private String country;
}
