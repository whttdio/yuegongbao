package com.yuegongbao.ygb.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 粤工保外部集成配置。
 *
 * @author yuegongbao
 */
@Component
@ConfigurationProperties(prefix = "ygb.integration")
public class YgbIntegrationProperties
{
    /**
     * 集成模式，当前默认 stub。
     */
    private String mode = "stub";

    public String getMode()
    {
        return mode;
    }

    public void setMode(String mode)
    {
        this.mode = mode;
    }

    public boolean isStub()
    {
        return "stub".equalsIgnoreCase(mode);
    }
}
