package com.paw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import com.paw.parser.DocumentParser;
import com.paw.vectorspace.VectorSpaceModel;

/**
 * This is the main start application from which the program initiates.
 */

@SpringBootApplication
@ComponentScan
public class Application {
    public static void main(String args[]){

        DocumentParser dp = new DocumentParser("classpath:train.csv","classpath:stopwords.txt");
        VectorSpaceModel vsm = new VectorSpaceModel(dp.contentMap,dp.stopWords);
        SpringApplication.run(Application.class, args);

    }
}
