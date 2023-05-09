package ua.kpi.mishchenko.mentoringsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.simp.broker.DefaultSubscriptionRegistry;
import org.springframework.messaging.simp.broker.SubscriptionRegistry;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.handler.socket.SocketErrorHandler;
import ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.interceptor.socket.AuthChannelInterceptorAdapter;
import ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.interceptor.socket.SubscribeChannelInterceptorAdapter;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final AuthChannelInterceptorAdapter authChannelInterceptorAdapter;
    private final SubscribeChannelInterceptorAdapter subscribeChannelInterceptorAdapter;
    private final SocketErrorHandler socketErrorHandler;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue");
        config.setApplicationDestinationPrefixes("/app");
        config.setUserDestinationPrefix("/user");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.setErrorHandler(socketErrorHandler)
                .addEndpoint("/ws-socket")
                .setAllowedOriginPatterns("*");
        registry.setErrorHandler(socketErrorHandler)
                .addEndpoint("/ws-socket")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Bean
    public SubscriptionRegistry subscriptionRegistry() {
        return new DefaultSubscriptionRegistry();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(authChannelInterceptorAdapter, subscribeChannelInterceptorAdapter);
    }
}
