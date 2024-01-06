package org.blossom.auth;

import org.blossom.auth.dto.RegisterDto;
import org.blossom.auth.repository.PasswordResetRepository;
import org.blossom.auth.repository.UserRepository;
import org.blossom.auth.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class CommonRequestHelper extends AbstractContextBeans {
    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordResetRepository passwordResetRepository;

    @Autowired
    protected VerificationTokenRepository verificationTokenRepository;

    protected MvcResult registerUser(String username, String email, String name, String password, ResultMatcher result) throws Exception {
        RegisterDto registerDto = new RegisterDto();
        registerDto.setUsername(username);
        registerDto.setEmail(email);
        registerDto.setFullName(name);
        registerDto.setPassword(password);

        return mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(result)
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();
    }
}
