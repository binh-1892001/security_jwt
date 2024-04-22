package com.ra.service;

import com.ra.exception.DataNotFound;
import com.ra.model.dto.request.UserLogin;
import com.ra.model.dto.request.UserRegister;
import com.ra.model.dto.respone.JwtResponse;

public interface IUserService {
	void register(UserRegister userRegister) throws DataNotFound;
	JwtResponse login(UserLogin userLogin) throws DataNotFound;

}
