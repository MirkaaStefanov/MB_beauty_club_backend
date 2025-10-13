package com.example.MB_beauty_club_backend.services.impl;

import com.example.MB_beauty_club_backend.enums.Provider;
import com.example.MB_beauty_club_backend.enums.Role;
import com.example.MB_beauty_club_backend.exceptions.common.AccessDeniedException;
import com.example.MB_beauty_club_backend.exceptions.user.UserCreateException;
import com.example.MB_beauty_club_backend.exceptions.user.UserNotFoundException;
import com.example.MB_beauty_club_backend.exceptions.user.UserValidationException;
import com.example.MB_beauty_club_backend.models.dto.auth.AdminUserDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.OAuth2UserInfoDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.PublicUserDTO;
import com.example.MB_beauty_club_backend.models.dto.auth.RegisterRequest;
import com.example.MB_beauty_club_backend.models.entity.ShoppingCart;
import com.example.MB_beauty_club_backend.models.entity.User;
import com.example.MB_beauty_club_backend.repositories.ShoppingCartRepository;
import com.example.MB_beauty_club_backend.repositories.UserRepository;
import com.example.MB_beauty_club_backend.services.UserService;
import com.example.MB_beauty_club_backend.services.impl.security.events.OnRegistrationCompleteEvent;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final ShoppingCartRepository shoppingCartRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${server.backend.baseUrl}")
    private String appBaseUrl;

    /**
     * Creates a new user based on the provided registration request.
     *
     * @param request The registration request containing user details.
     * @return The created user.
     * @throws UserCreateException             If there is an issue creating the user.
     * @throws DataIntegrityViolationException If there is a data integrity violation while creating the user.
     * @throws ConstraintViolationException    If there is a constraint violation while creating the user.
     */
    @Override
    public User createUser(RegisterRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new UserCreateException(true);
        }

        try {
            User user = buildUser(request);
            user.setRole(Role.USER);
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            user.setEnabled(false);

            User savedUser =  userRepository.save(user);
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(savedUser);
            shoppingCartRepository.save(shoppingCart);

            return savedUser;
        } catch (DataIntegrityViolationException exception) {
            throw new UserCreateException(true);
        } catch (ConstraintViolationException exception) {
            throw new UserValidationException(exception.getConstraintViolations());
        }
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public List<PublicUserDTO> getAllUsers() {
        return userRepository
                .findAll()
                .stream()
                .map(x -> modelMapper.map(x, PublicUserDTO.class))
                .toList();
    }

    @Override
    public AdminUserDTO updateUser(UUID id, AdminUserDTO userDTO, PublicUserDTO currentUser) {
        User userToUpdate = findById(id);

        if (!(userToUpdate.getId().equals(currentUser.getId())) && !currentUser.getRole().equals(Role.ADMIN)) {
            throw new AccessDeniedException();
        }

        if (currentUser.getRole().equals(Role.USER)) {
            userToUpdate.setName(userDTO.getName());
            userToUpdate.setSurname(userDTO.getSurname());
        } else if (currentUser.getRole().equals(Role.ADMIN)) {
            // It is not null it is "" so don't change it
            if (userDTO.getPassword() == "") {
                userDTO.setPassword(userToUpdate.getPassword());
            } else {
                userDTO.setPassword(passwordEncoder.encode(userDTO.getPassword()));
            }

            modelMapper.map(userDTO, userToUpdate);
        }

        userToUpdate.setId(id);

        User updatedUser = userRepository.save(userToUpdate);
        return modelMapper.map(updatedUser, AdminUserDTO.class);
    }


    @Override
    public void deleteUserById(UUID id, PublicUserDTO currentUser) {
        User user = findById(id);

        if (user.getId().equals(currentUser.getId())) {
            throw new AccessDeniedException();
        }

        if (user.getDeletedAt() == null) {
            user.setDeletedAt(LocalDateTime.now());
        } else {
            user.setDeletedAt(null);
        }

        userRepository.save(user);
    }

    /**
     * Processes the OAuth user obtained from the OAuth2 provider.
     * If the user does not exist in the database, a new user is created based on the OAuth user details.
     *
     * @param oAuth2User The OAuth2 user obtained from the OAuth provider.
     * @return The processed user.
     */
    @Override
    public User processOAuthUser(OAuth2UserInfoDTO oAuth2User) {
        User user = userRepository.findByEmail(oAuth2User.getEmail()).orElse(null);

        if (user == null) {
            RegisterRequest registerRequest = new RegisterRequest();

            registerRequest.setEmail(oAuth2User.getEmail());
            registerRequest.setProvider(oAuth2User.getProvider());

            if (oAuth2User.getProvider().equals(Provider.GOOGLE)) {
                registerRequest.setName(oAuth2User.getGiven_name());
                registerRequest.setSurName(oAuth2User.getFamily_name());
            }

            user = userRepository.save(buildUser(registerRequest));
            eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appBaseUrl));
            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            shoppingCartRepository.save(shoppingCart);
        }

        return user;
    }

    @Override
    public User findById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public AdminUserDTO getByIdAdmin(UUID id) {
        User user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, AdminUserDTO.class);
    }

    private User buildUser(RegisterRequest request) {
        User.UserBuilder userBuilder = User
                .builder()
                .name(request.getName())
                .email(request.getEmail())
                .provider(request.getProvider())
                .role(Role.USER);

        if (request.getPassword() != null) {
            userBuilder.password(passwordEncoder.encode(request.getPassword()));
        }

        return userBuilder.build();
    }
}

