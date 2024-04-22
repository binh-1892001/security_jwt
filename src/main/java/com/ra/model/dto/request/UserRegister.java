package com.ra.model.dto.request;

import com.ra.validation.UserExist;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserRegister {
	private String fullName;
	@UserExist
	private String email;
	private String password;
}
