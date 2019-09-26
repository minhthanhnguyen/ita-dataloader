package gov.ita.susastatsdataloader.configuration;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Data
@Entity
public class ReplaceValue {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;
  String replaceThis;
  String withThis;
}
