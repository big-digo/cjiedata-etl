package com.cjiedata.kettle;

import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.*;

@SpringBootApplication
public class App {
    private static App app;
    public static App getInstance() {
        if (app == null) {
            app = new App();
        }
        return app;
    }

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
