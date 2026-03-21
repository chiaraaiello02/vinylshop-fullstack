package it.chiara.vinylshop.controllers;

import it.chiara.vinylshop.dtos.InfoMsg;
import it.chiara.vinylshop.dtos.LoginDto;
import it.chiara.vinylshop.dtos.RegistrazioneDto;
import it.chiara.vinylshop.entities.User;
import it.chiara.vinylshop.exceptions.NotFoundException;
import it.chiara.vinylshop.services.api.AuthService;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;
import java.util.Optional;

@Log
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    @Autowired
    private AuthService authService;

    // POST /auth/register
    @PostMapping(value = "/register", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> registerUser(@RequestBody RegistrazioneDto registrazioneDto) {
        authService.registerUser(registrazioneDto);
        return new ResponseEntity<>(
                new InfoMsg(LocalDate.now(), "Salvataggio effettuato con successo"),
                HttpStatus.CREATED
        );
    }

    // POST /auth/login
    @PostMapping(value = "/login", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        // logica JWT per generare un token (deve essere implementata in AuthService)
        String token = authService.authenticate(loginDto);
        return ResponseEntity.ok(Map.of("token", token));
    }

    // GET /auth/cerca/id/{id}
    @SneakyThrows
    @GetMapping(value = "/cerca/id/{id}", produces = "application/json")
    public ResponseEntity<?> findByID(@PathVariable("id") Long id) {

        log.info("****** Otteniamo l'utente con id " + id + " *******");

        Optional<User> user = authService.findById(id);

        if (user == null || user.isEmpty()) {
            String errMsg = String.format("L'user con id %s non e' stato trovato!", id);
            log.warning(errMsg);
            throw new NotFoundException(errMsg);
        }

        User usResp = user.get();
        RegistrazioneDto dto = authService.convertToDto(usResp);

        return new ResponseEntity<>(dto, HttpStatus.OK);
    }
}