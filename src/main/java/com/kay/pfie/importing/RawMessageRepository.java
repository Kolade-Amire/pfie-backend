package com.kay.pfie.importing;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RawMessageRepository extends JpaRepository<RawMessage, UUID> {}