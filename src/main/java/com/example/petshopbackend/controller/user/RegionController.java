package com.example.petshopbackend.controller.user; // [MODIFIED] 包路径已更新为 user

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@Tag(name = "公共数据模块", description = "提供无需认证的公共数据，如行政区划")
@RestController
@RequestMapping("/api/user/regions") // [MODIFIED] API路径已更新为 /api/user/regions
@RequiredArgsConstructor
public class RegionController {

    private final ObjectMapper objectMapper;

    @Operation(summary = "获取中国省市区联动数据", description = "从静态JSON文件中读取并返回完整的省市区树形结构数据")
    @GetMapping
    public ResponseEntity<JsonNode> getRegions() throws IOException {
        ClassPathResource resource = new ClassPathResource("static/regions.json");
        JsonNode regions = objectMapper.readTree(resource.getInputStream());
        return ResponseEntity.ok(regions);
    }
}