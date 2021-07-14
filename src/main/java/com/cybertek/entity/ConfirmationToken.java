package com.cybertek.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "confirmation_email")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ConfirmationToken extends BaseEntity{

    private String token;

    @OneToOne(targetEntity = User.class) //==> want it to be uni-directional
    @JoinColumn(name = "user_id")
    private User user;

    private LocalDate expiredDate;

    public Boolean isTokenValid(LocalDate date){
        LocalDate now = LocalDate.now();
        return date.isEqual(now) || date.isEqual(now.plusDays(1));
    }

    public ConfirmationToken(User user) {
        this.user = user;
        expiredDate = LocalDate.now().plusDays(1);
        token = UUID.randomUUID().toString();

    }
}
