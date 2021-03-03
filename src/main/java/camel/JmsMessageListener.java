package camel;

import org.apache.camel.Exchange;
import org.apache.log4j.Logger;
import javax.jms.Message;
import javax.jms.MessageListener;

public class JmsMessageListener implements MessageListener {

    private static Logger log = Logger.getLogger(JmsMessageListener.class);

    @Override
    public void onMessage(Message message) {
        try {
            log.info(message.getStringProperty(Exchange.FILE_NAME));
        } catch (Exception e) {
            log.error("JmsFileListener Exception {}", e);
        }
    }
}