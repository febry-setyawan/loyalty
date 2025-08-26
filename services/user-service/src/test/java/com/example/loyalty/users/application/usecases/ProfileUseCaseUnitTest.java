package com.example.loyalty.users.application.usecases;

import com.example.loyalty.users.application.dto.UpdateProfileRequest;
import com.example.loyalty.users.domain.entities.User;
import com.example.loyalty.users.domain.repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.loyalty.common.validation.ValidationUtils;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for profile management use cases
 */
@ExtendWith(MockitoExtension.class)
class ProfileUseCaseUnitTest {

  @Mock
  private UserRepository userRepository;

  @Mock 
  private ValidationUtils validationUtils;

  @InjectMocks
  private GetUserProfileUseCase getUserProfileUseCase;

  @InjectMocks
  private UpdateUserProfileUseCase updateUserProfileUseCase;

  @Test
  @DisplayName("Should get user profile successfully")
  void should_get_user_profile_successfully() {
    // Given
    UUID userId = UUID.randomUUID();
    User user = new User("test@example.com", "John", "Doe");
    given(userRepository.findById(userId)).willReturn(Optional.of(user));

    // When
    User result = getUserProfileUseCase.execute(userId);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getEmail()).isEqualTo("test@example.com");
    assertThat(result.getFirstName()).isEqualTo("John");
    assertThat(result.getLastName()).isEqualTo("Doe");
  }

  @Test
  @DisplayName("Should update user profile successfully")
  void should_update_user_profile_successfully() {
    // Given
    UUID userId = UUID.randomUUID();
    User user = new User("test@example.com", "John", "Doe");
    UpdateProfileRequest request = new UpdateProfileRequest(
      "Jane", "Smith", "+1234567890", LocalDate.of(1990, 1, 1)
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = updateUserProfileUseCase.execute(userId, request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFirstName()).isEqualTo("Jane");
    assertThat(result.getLastName()).isEqualTo("Smith");
    assertThat(result.getPhoneNumber()).isEqualTo("+1234567890");
    assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
    
    verify(validationUtils).validate(request);
    verify(userRepository).save(user);
  }

  @Test
  @DisplayName("Should handle empty phone number in profile update")
  void should_handle_empty_phone_number_in_profile_update() {
    // Given
    UUID userId = UUID.randomUUID();
    User user = new User("test@example.com", "John", "Doe");
    UpdateProfileRequest request = new UpdateProfileRequest(
      "Jane", "Smith", null, LocalDate.of(1990, 1, 1)
    );

    given(userRepository.findById(userId)).willReturn(Optional.of(user));
    given(userRepository.save(any(User.class))).willAnswer(invocation -> invocation.getArgument(0));

    // When
    User result = updateUserProfileUseCase.execute(userId, request);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getFirstName()).isEqualTo("Jane");
    assertThat(result.getLastName()).isEqualTo("Smith");
    assertThat(result.getPhoneNumber()).isNull();
    assertThat(result.getDateOfBirth()).isEqualTo(LocalDate.of(1990, 1, 1));
    
    verify(validationUtils).validate(request);
    verify(userRepository).save(user);
  }
}