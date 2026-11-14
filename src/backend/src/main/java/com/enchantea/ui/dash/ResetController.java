package com.enchantea.ui.dash;

import com.enchantea.application.dash.ResetService;
import com.enchantea.application.reg.AccountFacade;
import com.enchantea.domain.reg.AccountType;
import com.enchantea.domain.reg.User;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/dash")
public class ResetController {
    private final ResetService RESET_SERVICE;
    private final AccountFacade ACCOUNT_FACADE;

    public ResetController(ResetService resetService, AccountFacade accountFacade) {
        this.RESET_SERVICE = resetService;
        this.ACCOUNT_FACADE = accountFacade;
    }

    @PostMapping("/reset")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reset(HttpServletRequest request) {
        User user = ACCOUNT_FACADE.requireLoggedIn(request.getSession(false));
        if (user.getAccountType() != AccountType.OPERATOR) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operator account required");
        }
        RESET_SERVICE.reset();
    }
}
