package com.examflow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

@SpringBootApplication
public class OnlineExaminationApplication {

    private static final Logger logger = LoggerFactory.getLogger(OnlineExaminationApplication.class);

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(OnlineExaminationApplication.class);
        Environment env = app.run(args).getEnvironment();
        String port = env.getProperty("server.port", "8080");

        logger.info("====================================================================");
        logger.info(" ExamFlow - Online Examination & Result Processing System Started!");
        logger.info(" Portal URL: http://localhost:{}", port);
        logger.info(" Admin URL:  http://localhost:{}/admin/login", port);
        logger.info("====================================================================");
    }
}
