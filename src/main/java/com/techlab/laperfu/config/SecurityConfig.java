package com.techlab.laperfu.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.techlab.laperfu.security.JwtAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(Customizer.withDefaults())
            .sessionManagement(sesion -> sesion
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(rutas -> rutas
                // 1. Recursos estáticos del Front-End (HTML, JS, CSS, imágenes)
                .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/imagenes/**", "/favicon.ico").permitAll()
                // 2. Autenticación pública (login y registro)
                .requestMatchers("/auth/**").permitAll()
                // 3. Catálogo público (lectura GET de productos y categorías)
                .requestMatchers(HttpMethod.GET, "/producto/**", "/categoria/**").permitAll()
                // 4. Gestión del catálogo (POST, PUT, DELETE) solo para ADMIN
                .requestMatchers("/producto/**", "/categoria/**").hasRole("ADMIN")
                // 5. Pedidos y cualquier otra ruta exigen usuario autenticado (USER o ADMIN)
                .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
