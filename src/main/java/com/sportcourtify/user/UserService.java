package com.sportcourtify.user;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sportcourtify.common.exception.ResourceNotFoundException;
import com.sportcourtify.user.dto.UserRequest;
import com.sportcourtify.user.dto.UserResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

	private final UserRepository userRepository;

	public UserResponse create(UserRequest request) {
		User user = User.builder()
				.fullName(request.fullName())
				.email(request.email())
				.phone(request.phone())
				.build();
		return UserResponse.from(userRepository.save(user));
	}

	public List<UserResponse> list() {
		return userRepository.findAll().stream()
				.map(UserResponse::from)
				.toList();
	}

	public UserResponse getById(Long id) {
		return UserResponse.from(findUser(id));
	}

	public UserResponse update(Long id, UserRequest request) {
		User user = findUser(id);
		user.setFullName(request.fullName());
		user.setEmail(request.email());
		user.setPhone(request.phone());
		return UserResponse.from(userRepository.save(user));
	}

	public void delete(Long id) {
		userRepository.delete(findUser(id));
	}

	private User findUser(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new ResourceNotFoundException("User not found: " + id));
	}
}
