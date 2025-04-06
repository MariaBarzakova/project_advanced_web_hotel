package app.user.service;

import app.employee.service.EmployeeService;
import app.exception.AlreadyExistException;
import app.exception.UniqueEmail;
import app.security.AuthenticationMetadata;
import app.user.model.User;
import app.user.model.UserRole;
import app.user.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmployeeService employeeService;


    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, EmployeeService employeeService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.employeeService = employeeService;
    }

    public User registerUser(RegisterRequest registerRequest) {
        Optional<User> optionalUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionalUser.isPresent()) {
            throw new AlreadyExistException("User with username already exists!");
        }

        Optional<User> optionalByEmail = userRepository.findByEmail(registerRequest.getEmail());
        if(optionalByEmail.isPresent()){
            throw new UniqueEmail("Email [%s] is in use. Email must be unique!".formatted(registerRequest.getEmail()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .email(registerRequest.getEmail())
                .role(UserRole.GUEST)
                .active(false)
                .createdAt(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();
        userRepository.save(user);
        if (userRepository.count() == 1) {
            user.setActive(true);
            user.setRole(UserRole.ADMIN);
            userRepository.save(user);
        }
        return user;
    }

    public User getUserById(UUID userId) {
        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User does not exist"));
    }

    public void editUserDetails(UUID userId, UserEditRequest userEditRequest) {
        User user = getUserById(userId);
        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setPhoneNumber(userEditRequest.getPhoneNumber());
        user.setAddress(userEditRequest.getAddress());
        user.setPassport(userEditRequest.getPassport());
        user.setActive(true);
        user.setUpdatedOn(LocalDateTime.now());
        userRepository.save(user);

        employeeService.createEmployeeRequest(user.getId());
        //employeeService.updateEmployeeActive(userId);
    }

    public List<User> getAllUsersReadyForRenewal() {
        return userRepository.findAllByActiveIsTrue();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public void switchRole(UUID userId) {
        User user = getUserById(userId);
        if (user.getRole() == UserRole.GUEST) {
            user.setRole(UserRole.EMPLOYEE);
            user.setActive(true);
            userRepository.save(user);

            employeeService.createEmployeeRequest(user.getId());

        } else if (user.getRole() == UserRole.EMPLOYEE) {
            user.setRole(UserRole.GUEST);
            user.setActive(false);
            userRepository.save(user);
        }

    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User with this username does not exist."));

        return new AuthenticationMetadata(user.getId(), username, user.getPassword(), user.getRole());
    }

}
