package dfjinxin.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

@SpringBootApplication
public class DfjinxinDemoApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(DfjinxinDemoApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DfjinxinDemoApplication.class);
    }
}
