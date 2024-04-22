package com.ra.service.impl;

import com.ra.constants.RoleName;
import com.ra.exception.DataNotFound;
import com.ra.model.dto.request.UserLogin;
import com.ra.model.dto.request.UserRegister;
import com.ra.model.dto.respone.JwtResponse;
import com.ra.model.entity.Roles;
import com.ra.model.entity.Users;
import com.ra.repository.IRoleRepository;
import com.ra.repository.IUserRepository;
import com.ra.security.jwt.JwtProvider;
import com.ra.security.user_principal.UserPrincipal;
import com.ra.service.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements IUserService {
	private final PasswordEncoder passwordEncoder;
	private final IRoleRepository roleRepository;
	private final IUserRepository userRepository;
	private final AuthenticationProvider authenticationProvider;
	private final JwtProvider jwtProvider;
	
	@Override
	public void register(UserRegister userRegister) throws DataNotFound {
		Roles roleUser = roleRepository.findByRoleName(RoleName.ROLE_USER).orElseThrow(() -> new DataNotFound("role not found"));
		Set<Roles> roles = new HashSet<>();
		roles.add(roleUser);
		Users users = Users.builder()
				  .fullName(userRegister.getFullName())
				  .email(userRegister.getEmail())
				  .password(passwordEncoder.encode(userRegister.getPassword()))
				  .status(true)
				  .roles(roles)
				  .build();
		userRepository.save(users);
	}
	
	@Override
	public JwtResponse login(UserLogin userLogin) throws DataNotFound {
		Authentication authentication;
		try {
			authentication = authenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(userLogin.getEmail(), userLogin.getPassword()));
		} catch (AuthenticationException e) {
			throw new DataNotFound("username or password incorrect");
		}
		UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
		if(!userPrincipal.getStatus()) {
			throw new DataNotFound("user inactive");
		}
		String accessToken = jwtProvider.generateToken(userPrincipal);
		return JwtResponse.builder()
				  .accessToken(accessToken)
				  .fullName(userPrincipal.getFullName())
				  .email(userLogin.getEmail())
				  .roles(userPrincipal.getAuthorities())
				  .status(userPrincipal.getStatus())
				  .build();
	}
}
