package it.chiara.vinylshop.services.impl;

import it.chiara.vinylshop.dtos.LoginDto;
import it.chiara.vinylshop.dtos.RegistrazioneDto;
import it.chiara.vinylshop.entities.RoleName;
import it.chiara.vinylshop.entities.User;
import it.chiara.vinylshop.entities.UserRole;
import it.chiara.vinylshop.exceptions.DuplicateException;
import it.chiara.vinylshop.exceptions.NotFoundException;
import it.chiara.vinylshop.repository.UserRepository;
import it.chiara.vinylshop.repository.UserRoleRepository;
import it.chiara.vinylshop.security.JwtTokenProvider;
import it.chiara.vinylshop.services.api.AuthService;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserRoleRepository userRoleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthServiceImpl(UserRepository userRepository,
                           UserRoleRepository userRoleRepository,
                           PasswordEncoder passwordEncoder,
                           JwtTokenProvider jwtTokenProvider) {
        this.userRepository = userRepository;
        this.userRoleRepository = userRoleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // -------------------- REGISTER --------------------
    @Override
    @Transactional
    public void registerUser(RegistrazioneDto dto) throws DuplicateException {

        if (dto == null) {
            throw new RuntimeException("Registrazione non valida");
        }

        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new RuntimeException("Username obbligatorio");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Password obbligatoria");
        }

        String username = dto.getUsername().trim();

        if (userRepository.existsByUsernameIgnoreCase(username)) {
            throw new DuplicateException("Username già in uso");
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            if (userRepository.existsByEmailIgnoreCase(dto.getEmail().trim())) {
                throw new DuplicateException("Email già in uso");
            }
        }

        User user = new User();
        user.setUsername(username);

        // password salvata in bcrypt
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        user.setEmail(dto.getEmail());


        User saved = userRepository.save(user);

        // ruolo base
        UserRole role = new UserRole(saved, RoleName.ROLE_USER);
        userRoleRepository.save(role);
    }

    // -------------------- LOGIN --------------------
    @Override
    public String authenticate(LoginDto loginDto) throws NotFoundException {

        if (loginDto == null) {
            throw new RuntimeException("Login non valido");
        }

        String username = (loginDto.getUsername() == null) ? null : loginDto.getUsername().trim();
        String password = (loginDto.getPassword() == null) ? null : loginDto.getPassword().trim();

        if (username == null || username.isBlank()) {
            throw new RuntimeException("Username obbligatorio");
        }

        if (password == null || password.isBlank()) {
            throw new RuntimeException("Password obbligatoria");
        }

        User user = loadByUsername(username)
                .orElseThrow(() -> new NotFoundException("Utente non trovato"));

        // verifica bcrypt
        if (user.getPassword() == null || !passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Credenziali non valide");
        }

        List<String> roles = user.getRoles().stream()
                .map(r -> r.getRoleName().name())
                .toList();

        return jwtTokenProvider.generateToken(user.getId(), user.getUsername(), roles);
    }

    // -------------------- LOOKUP --------------------
    @Override
    public Optional<User> loadByUsername(String username) {
        if (username == null) return Optional.empty();
        return userRepository.findByUsernameIgnoreCase(username.trim());
    }

    @Override
    public Optional<User> findById(Long id) {
        if (id == null) return Optional.empty();
        return userRepository.findById(id);
    }

    @Override
    public RegistrazioneDto convertToDto(User user) {

        if (user == null) return null;

        RegistrazioneDto dto = new RegistrazioneDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());


        return dto;
    }
}