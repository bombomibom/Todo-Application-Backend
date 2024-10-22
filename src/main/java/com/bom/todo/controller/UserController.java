package com.bom.todo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.bom.todo.dto.ResponseDTO;
import com.bom.todo.dto.UserDTO;
import com.bom.todo.model.UserEntity;
import com.bom.todo.service.UserService;

@RestController
@RequestMapping("/auth")
public class UserController {

	@Autowired
	private UserService service;
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@RequestBody UserDTO dto) {
		try {
			if(dto == null || dto.getPassword() == null) {
				throw new RuntimeException("Invalid Password value.");
			}
			
			UserEntity user = UserDTO.toEntity(dto);
			UserEntity registeredUser = service.create(user);
			UserDTO responseUserDTO = new UserDTO(registeredUser);
			
			return ResponseEntity.ok().body(responseUserDTO);
			
		} catch (Exception e) {
			String error = e.getMessage();
			ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().error(error).build();
			
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
	
	@PostMapping("/signin")
	public ResponseEntity<?> authenticate(@RequestBody UserDTO dto) {
		UserEntity user = service.getByCredentials(dto.getUsername(), dto.getPassword());
		
		if(user != null) {
			UserDTO responseUserDTO = new UserDTO(user);
			return ResponseEntity.ok().body(responseUserDTO);
			
		} else {
			ResponseDTO<UserDTO> responseDTO = ResponseDTO.<UserDTO>builder().error("Login failed").build();
			return ResponseEntity.badRequest().body(responseDTO);
		}
	}
}
