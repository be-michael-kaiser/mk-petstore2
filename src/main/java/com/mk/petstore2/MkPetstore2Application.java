package com.mk.petstore2;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme("mk-petstore2")
public class MkPetstore2Application implements AppShellConfigurator {

    public static void main(String[] args) {
        SpringApplication.run(MkPetstore2Application.class, args);
    }
}
