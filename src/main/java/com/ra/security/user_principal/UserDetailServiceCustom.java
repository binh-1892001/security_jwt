package com.ra.security.user_principal;

import com.ra.model.entity.Users;
import com.ra.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceCustom implements UserDetailsService {
	private final IUserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Users users = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException("username not found"));
		return UserPrincipal.builder()
				  .id(users.getId())
				  .fullName(users.getFullName())
				  .email(users.getEmail())
				  .password(users.getPassword())
				  .status(users.getStatus())
				  .authorities(
							 users.getRoles().stream()
										.map(roles -> new SimpleGrantedAuthority(roles.getRoleName().name()))
										.toList()
				  )
				  .build();
	}
}
