package CampusVoice;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AdminAccountBootstrap implements InitializingBean {
    private final AdminAccountRepository adminAccountRepository;
    private final PasswordHasher passwordHasher;

    @Value("${campusvoice.admin.username}")
    private String initialHodUsername;

    @Value("${campusvoice.admin.password}")
    private String initialHodPassword;

    public AdminAccountBootstrap(AdminAccountRepository adminAccountRepository,
                                 PasswordHasher passwordHasher) {
        this.adminAccountRepository = adminAccountRepository;
        this.passwordHasher = passwordHasher;
    }

    @Override
    public void afterPropertiesSet() {
        if (adminAccountRepository.findAll().stream().anyMatch(AdminAccount::isHod)) return;

        String username = initialHodUsername == null ? "" : initialHodUsername.trim();
        if (username.isEmpty() || initialHodPassword == null || initialHodPassword.isBlank()) {
            throw new IllegalStateException(
                    "Set CAMPUSVOICE_ADMIN_USERNAME and CAMPUSVOICE_ADMIN_PASSWORD to initialize the HOD account.");
        }

        if (adminAccountRepository.existsByUsernameIgnoreCase(username)) {
            throw new IllegalStateException(
                    "The configured HOD username already exists as a non-HOD account. Choose a different username.");
        }

        AdminAccount hod = new AdminAccount();
        hod.setUsername(username);
        hod.setDisplayName("Rais Mulla");
        hod.setPasswordHash(passwordHasher.hash(initialHodPassword));
        hod.setRole(AdminAccount.ROLE_HOD);
        hod.setCategory(null);
        hod.setEnabled(true);
        adminAccountRepository.save(hod);
    }
}
