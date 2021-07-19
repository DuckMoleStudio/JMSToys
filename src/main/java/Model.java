import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Model {
    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Queue requestQueue = session.createQueue("RequestQueue");
        Queue replyQueue = session.createQueue("ReplyQueue");

        MessageConsumer consumer = session.createConsumer(requestQueue);
        MessageProducer producer = session.createProducer(replyQueue);

        class ModelListener implements MessageListener {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        ObjectMapper objectMapper = new ObjectMapper();
                        AdditionRequest additionRequest = objectMapper
                                .readValue(textMessage.getText(), AdditionRequest.class);
                        additionRequest.setResult(additionRequest.getArg1()+additionRequest.getArg2());
                        TextMessage outMessage = session.createTextMessage(objectMapper
                                .writeValueAsString(additionRequest));
                        producer.send(outMessage);

                        System.out.println("Processing "
                                + textMessage.getText() + ".....");
                    }
                } catch (JMSException e) {
                    System.out.println("Caught:" + e);
                    e.printStackTrace();
                } catch (JsonMappingException e) {
                    e.printStackTrace();
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                }
            }
        }

        MessageListener listener = new ModelListener();

        consumer.setMessageListener(listener);
    }
}
