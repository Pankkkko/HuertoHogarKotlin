package React.HuertoHogar.controller;

import React.HuertoHogar.model.Usuario;
import React.HuertoHogar.service.UsuarioService;
import React.HuertoHogar.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UsuarioService usuarioService;
    private final JwtUtils jwtUtils;

    public AuthController(UsuarioService usuarioService, JwtUtils jwtUtils) {
        this.usuarioService = usuarioService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String password = payload.get("password");
        if (email == null || password == null) return ResponseEntity.badRequest().body(Map.of("error","email and password required"));

        Usuario u = usuarioService.findByEmailRaw(email);
        if (u == null) return ResponseEntity.status(401).body(Map.of("error","invalid credentials"));
        boolean ok = usuarioService.checkPassword(u, password);
        if (!ok) return ResponseEntity.status(401).body(Map.of("error","invalid credentials"));

        String token = jwtUtils.generateToken(u.getEmail());
        return ResponseEntity.ok(Map.of("accessToken", token, "email", u.getEmail(), "rol", u.getRol()));
    }
}
