package com.eztools.sl_backend.controller;

import com.eztools.sl_backend.dto.ApiResponse;
import com.eztools.sl_backend.dto.ShortenRequest;
import com.eztools.sl_backend.dto.ShortenResponse;
import com.eztools.sl_backend.service.ShortenService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

/**
 * 短链接生成控制器
 * @author <Rezven>
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ShortenController {
    private final ShortenService shortenService;
    
    @Value("${ShortLink.host}")
    private int serverHost;
    
    @PostMapping("/shorten")
    public ResponseEntity<ApiResponse<ShortenResponse>> createShortLink(
            @RequestBody ShortenRequest request) {
        String shortCode = shortenService.generateShortUrl(request.url(), 604800);
        return ResponseEntity.ok(ApiResponse.success(
                new ShortenResponse(
                        shortCode,
                        "http://serverHost:8080/" + shortCode,
                        java.time.Instant.now().plusSeconds(604800)
                )
        ));
    }
    
    @GetMapping("/{code}")
    public ResponseEntity<Void> redirect(
            @PathVariable String code
    ) {
        String url = shortenService.resolveShortCode(code);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(url))
                .build();
    }
}