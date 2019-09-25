package gov.ita.susastatsdataloader.ingest.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReplaceValueRepository extends JpaRepository<ReplaceValue, Long> {
}
