package com.repair.controller;

import com.repair.dto.BindDormRequest;
import com.repair.dto.ChangePasswordRequest;
import com.repair.dto.RegisterRequest;
import com.repair.dto.UserView;
import com.repair.entity.User;
import com.repair.enums.Role;
import com.repair.security.LoginUserPrincipal;
import com.repair.service.UserService;
import com.repair.util.ValidateUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (req.getAccount() == null || req.getPassword() == null || req.getRole() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "account、password、role 不能为空"));
        }
        Role r = Role.fromCode(req.getRole());
        if (r == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "角色无效"));
        }
        if (!ValidateUtil.isAccountValid(req.getAccount(), r)) {
            return ResponseEntity.badRequest().body(Map.of("error", "学号/工号格式不正确"));
        }
        if (!ValidateUtil.isPasswordValid(req.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("error", "密码需 6–20 位且含字母与数字"));
        }
        boolean ok = userService.register(req.getAccount(), req.getPassword(), req.getRole());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "账号已存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{userId}/password")
    public ResponseEntity<?> changePassword(@PathVariable Long userId,
                                            @RequestBody ChangePasswordRequest req,
                                            @AuthenticationPrincipal LoginUserPrincipal principal) {
        if (principal == null || !principal.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "无权操作"));
        }
        boolean ok = userService.changePassword(userId, req.getOldPassword(), req.getNewPassword());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "用户不存在或旧密码错误"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PutMapping("/{userId}/dorm")
    public ResponseEntity<?> bindDorm(@PathVariable Long userId,
                                      @RequestBody BindDormRequest req,
                                      @AuthenticationPrincipal LoginUserPrincipal principal) {
        if (principal == null || !principal.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("error", "无权操作"));
        }
        userService.bindDorm(userId, req.getBuilding(), req.getRoomNo());
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<?> getById(@PathVariable Long userId,
                                     @AuthenticationPrincipal LoginUserPrincipal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        boolean admin = principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!admin && !principal.getUserId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        User user = userService.getById(userId);
        if (user == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(UserView.from(user));
    }
}
