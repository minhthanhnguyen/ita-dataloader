package gov.ita.dataloader.extract.fta;

import gov.ita.dataloader.extract.fta.tariff.Link;
import gov.ita.dataloader.extract.fta.tariff.Tariff;
import gov.ita.dataloader.extract.fta.tariff.TariffDocsMetadata;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RulesOfOriginProcessor {
  public void process(List<Tariff> tariffs, List<TariffDocsMetadata> metadata) {

    Map<String, List<TariffDocsMetadata>> metadataMappings = new HashMap<>();

    metadata.stream()
      .filter(m -> m.getHS2Code() != null)
      .forEach(m -> m.getCountries().forEach(c -> {
        String key = c + "#" + m.getHS2Code();
        List<TariffDocsMetadata> tmpMeta = metadataMappings.get(key);
        if (tmpMeta == null) {
          tmpMeta = new ArrayList<>();
        }
        tmpMeta.add(m);
        metadataMappings.put(key, tmpMeta);
      }));

    tariffs.stream().map(t -> {
      String metadataKey = t.getCountry() + "#" + t.getHs6().substring(0, 2);
      List<TariffDocsMetadata> tariffDocsMetadata = metadataMappings.getOrDefault(metadataKey, Collections.emptyList());
      List<Link> links = new ArrayList<>();
      tariffDocsMetadata.forEach(m -> {
        Link link = new Link(m.getMetadataStoragePath(), null);
        links.add(link);
      });

      t.setLinks(links);
      return t;
    }).collect(Collectors.toList());
  }
}
