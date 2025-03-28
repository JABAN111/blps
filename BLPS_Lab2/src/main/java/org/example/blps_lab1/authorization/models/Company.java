package org.example.blps_lab1.authorization.models;

import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor//NOTE: можно добавить сюда что-нибудь еще, но пока непонятно что.
//NOTE: компания может регестрировать новых пользователей, путем импорта CSV файла с необходимой информацией
public class Company {
    @Id
    private String companyName;

    @OneToOne
    @JoinColumn(nullable = false)
    private User user;
}
