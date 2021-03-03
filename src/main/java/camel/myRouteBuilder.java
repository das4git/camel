package camel;

import org.apache.camel.Exchange;
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
                        checkNumberAllFiles(numberAllFiles);
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
                        checkNumberAllFiles(numberAllFiles);
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
                        checkNumberAllFiles(numberAllFiles);
                        log.info("Received bad file №"
                                + numberBadFiles + " :"
                                + exchange.getIn()
                                .getHeader("CamelFileName"));
                    }
                });
    }

    public void checkNumberAllFiles(int numberAllFiles) {
        if(numberAllFiles==100) log.info("--------Recieved 100 files!---------");
    }

    public void insertTxtFileIntoDB(Exchange exchange) {
        int text_id = new Random().nextInt();
        String resultMessage = exchange.getIn().getBody(String.class);
        SimpleDateFormat formater = new SimpleDateFormat("HH:mm:ss,SSS");
        String time = formater.format(new Date());
        try {
            db.insertFields1(text_id, resultMessage, time);
            log.info("TXT file inserted into DB");
        } catch (SQLException e) {
            log.error("Exception: {}", e);
        }
    }
}