package com.lightdiary.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 应用启动成功后输出访问地址
 */
@Component
public class ApplicationStartupRunner implements ApplicationRunner {

    private static final String SEPARATOR = "----------------------------------------------------------";

    @Value("${spring.application.name:lightdiary}")
    private String applicationName;

    private final Environment environment;

    public ApplicationStartupRunner(Environment environment) {
        this.environment = environment;
    }

    @Override
    public void run(ApplicationArguments args) {
        String port = environment.getProperty("local.server.port", environment.getProperty("server.port", "8080"));
        String contextPath = environment.getProperty("server.servlet.context-path", "");
        if (contextPath == null || "/".equals(contextPath)) {
            contextPath = "";
        }
        String baseUrl = "http://127.0.0.1:" + port + contextPath;

        System.out.println();
        System.out.println(SEPARATOR);
        System.out.println("应用 '" + applicationName + "' 启动成功！访问连接：");
        System.out.println("在线接口文档：" + baseUrl + "/doc.html");
        System.out.println("Swagger UI  ：" + baseUrl + "/swagger-ui/");
        System.out.println("前台博客 API：" + baseUrl + "/api/settings");
        System.out.println("后台登录 API ：" + baseUrl + "/admin/login");
        System.out.println(SEPARATOR);
        System.out.println();
    }
}
