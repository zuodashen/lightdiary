package com.lightdiary.config;

import com.lightdiary.modules.ums.model.UmsResource;
import com.lightdiary.modules.ums.service.UmsAdminService;
import com.lightdiary.modules.ums.service.UmsResourceService;
import com.lightdiary.security.component.DynamicSecurityService;
import com.lightdiary.security.config.SecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * lightdiary-security模块相关配置
 * 自定义配置，用于配置如何获取用户信息及动态权限
 */
@Configuration
public class LightDiarySecurityConfig {

    @Autowired
    private UmsAdminService adminService;
    @Autowired
    private UmsResourceService resourceService;

    @Bean
    public UserDetailsService userDetailsService() {
        //获取登录用户信息
        return username -> adminService.loadUserByUsername(username);
    }

    @Bean
    public DynamicSecurityService dynamicSecurityService() {
        return new DynamicSecurityService() {
            @Override
            public Map<String, ConfigAttribute> loadDataSource() {
                Map<String, ConfigAttribute> map = new ConcurrentHashMap<>();
                List<UmsResource> resourceList = resourceService.list();
                for (UmsResource resource : resourceList) {
                    if (resource.getUrl() != null && !resource.getUrl().isEmpty()) {
                        map.put(resource.getUrl(), new org.springframework.security.access.SecurityConfig(resource.getUrl()));
                    }
                }
                return map;
            }
        };
    }
}
