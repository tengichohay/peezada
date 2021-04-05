package vn.vnpt.sso.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "usersso", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy=GenerationType.SEQUENCE, generator = "id_Sequence")
    @SequenceGenerator(name = "id_Sequence", sequenceName = "ID_SEQ")
    @Column(name = "id")
    private Long id;

    @Column(nullable = false, name = "name")
    private String name;

    @Email
    @Column(nullable = false, name = "email")
    private String email;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(nullable = false, name = "email_verified")
    private Boolean emailVerified = false;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "provider_Id")
    private String providerId;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "provider")
    private AuthProvider provider;
}
