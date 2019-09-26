package gov.ita.dataloader.security;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile({"development", "staging"})
public class DevelopmentAuthenticationFacade implements AuthenticationFacade {

  @Override
  public String getUserName() {
    return "TestUser@trade.gov";
  }

}

