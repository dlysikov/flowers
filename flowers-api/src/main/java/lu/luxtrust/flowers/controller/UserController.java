package lu.luxtrust.flowers.controller;

import lu.luxtrust.flowers.entity.system.Role;
import lu.luxtrust.flowers.entity.system.User;
import lu.luxtrust.flowers.enums.RoleType;
import lu.luxtrust.flowers.enums.Markers;
import lu.luxtrust.flowers.model.PageParams;
import lu.luxtrust.flowers.model.PageResponse;
import lu.luxtrust.flowers.repository.UserRepository;
import lu.luxtrust.flowers.security.RestAuthenticationToken;
import lu.luxtrust.flowers.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static Marker auditMarker = MarkerFactory.getMarker(Markers.AUDIT.getName());

    private UserService userService;
    private UserRepository userRepository;

    @Autowired
    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @PreAuthorize("hasAnyPermission('User', 'read')")
    @GetMapping
    public ResponseEntity<PageResponse<User>> findUsers(RestAuthenticationToken authentication, PageParams params) {
        LOG.info(auditMarker, "Retrieving users list for user with id={}", authentication.getId());
        List<Role> roles = userService.managedBy(roles(authentication));
        List<User> users = userService.findAll(params, roles);
        LOG.info(auditMarker, "Found {} users for user with id={}", users.size(), authentication.getId());
        return ResponseEntity.ok(new PageResponse<>(users, userService.count(params.getFilter(), roles)));
    }

    @PreAuthorize("hasAnyPermission('User', 'read') and isManagerOf('User', #id)")
    @GetMapping("/{id}")
    public ResponseEntity<User> getUser(@PathVariable("id") Long id, RestAuthenticationToken authentication) {
        LOG.info(auditMarker, "User with id={} is trying to obtain data for user with id={}", authentication.getId(), id);
        User user = userRepository.findOne(id);
        if (user != null) {
            LOG.info(auditMarker, "User with id={} found for user with id={}", id, authentication.getId());
            return ResponseEntity.ok(user);
        }
        LOG.info(auditMarker, "User with id={} doesn't exists. Requested by user with id={}", id, authentication.getId());
        return ResponseEntity.notFound().build();
    }

    @PreAuthorize("hasAnyPermission('User', 'create') and isCreatingNew(#user) and isManagerOf('User', #user)")
    @PutMapping
    public ResponseEntity<User> create(@Validated @RequestBody User user, BindingResult result, RestAuthenticationToken authentication) throws BindException {
        LOG.info(auditMarker, "User with id={} is trying to create user with ssn={}", authentication.getId(), user.getSsn());
        if (userRepository.existsBySsn(user.getSsn())) {
            LOG.info(auditMarker, "User with ssn={}  already exists", user.getSsn());
            FieldError error = new FieldError("user", "ssn", user.getSsn(), true, new String[]{"duplicate"}, new String[]{}, String.format("User with ssn=%s already exists", user.getSsn()));
            result.addError(error);
        }
        if (result.hasErrors()) {
            throw new BindException(result);
        }
        return ResponseEntity.ok(userService.save(user));
    }

    @PreAuthorize("hasAnyPermission('User', 'edit') and isManagerOf('User', #user)")
    @PostMapping("/{id}")
    public ResponseEntity<User> saveUser(@PathVariable("id") Long userId, @Validated @RequestBody User user, BindingResult result, RestAuthenticationToken authentication) throws BindException {
        if (!Objects.equals(userId, user.getId())) {
            LOG.warn(auditMarker, "User with id {} is trying to update user with id {}, when real id is {}", authentication.getId(), userId, user.getId());
            return ResponseEntity.badRequest().build();
        }
        if (!userRepository.exists(user.getId())) {
            LOG.warn(auditMarker, "User with id {} is trying to update data for not existed user with id {}", authentication.getId(), userId);
            return ResponseEntity.notFound().build();
        }
        LOG.info(auditMarker, "User with id={} is trying to update data for user with id={} ssn={}", authentication.getId(), user.getId(), user.getSsn());
        Long idForSsn = userRepository.findUserIdBySsn(user.getSsn());

        if (idForSsn != null && !Objects.equals(idForSsn, userId)) {
            LOG.info(auditMarker, "User with ssn={}  already exists", user.getSsn());
            FieldError error = new FieldError("user", "ssn", user.getSsn(), true, new String[]{"duplicate"}, new String[]{}, String.format("User with ssn=%s already exists", user.getSsn()));
            result.addError(error);
        }
        if (result.hasErrors()) {
            throw new BindException(result);
        }
        return ResponseEntity.ok(userService.save(user));
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/roles_to_create")
    public ResponseEntity<List<Role>> rolesToCreate(RestAuthenticationToken authentication) {
        return ResponseEntity.ok(userService.managedBy(roles(authentication)));
    }

    private List<RoleType> roles(RestAuthenticationToken authentication) {
        return authentication.getAuthorities().stream().map(au -> RoleType.valueOf(au.getAuthority())).collect(Collectors.toList());
    }
}
