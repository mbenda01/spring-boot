package iibs.gestionportefeuille.aspect;

import org.springframework.security.core.context.SecurityContextHolder;
import iibs.gestionportefeuille.entity.AuditLog;
import iibs.gestionportefeuille.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private static final Logger log = LoggerFactory.getLogger("AUDIT");

    private final AuditLogRepository auditLogRepository;

    @AfterReturning("execution(* iibs.gestionportefeuille.service.impl.*.*(..))")
    public void journaliser(JoinPoint joinPoint) {
        String email = recupererEmailConnecte();
        String action = joinPoint.getSignature().toShortString();

        log.info("[{}] {} -> {}", email, action, joinPoint.getArgs());

        AuditLog entree = AuditLog.builder()
                .utilisateurEmail(email)
                .action(action)
                .details(String.valueOf((Object[]) joinPoint.getArgs()))
                .horodatage(LocalDateTime.now())
                .build();

        auditLogRepository.save(entree);
    }

    private String recupererEmailConnecte() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        return (auth != null && auth.isAuthenticated()) ? auth.getName() : "anonyme";
    }
}
