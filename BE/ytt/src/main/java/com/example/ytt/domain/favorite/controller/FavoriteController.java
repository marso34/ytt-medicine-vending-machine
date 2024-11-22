package com.example.ytt.domain.favorite.controller;

import com.example.ytt.domain.favorite.service.FavoriteService;
import com.example.ytt.domain.user.auth.security.CustomUserDetails;
import com.example.ytt.domain.vendingmachine.dto.VendingMachineDto;
import com.example.ytt.global.common.annotation.SwaggerApi;
import com.example.ytt.global.common.response.ResponseDto;
import com.example.ytt.global.util.ResponseUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    /* 조회 */

    @GetMapping
    @SwaggerApi(summary = "즐겨찾기 자판기 조회", description = "즐겨찾기한 자판기 리스트 조회")
    public ResponseEntity<ResponseDto<List<VendingMachineDto>>> getFavoriteVendingMachines(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseUtil.success(favoriteService.getFavorites(user.getId()));
    }

    /* 생성, 삭제 */

    @PostMapping("/{machineId}")
    @SwaggerApi(summary = "즐겨찾기 추가", description = "자판기를 즐겨찾기에 추가하는 API")
    public ResponseEntity<ResponseDto<Boolean>> addFavorite(@PathVariable(value = "machineId") Long productId, @AuthenticationPrincipal CustomUserDetails user) {
        favoriteService.addFavorite(user.getId(), productId);

        return ResponseUtil.success(null);
    }

    @DeleteMapping("/{machineId}")
    @SwaggerApi(summary = "즐겨찾기 삭제", description = "자판기를 즐겨찾기에서 삭제하는 API")
    public ResponseEntity<ResponseDto<Boolean>> removeFavorite(@PathVariable(value = "machineId") Long productId, @AuthenticationPrincipal CustomUserDetails user) {
        boolean isRemoved = favoriteService.removeFavorite(user.getId(), productId);

        return ResponseUtil.success(isRemoved);
    }
}
