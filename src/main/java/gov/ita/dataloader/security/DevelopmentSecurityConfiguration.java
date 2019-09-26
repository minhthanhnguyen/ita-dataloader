package gov.ita.dataloader.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
@Profile({"development", "staging"})
public class DevelopmentSecurityConfiguration extends WebSecurityConfigurerAdapter {

  @Autowired
  protected void configure(AuthenticationManagerBuilder auth) throws Exception {
    //...
  }

  @Override
  public void configure(WebSecurity web) throws Exception {
    web.ignoring().antMatchers("/**");
  }

}
