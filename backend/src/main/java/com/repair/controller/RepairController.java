package com.repair.controller;

import com.repair.dto.CreateRepairRequest;
import com.repair.dto.UpdateRepairStatusRequest;
import com.repair.entity.RepairOrder;
import com.repair.entity.User;
import com.repair.security.LoginUserPrincipal;
import com.repair.service.RepairService;
import com.repair.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/repairs")
public class RepairController {

    private final RepairService repairService;
    private final UserService userService;

    public RepairController(RepairService repairService, UserService userService) {
        this.repairService = repairService;
        this.userService = userService;
    }

    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> create(@RequestBody CreateRepairRequest req,
                                    @AuthenticationPrincipal LoginUserPrincipal principal) {
        User u = userService.getById(principal.getUserId());
        if (u.getBuilding() == null || u.getRoomNo() == null
                || u.getBuilding().isBlank() || u.getRoomNo().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "请先绑定宿舍"));
        }
        if (req.getDescription() == null || req.getDescription().isBlank() || req.getDeviceType() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "设备类型与问题描述不能为空"));
        }
        Long id = repairService.createOrder(
                principal.getUserId(),
                u.getBuilding(),
                u.getRoomNo(),
                req.getDeviceType(),
                req.getDescription().trim(),
                req.getPriority() != null ? req.getPriority() : 2
        );
        return ResponseEntity.ok(Map.of("id", id));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('STUDENT')")
    public List<RepairOrder> myOrders(@AuthenticationPrincipal LoginUserPrincipal principal) {
        return repairService.getMyOrders(principal.getUserId());
    }

    @PostMapping("/{orderId}/cancel")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> cancel(@PathVariable Long orderId,
                                    @AuthenticationPrincipal LoginUserPrincipal principal) {
        boolean ok = repairService.cancelOrder(orderId, principal.getUserId());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "无法取消"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @PostMapping("/{orderId}/rate")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<?> rate(@PathVariable Long orderId,
                                  @RequestParam int rating,
                                  @AuthenticationPrincipal LoginUserPrincipal principal) {
        boolean ok = repairService.rateOrder(orderId, principal.getUserId(), rating);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "无法评分"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<RepairOrder> listAll(@RequestParam(required = false) Integer status) {
        return repairService.getAllOrders(status);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<RepairOrder> detail(@PathVariable Long orderId,
                                              @AuthenticationPrincipal LoginUserPrincipal principal) {
        RepairOrder order = repairService.getOrderDetail(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        boolean admin = principal.getAuthorities().stream()
                .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!admin && !order.getStudentId().equals(principal.getUserId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/{orderId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStatus(@PathVariable Long orderId,
                                          @RequestBody UpdateRepairStatusRequest req,
                                          @AuthenticationPrincipal LoginUserPrincipal principal) {
        boolean ok = repairService.updateStatus(orderId, principal.getUserId(), req.getStatus(), req.getRemark());
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "报修单不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @DeleteMapping("/{orderId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long orderId) {
        boolean ok = repairService.deleteOrder(orderId);
        if (!ok) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "报修单不存在"));
        }
        return ResponseEntity.ok(Map.of("success", true));
    }

    @GetMapping("/stats/by-status")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, int[]> countByStatus() {
        return Map.of("counts", repairService.countByStatus());
    }
}
