package by.fc.bot.service;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    public String encodePassword(String rawPassword) {
        return BCrypt.hashpw(rawPassword, BCrypt.gensalt(12));
    }

    public boolean verifyPassword(String password, String storedPassword) {
        return BCrypt.checkpw(password, storedPassword);
    }
}