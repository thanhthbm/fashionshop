package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.entity.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

  Address findByUser(User user);

  List<Address> findAddressByUser(User user);

  Optional<Address> findByUserAndId(User user, UUID addressId);

  @Modifying
  @Transactional
  @Query("UPDATE Address a SET a.isDefault = false WHERE a.user.id = :userId")
  void resetAllDefaultAddresses(UUID userId);

  int countByUser(User user);
}
