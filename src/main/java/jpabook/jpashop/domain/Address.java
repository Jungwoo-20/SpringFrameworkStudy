package jpabook.jpashop.domain;

import lombok.*;

import javax.persistence.Embeddable;
import javax.persistence.Entity;

@Getter
@Setter
@Embeddable
@AllArgsConstructor
@NoArgsConstructor
public class Address {
    private String city;
    private String street;
    private String zipcode;
}
