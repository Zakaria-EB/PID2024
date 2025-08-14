package com.reservations.reservations.mapper;


import com.reservations.reservations.dto.LoginRequestDTO;
import com.reservations.reservations.dto.LoginResponseDTO;
import com.reservations.reservations.model.User;

public class UserMapper {

    // Mapper de User à LoginResponseDTO
    public static LoginResponseDTO toLoginResponseDTO(User user) {
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setId(user.getId());
        dto.setFirstname(user.getFirstname());
        dto.setLastname(user.getLastname());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole().toString()); // ou quelque chose de plus approprié
        return dto;
    }

    // Mapper de LoginRequestDTO à User (pour l'authentification)
    public static User toUser(LoginRequestDTO loginRequestDTO) {
        User user = new User();
        user.setLogin(loginRequestDTO.getLogin());
        user.setPassword(loginRequestDTO.getPassword()); // Note: n'oublie pas de hasher le mot de passe avant de l'enregistrer
        return user;
    }
}
