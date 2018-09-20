package org.jim.bch;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BchApp implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BchApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
    }

}
