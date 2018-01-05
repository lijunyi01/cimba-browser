package com.allcom.app;

import com.allcom.App;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.ConfigurableEmbeddedServletContainer;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackageClasses = App.class)
public class Application implements EmbeddedServletContainerCustomizer {

//    private static Logger log = LoggerFactory.getLogger(Application.class);

    @Value("${system.embeddedtomcatport}")
    private int tomcatport;

    @Override
    public void customize(ConfigurableEmbeddedServletContainer container){
        container.setPort(tomcatport);
        //jwt 不需要session机制，通过token认证
//        container.setSessionTimeout(600, TimeUnit.SECONDS);
    }


    public static void main(String[] args) throws Exception {
        SpringApplication.run(Application.class);

    }
}
