package com.ra.controller;

import com.ra.exception.DataNotFound;
import com.ra.model.dto.request.UserLogin;
import com.ra.model.dto.request.UserRegister;
import com.ra.service.IUserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
	private final IUserService userService;
	
	@PostMapping("/register")
	public ResponseEntity<?> handleRegister(@RequestBody @Valid UserRegister userRegister) throws DataNotFound {
		userService.register(userRegister);
		return new ResponseEntity<>("Register successfully", HttpStatus.CREATED);
	}
	
	@PostMapping("/login")
	public ResponseEntity<?> handleLogin(@RequestBody @Valid UserLogin userLogin) throws DataNotFound {
		return new ResponseEntity<>(userService.login(userLogin), HttpStatus.OK);
	}
	
}
