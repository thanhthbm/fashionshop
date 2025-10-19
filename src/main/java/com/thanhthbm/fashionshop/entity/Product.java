package com.thanhthbm.fashionshop.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Product {

  @Id
  @Column
  @GeneratedValue
  private UUID id;

  @Column
  private String name;

  @Column
  private String description;

  @Column(nullable = false)
  private BigDecimal price;

  @Column(nullable = false)
  private String brand;

  @Column
  private Float rating;

  @Column(nullable = false)
  private boolean isNewArrival;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<ProductVariant> productVariants;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id", nullable = false)
  @JsonIgnore
  private Category category;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "categoryType_id", nullable = false)
  @JsonIgnore
  private CategoryType categoryType;

  @OneToMany(mappedBy = "product", cascade = CascadeType.ALL)
  private List<Resources> resources;

  @Column(nullable = false, updatable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdAt;

  @Column(nullable = false)
  @Temporal(TemporalType.TIMESTAMP)
  private Date updatedAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = new Date();
    updatedAt = createdAt;
  }

  @PreUpdate
  protected void onUpdate() {
    this.updatedAt = new Date();
  }
}
