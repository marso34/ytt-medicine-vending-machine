package com.example.ytt.domain.management.controller;

import com.example.ytt.domain.management.service.ManagementService;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.user.dto.Role;
import com.example.ytt.domain.user.dto.UserDto;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.error.code.ExceptionType;
import com.example.ytt.global.util.ResponseUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/management")
@RequiredArgsConstructor
@Tag(name = "관리자", description = "자판기 관리 API")
public class ManagementController {

    private final ManagementService managementService;

    /* 조회 */

    @GetMapping
    @SwaggerApi(summary = "관리 자판기 조회", description = "관리하는 자판기 리스트 조회")
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getManagedVendingMachines(@AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기 관리 목록에 접근할 권한이 없습니다.", null);
        }

        return ResponseUtil.success(managementService.getManagedVendingMachines(user.getId()));
    }

    @GetMapping("{machineId}")
    @SwaggerApi(summary = "자판기 관리자 조회", description = "특정 자판기의 관리자 리스트 조회")
    public ResponseEntity<ResponseDto<List<UserDto>>> getManagers(@PathVariable("machineId") Long machineId, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기 관리 권한이 없습니다.", null);
        }

        return ResponseUtil.success(managementService.getManagers(machineId));
    }

    /* 생성, 삭제 */

    // TODO: RequestBody 추가해서 다른 관리자를 삭제하는 방향으로 수정할 것 (현재는 자신을 삭제하는 방향)

    @PostMapping("/{machineId}")
    @SwaggerApi(summary = "자판기 관리자 추가 (사용X)", description = "특정 자판기의 관리자를 추가하는 API", responseCode = "201")
    public ResponseEntity<ResponseDto<Boolean>> addFavorite(
            @PathVariable(value = "machineId") Long machineId,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기 관리 권한이 없습니다.", null);
        }

        managementService.addManagerById(user.getId(), machineId);

        return ResponseUtil.success(HttpStatus.CREATED, "관리자 추가 완료", null);
    }

    @DeleteMapping("/{machineId}")
    @SwaggerApi(summary = "자판기 관리자 삭제 (사용X)", description = "특정 자판기의 관리자를 삭제하는 API", responseCode = "204")
    public ResponseEntity<ResponseDto<Boolean>> removeFavorite(@PathVariable(value = "machineId") Long machineId, @AuthenticationPrincipal CustomUserDetails user) {
        if (user.getRole().equals(Role.CUSTOMER)) {
            return ResponseUtil.error(ExceptionType.FORBIDDEN_USER, "자판기 관리 권한이 없습니다.", null);
        }

        managementService.removeManager(user.getId(), machineId);

        return ResponseUtil.success(HttpStatus.NO_CONTENT, "관리자 삭제 완료", null);
    }

}
