package edutech.project.security.jwt;

import edutech.project.model.User;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class MustChangePasswordFilter extends OncePerRequestFilter {
    private static final String CHANGE_PASSWORD_PATH = "/api/user/change-password";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        boolean isAuthEndpoint = path.startsWith("/api/auth/");
        boolean isChangePasswordEndpoint = path.equals(CHANGE_PASSWORD_PATH);

        if (!isAuthEndpoint && !isChangePasswordEndpoint) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof User user) {
                if (Boolean.TRUE.equals(user.getMustChangePassword())) {
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType("application/json");
                    response.getWriter().write(
                            "{\"error\":\"Password change required. Call PATCH /api/user/change-password before accessing this resource.\"}");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }
}