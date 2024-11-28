package com.bom.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bom.todo.dto.ResponseDTO;
import com.bom.todo.dto.UserDTO;
import com.bom.todo.model.UserEntity;
import com.bom.todo.security.TokenProvider;
import com.bom.todo.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService service;
	
	@Autowired
	private TokenProvider tokenProvider;
	
	private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
		try {
			if(dto == null || dto.getPassword() == null) {
				throw new RuntimeException("Invalid Password value.");
			}
			
			UserEntity user = UserEntity.builder()
					.username(dto.getUsername())
					.password(passwordEncoder.encode(dto.getPassword()))
					.build();
			UserEntity registeredUser = service.create(user);
			UserDTO responseUserDTO = UserDTO.builder()
					.id(registeredUser.getId())
					.username(registeredUser.getUsername())
					.build();
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().error(error).build();
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO dto) {
		UserEntity user = service.getByCredentials(dto.getUsername(), dto.getPassword(), passwordEncoder);
		
		if(user != null) {
			final String token = tokenProvider.create(user);
			
			final UserDTO responseUserDTO = UserDTO.builder()
					.username(user.getUsername())
					.id(user.getId())
					.token(token)
					.build();
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		} else {
			ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().error("Login failed").build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
}
