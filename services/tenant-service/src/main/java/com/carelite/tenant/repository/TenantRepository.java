package com.carelite.tenant.repository;

import com.carelite.tenant.domain.Tenant;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TenantRepository extends JpaRepository<Tenant, String> {
  boolean existsBySlug(String slug);

  Optional<Tenant> findByIdempotencyKey(String idempotencyKey);
}
