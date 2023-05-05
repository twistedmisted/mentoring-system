package ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.interceptor.socket;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtTokenService;
import ua.kpi.mishchenko.mentoringsystem.service.security.JwtUserDetailsService;

@Component
@RequiredArgsConstructor
@Slf4j
public class AuthChannelInterceptorAdapter implements ChannelInterceptor {

    private final JwtTokenService jwtTokenService;
    private final JwtUserDetailsService jwtUserDetailsService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        final StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        StompCommand command = accessor.getCommand();
        if (StompCommand.CONNECT == command) {
            final String header = accessor.getFirstNativeHeader("Authorization");
            if (header == null || !header.startsWith("Bearer ")) {
                return message;
            }
            final String token = header.substring(7);
            final String username = jwtTokenService.validateTokenAndGetUsername(token);
            if (username == null) {
                return message;
            }
            UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(username);
            final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities());
            accessor.setUser(authentication);
        }

        return message;
    }
}
