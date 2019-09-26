package gov.ita.dataloader.ingest.configuration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public
interface DataSetConfigRepository extends JpaRepository<DataSetConfig, Long> {
  List<DataSetConfig> findByContainerName(String containerName);
}
