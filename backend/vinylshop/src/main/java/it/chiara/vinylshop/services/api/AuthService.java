package it.chiara.vinylshop.services.api;

import it.chiara.vinylshop.dtos.LoginDto;
import it.chiara.vinylshop.dtos.RegistrazioneDto;
import it.chiara.vinylshop.entities.User;
import it.chiara.vinylshop.exceptions.DuplicateException;

import java.util.Optional;

public interface AuthService {

    void registerUser(RegistrazioneDto registrazioneDto) throws DuplicateException;

    String authenticate(LoginDto loginDto);

    Optional<User> loadByUsername(String username);

    Optional<User> findById(Long id);

    RegistrazioneDto convertToDto(User user);
}

