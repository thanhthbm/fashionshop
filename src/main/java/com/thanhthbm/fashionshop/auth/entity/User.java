package com.thanhthbm.fashionshop.auth.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanhthbm.fashionshop.entity.Address;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "AUTH_USER_DETAILS")
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails {
  @Id
  @GeneratedValue
  private UUID id;

  private String firstName;
  private String lastName;

  @JsonIgnore
  private String password;

  private Date createdOn;
  private Date updatedOn;

  @Column(nullable = false, unique = true)
  private String email;

  private String phoneNumber;

  private String provider;

  private String verificationCode;

  private String refreshToken;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
  @ToString.Exclude
  private List<Address> addressList;

  private boolean enabled = false;

  @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinTable(
      name = "AUTH_USER_AUTHORITY",
      joinColumns = @JoinColumn(referencedColumnName = "id"),
      inverseJoinColumns = @JoinColumn(referencedColumnName = "id")
  )
  private List<Authority> authorities;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }
}
