package com.thanhthbm.fashionshop.repository;

import com.thanhthbm.fashionshop.auth.entity.User;
import com.thanhthbm.fashionshop.entity.Address;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

  Address findByUser(User user);

  List<Address> findAddressByUser(User user);

  Optional<Address> findByUserAndId(User user, UUID addressId);


}
