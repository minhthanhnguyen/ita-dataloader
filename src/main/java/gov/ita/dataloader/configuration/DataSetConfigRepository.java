package gov.ita.dataloader.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public
interface DataSetConfigRepository extends JpaRepository<DataSetConfig, Long> {
}
