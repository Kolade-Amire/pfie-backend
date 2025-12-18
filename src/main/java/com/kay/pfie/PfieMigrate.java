package com.kay.pfie;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

public final class PfieMigrate {
    private PfieMigrate() {}

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx =
                new SpringApplicationBuilder(PfieBackendApplication.class)
                        .web(WebApplicationType.NONE)
                        .run(args);

        int code = SpringApplication.exit(ctx, () -> 0);
        System.exit(code);
    }
}