package lu.luxtrust.flowers.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lu.luxtrust.flowers.properties.OrelyAuthProperties;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.security.jwt.JWTService;
import lu.luxtrust.flowers.security.orely.OrelyAuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/api/login")
public class LoginController {

    private final OrelyAuthenticationService orelyAuthenticationService;
    private final OrelyAuthProperties orelyAuthProperties;
    private final JWTService jwtService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    @Autowired
    public LoginController(OrelyAuthenticationService orelyAuthenticationService,
                           OrelyAuthProperties orelyAuthProperties,
                           ObjectMapper objectMapper,
                           UserRepository userRepository,
                           JWTService jwtService) {
        this.orelyAuthenticationService = orelyAuthenticationService;
        this.orelyAuthProperties = orelyAuthProperties;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ModelAndView login(@RequestParam(value = "lang", required = false) String languageCode) throws Exception {
        ModelAndView mv = new ModelAndView();
        if (orelyAuthProperties.isMockEnabled()) {
            mv.setViewName("login-mock");
            mv.addObject("destination", orelyAuthProperties.getReturnUrl());
            mv.addObject("users", userRepository.findAll());
        } else {
            mv.setViewName("login");
            mv.addObject("samlRequest", orelyAuthenticationService.generateSAMLRequest(languageCode));
            mv.addObject("destination", orelyAuthProperties.getDestinationUrl());
        }
        return mv;
    }

    @PostMapping(value = "/reload")
    public ModelAndView reloadLoginPage() {
        return new ModelAndView("login-reload");
    }

    @PostMapping(value = "/success")
    public ModelAndView loginSuccess(HttpServletResponse response, RestAuthenticationToken authencation) throws Exception {
        ModelAndView mv = new ModelAndView("login-success");
        mv.addObject("jwt", jwtService.getToken(response));
        mv.addObject("refreshJwt", jwtService.getRefreshToken(response));
        mv.addObject("authentication", objectMapper.writeValueAsString(authencation));
        return mv;
    }
}
