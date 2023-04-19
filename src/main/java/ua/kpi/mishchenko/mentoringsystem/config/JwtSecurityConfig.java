package ua.kpi.mishchenko.mentoringsystem.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ua.kpi.mishchenko.mentoringsystem.repository.UserRepository;
import ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.filter.CORSFilter;
import ua.kpi.mishchenko.mentoringsystem.web.rest.api.v1.filter.JwtUserRequestFilter;

import java.util.Collections;

import static org.springframework.http.HttpMethod.POST;

@Configuration
@RequiredArgsConstructor
public class JwtSecurityConfig {

    private static final String API_PATH = "/api/v1";
    private static final String MENTOR_ROLE = "MENTOR";
    private static final String MENTEE_ROLE = "MENTEE";

    private final JwtUserRequestFilter jwtRequestFilter;
    //    private final JwtStaffRequestFilter jwtStaffRequestFilter;
    private final UserRepository userRepository;
    //    private final StaffRepository staffRepository;
    private final CORSFilter corsFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtUserProvider jwtUserProvider() {
        return new JwtUserProvider(userRepository, passwordEncoder());
    }

//    @Bean
//    public JwtStaffProvider jwtStaffProvider() {
//        return new JwtStaffProvider(staffRepository, passwordEncoder());
//    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Collections.singletonList(jwtUserProvider()));
//        return new ProviderManager(Arrays.asList(jwtUserProvider(), jwtStaffProvider()));
    }

    @Bean
    public SecurityFilterChain configure(final HttpSecurity http) throws Exception {
        return http.cors().and()
                .csrf().disable()
                .authorizeHttpRequests()
                .requestMatchers(POST, API_PATH + "/auth/**").permitAll()
//                .requestMatchers(GET, API_PATH + "/recipes/{id}", API_PATH + "/recipes", API_PATH + "/categories", API_PATH + "/regions").permitAll()
//                .requestMatchers(POST, API_PATH + "/recipes").hasRole(USER)
                .anyRequest().hasAnyAuthority(MENTEE_ROLE, MENTOR_ROLE).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .addFilterBefore(corsFilter, ChannelProcessingFilter.class)
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}