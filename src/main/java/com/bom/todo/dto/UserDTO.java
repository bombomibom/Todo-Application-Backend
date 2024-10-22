package com.bom.todo.dto;

import com.bom.todo.model.UserEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
	private String token;
	private String username;
	private String password;
	private String id;
	
	public UserDTO(final UserEntity entity) {
		this.id = entity.getId();
		this.username = entity.getPassword();
	}
	
	public static UserEntity toEntity(final UserDTO dto) {
		return UserEntity.builder()
				.username(dto.getUsername())
				.password(dto.getPassword())
				.build();
	}
}
