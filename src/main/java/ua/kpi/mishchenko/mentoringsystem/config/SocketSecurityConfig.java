package ua.kpi.mishchenko.mentoringsystem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

import static org.springframework.messaging.simp.SimpMessageType.CONNECT;
import static org.springframework.messaging.simp.SimpMessageType.DISCONNECT;

@Configuration
//@EnableWebSocketSecurity
public class SocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages.simpTypeMatchers(CONNECT, DISCONNECT).permitAll()
                .anyMessage().authenticated();
    }
}
