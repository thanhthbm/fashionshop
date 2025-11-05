package com.thanhthbm.fashionshop.auth.repository;

import com.thanhthbm.fashionshop.auth.entity.Authority;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, UUID> {

  Authority findByRoleCode(String roleCode);

}
