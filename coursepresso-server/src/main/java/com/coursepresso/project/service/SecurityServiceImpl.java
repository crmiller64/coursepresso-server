package com.coursepresso.project.service;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class SecurityServiceImpl implements SecurityService {

  @Inject
  private AuthenticationManager authenticationManager;
  @Inject
  private JdbcUserDetailsManager jdbcUserDetailsManager;
  @Inject
  private HttpServletRequest request;

  private static final Logger log = LoggerFactory.getLogger(
      SecurityServiceImpl.class
  );

  @Override
  public void login(String username, String password) {
    // Never log password information!
    log.info("Logging in user '{}'", username);

    UsernamePasswordAuthenticationToken authToken
        = new UsernamePasswordAuthenticationToken(username, password);

    Authentication authentication = authenticationManager.authenticate(
        authToken
    );

    SecurityContextHolder.getContext().setAuthentication(authentication);

    log.debug("User '{}' successfully logged in with authorities: '{}'",
        username,
        authentication.getAuthorities()
    );
  }

  @Override
  public void logout() {
    Authentication authentication = SecurityContextHolder.getContext()
        .getAuthentication();

    log.info("Logging out user '{}'", authentication.getName());

    SecurityContextHolder.clearContext();

    // Clear HTTP session
    request.getSession().invalidate();

    log.debug("User '{}' successfully logged out", authentication.getName());
  }

  @Override
  public void createUser(UserDetails user) {
    log.info("Creating new user '{}'", user.getUsername());

    jdbcUserDetailsManager.createUser(user);

    log.debug("User '{}' successfully created", user.getUsername());
  }

  @Override
  public String getAuthority() {

    return SecurityContextHolder.getContext().
        getAuthentication().getAuthorities().toString();

  }

}
