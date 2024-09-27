package com.gema.thefirst.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gema.thefirst.entities.User;
import com.gema.thefirst.services.exceptions.ForbiddenException;

import static com.gema.thefirst.constants.Constants.ACCESS_DENIED_SENTENCE;

@Service
public class AuthService {

  @Autowired
  private UserService userService;

  public void validateSelfOrAdmin(Long userId) {
    User me = userService.authenticated();
    if (me.hasRole("ROLE_ADMIN")) {
      return;
    }
    if (!me.getId().equals(userId)) {
      throw new ForbiddenException(ACCESS_DENIED_SENTENCE);
    }
  }
}
