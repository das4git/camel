package camel;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.spring.SpringRouteBuilder;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class myRouteBuilder extends SpringRouteBuilder {

    private int numberXmlFiles=0;
    private int numberTxtFiles=0;
    private int numberBadFiles=0;
    private int numberAllFiles=0;
    private DB db;

    public DB getDb() { return db; }

    public void setDb(DB db) { this.db = db; }
    SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss,SSS");

    public void configure() {

        from("file:src/data?noop=true")
                .transacted()
                .choice()
                .when(header("CamelFileName").endsWith(".xml"))
                .to("jms:queue")
                .process(new Processor(){
                    public void process(Exchange exchange) throws Exception {
                        numberXmlFiles++;
                        numberAllFiles++;
                        checkNumberAllFiles(numberAllFiles,exchange);
                        log.info("Received XML file №"
                                + numberXmlFiles + " :"
                                + exchange.getIn()
                                .getHeader("CamelFileName"));
                    }
                })
                .when(header("CamelFileName").endsWith(".txt"))
                .to("jms:queue").
                process(new Processor(){
                    public void process(Exchange exchange) throws Exception {
                        numberTxtFiles++;
                        numberAllFiles++;
                        checkNumberAllFiles(numberAllFiles, exchange);
                        log.info("Received TXT file №"
                                + numberTxtFiles +" :"
                                + exchange.getIn()
                                .getHeader("CamelFileName"));
                        insertTxtFileIntoDB(exchange);
                    }
                })
                .otherwise()
                .to("jms:queue:invalid")
                .process(new Processor(){
                    public void process(Exchange exchange) throws Exception {
                        numberBadFiles++;
                        numberAllFiles++;
                        checkNumberAllFiles(numberAllFiles, exchange);
                        log.info("Received invalid file №"
                                + numberBadFiles + " :"
                                + exchange.getIn()
                                .getHeader("CamelFileName"));
                    }
                })
                .end()
                .choice()
                .when(header("to").isNotNull())
                .to("smtps://smtp.gmail.com:465?username=yourEmail@gmail.com&password=yourPassword")
                .end();
    }

    public void checkNumberAllFiles(int numberAllFiles, Exchange exchange) {
        if(numberAllFiles==100) {
            log.info("--------Recieved 100 files!---------") ;
            sendMail(exchange);
        }
    }

    public void insertTxtFileIntoDB(Exchange exchange) {
        int text_id = new Random().nextInt();
        String resultMessage = exchange.getIn().getBody(String.class);
        String time = formater.format(new Date());
        try {
            db.insertFields1(text_id, resultMessage, time);
            log.info("TXT file inserted into DB");
        } catch (SQLException e) {
            log.error("Exception: {}", e);
        }
    }

    private void sendMail(Exchange exchange) {

        StringBuilder messageBody = new StringBuilder();
        messageBody.append("./src/data contains " + String.valueOf(numberAllFiles)
                + " files. Current time=" + formater.format(new Date()) + "\n");
        messageBody.append(String.valueOf(numberXmlFiles) + " XML files\n");
        messageBody.append(String.valueOf(numberTxtFiles) + " TXT files\n");
        messageBody.append(String.valueOf(numberBadFiles) + " invalid files\n");
        Message message = exchange.getOut();
        message.setHeader("to", "yourEmaildas@gmail.com");
        message.setHeader("subject", "Files from ./src/data");
        message.setBody(messageBody.toString());
    }
}