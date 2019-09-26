package gov.ita.dataloader.ingest.configuration.business;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class BusinessUnit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String businessName;
  String containerName;
}
