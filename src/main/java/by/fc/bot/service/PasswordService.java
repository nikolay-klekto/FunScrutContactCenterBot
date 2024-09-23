package by.fc.bot.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;

@Service
public class PasswordService {

    // Генерация случайной соли
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return BCrypt.gensalt(12, random);
    }

    // Хеширование пароля с солью и объединение в одну строку
    public String encodePassword(String rawPassword) {
        String salt = generateSalt();
        String hashedPassword = BCrypt.hashpw(rawPassword, salt);
        // Объединяем соль и хеш, разделенные символом $
        return salt + "$" + hashedPassword;
    }

    // Проверка пароля
    public boolean verifyPassword(String password, String storedPassword) {
        // Разделяем строку на соль и хеш пароля
        String[] parts = storedPassword.split("\\$");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid stored password format");
        }
        String salt = parts[0];
        String hashedPassword = parts[1];
        // Генерируем хеш введенного пароля с солью из БД
        String generatedHash = BCrypt.hashpw(password, salt);
        // Сравниваем с хешем из БД
        return hashedPassword.equals(generatedHash);
    }
}
