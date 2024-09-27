package com.gema.thefirst.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gema.thefirst.dto.UserDTO;
import com.gema.thefirst.entities.Role;
import com.gema.thefirst.entities.User;
import com.gema.thefirst.projections.UserDetailsProjection;
import com.gema.thefirst.repositories.UserRepository;
import com.gema.thefirst.util.CustomUserUtil;

import static com.gema.thefirst.constants.Constants.EMAIL_NOT_FOUND;

@Service
public class UserService implements UserDetailsService {

  @Autowired
  private UserRepository repository;

  @Autowired
  private CustomUserUtil customUserUtil;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    List<UserDetailsProjection> result = repository.searchUserAndRolesByEmail(username);
    if (result.size() == 0) {
      throw new UsernameNotFoundException("User not found");
    }
    User user = new User();
    user.setEmail(username);
    user.setPassword(result.get(0).getPassword());
    for (UserDetailsProjection projection : result) {
      user.addRole(new Role(projection.getRoleId(), projection.getAuthority()));
    }
    return user;
  }

  protected User authenticated() {
    try {
      String username = customUserUtil.getLoggedUsername();
      return repository.findByEmail(username).get();
    } catch (Exception e) {
      throw new UsernameNotFoundException(EMAIL_NOT_FOUND);
    }
  }

  @Transactional(readOnly = true)
  public UserDTO getMe() {
    User user = authenticated();
    return new UserDTO(user);
  }
}
