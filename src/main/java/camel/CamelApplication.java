package camel;

import org.apache.log4j.Logger;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelApplication {
    private static Logger log = Logger.getLogger(camel.CamelApplication.class.getName());
    public static void main(String[] args) throws InterruptedException {

        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                "applicationContext.xml");
        log.info("ApplicationContext getting");

        context.start();
        Thread.sleep(10000);
        context.close();
    }
}

