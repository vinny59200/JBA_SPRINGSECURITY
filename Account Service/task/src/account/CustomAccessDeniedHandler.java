package account;

import account.management.EventLogger;
import account.management.entities.EventAction;
import account.management.exceptions.CustomErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import javax.json.Json;
import javax.json.JsonObject;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    @Autowired
    EventLogger logger;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException, ServletException {
//        response.getWriter().write( "Access Denied!");
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        log.error("pb Access Denied:"+e.getMessage()+" "+request.getRequestURI());
        CustomErrorResponse error = new CustomErrorResponse();
        error.setPath(request.getRequestURI());
        error.setError( "Forbidden");
        error.setStatus(403);
        error.setMessage("Access Denied!");
        error.setTimestamp(LocalDateTime.now());
        Authentication auth
                = SecurityContextHolder.getContext().getAuthentication();
        String subject = (auth == null ? "Anonymous" : auth.getName());
        logger.log(0L, EventAction.ACCESS_DENIED, subject, request.getServletPath());


        PrintWriter out = response.getWriter();
        ObjectMapper objectMapper= new ObjectMapper();
        String jsonString = objectMapper.writeValueAsString(error);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        out.print(jsonString);
        out.flush();

    }
}
