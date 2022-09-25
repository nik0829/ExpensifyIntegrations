package com.example.expensify.controller;

import com.example.expensify.Repository.ExpensifyRepository;
import com.example.expensify.Utils.JwtUtil;
import com.example.expensify.model.AuthRequest;
import com.example.expensify.model.ExportTemplate;
import com.example.expensify.service.ExpensifyIntegrationsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
public class ExpensifyController {

    @Autowired
    private ExpensifyIntegrationsService getCall;

    @Autowired
    private ExpensifyRepository repository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

//    @ApiIgnore
//    @RequestMapping(value = "/")
//    public void redirect(HttpServletResponse response) throws IOException {
//        response.sendRedirect("/swagger-ui.html");
//    }

    @RequestMapping("/getData")
    public String getDataFromExpensify() throws Exception {
        List<ExportTemplate> exportTemplateList = getCall.getCSV();
        repository.saveAll(exportTemplateList);
        return "Success";
    }

    @RequestMapping("/authorize")
    public String generateAuthToken(@RequestBody AuthRequest authRequest) throws Exception {
        log.info("Validating User");
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUserName(), authRequest.getPassword())
            );
        } catch (Exception e) {
            throw new Exception("invalid username or password");
        }
        log.info("User validated");
        log.info("generating Auth Token");
        return  jwtUtil.generateToken(authRequest.getUserName());
    }

}
