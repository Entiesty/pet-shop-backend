package com.example.petshopbackend.config;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.models.media.Schema;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * SpringDoc/Knife4j 的自定义配置
 */
@Configuration
public class SwaggerConfig {

    /**
     * 定义一个OpenApiCustomizer的Bean，用于解决MyBatis-Plus的Page对象在Swagger中无法解析的问题
     */
    @Bean
    public OpenApiCustomizer pageableOpenApiCustomizer() {
        return openApi -> {
            // 我们为MyBatis-Plus的Page类定义一个全局的、统一的Schema（文档说明）
            Schema<?> pageSchema = new Schema<>()
                    .name("Page") // Schema的名称
                    .description("MyBatis-Plus分页结果") // Schema的描述
                    .addProperty("records", new Schema<>().type("array").description("当前页数据列表"))
                    .addProperty("total", new Schema<>().type("integer").format("int64").description("总记录数"))
                    .addProperty("size", new Schema<>().type("integer").format("int64").description("每页显示数量"))
                    .addProperty("current", new Schema<>().type("integer").format("int64").description("当前页码"));

            // 将这个自定义的Schema添加到OpenAPI的组件中
            openApi.getComponents().addSchemas("MybatisPlusPage", pageSchema);
        };
    }
}
