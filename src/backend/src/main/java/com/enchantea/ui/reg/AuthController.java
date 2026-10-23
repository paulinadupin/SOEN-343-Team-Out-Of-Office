package com.enchantea.ui.reg;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.enchantea.application.reg.AccountResponse;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.application.reg.LoginRequest;
import com.enchantea.application.reg.RegisterRequest;
import com.enchantea.domain.reg.User;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AccountFacade accountFacade;

    public AuthController(AccountFacade accountFacade) {
        this.accountFacade = accountFacade;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AccountResponse register(@Valid @RequestBody RegisterRequest request) {
        return accountFacade.register(request);
    }

    @PostMapping("/login")
    public AccountResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest servletRequest) {
        User user = accountFacade.authenticate(request);

        // Keep the logged-in user's ID and type in the browser's session.
        HttpSession session = servletRequest.getSession();
        servletRequest.changeSessionId();
        session.setAttribute("userId", user.getUserId());
        session.setAttribute("accountType", user.getAccountType().name());
        return AccountResponse.from(user);
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(HttpServletRequest servletRequest) {
        HttpSession session = servletRequest.getSession(false);
        accountFacade.requireLoggedIn(session);
        session.invalidate();
    }
}
