import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import javax.jms.Queue;
import java.util.*;

public class Model12_02 {
    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Queue requestQueue = session.createQueue("RequestQueue");
        MessageConsumer consumer = session.createConsumer(requestQueue);

        List<MessageProducer> producers = new ArrayList<>();

        class ModelListener implements MessageListener {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {

                        TextMessage textMessage = (TextMessage) message;
                        ObjectMapper objectMapper = new ObjectMapper();
                        AdditionRequest additionRequest = objectMapper
                                .readValue(textMessage.getText(), AdditionRequest.class);

                        MessageProducer producer = session.createProducer(session.createQueue(""));
                        boolean newProducer = true;

                        for(MessageProducer curProducer: producers){
                            if(curProducer.getDestination().toString().equals(additionRequest.getSenderId())){
                                producer = curProducer;
                                newProducer = false;
                            }
                        }
                        if(newProducer){
                            Queue replyQueue = session.createQueue(additionRequest.getSenderId());
                            producer = session.createProducer(replyQueue);
                            producers.add(producer);
                        }

                        additionRequest.setResult(additionRequest.getArg1()+additionRequest.getArg2());
                        TextMessage outMessage = session.createTextMessage(objectMapper
                                .writeValueAsString(additionRequest));
                        producer.send(outMessage);

                        System.out.println("Processing "
                                + textMessage.getText() + ".....");
                        Thread.sleep(100);
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        MessageListener listener = new ModelListener();

        consumer.setMessageListener(listener);
    }
}
