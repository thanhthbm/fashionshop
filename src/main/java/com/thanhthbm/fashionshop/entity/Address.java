package com.thanhthbm.fashionshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.thanhthbm.fashionshop.auth.entity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "addresses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
  @Id
  @GeneratedValue
  private UUID id;

  @Column(nullable = false)
  private String receiverName;

  @Column(nullable = false)
  private String phoneNumber;

  @Column(nullable = false)
  private String detail;

  @Column(nullable = false)
  private String ward;

  @Column(nullable = false)
  private String district;

  @Column(nullable = false)
  private String province;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  @JsonIgnore
  @ToString.Exclude
  private User user;


  public String getFullAddress() {
    return String.format("%s, %s, %s, %s", detail, ward, district, province);
  }
}
