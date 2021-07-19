import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Controller {
    public static void main(String[] args) throws Exception {
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Queue requestQueue = session.createQueue("RequestQueue");
        Queue replyQueue = session.createQueue("ReplyQueue");

        MessageConsumer consumer = session.createConsumer(replyQueue);
        MessageProducer producer = session.createProducer(requestQueue);

        class ControllerListener implements MessageListener {
            @Override
            public void onMessage(Message message) {
                try {
                    if (message instanceof TextMessage) {
                        TextMessage textMessage = (TextMessage) message;
                        ObjectMapper objectMapper = new ObjectMapper();
                        AdditionRequest additionRequest = objectMapper
                                .readValue(textMessage.getText(), AdditionRequest.class);

                        System.out.println("Received result: "
                                + additionRequest.getArg1() + " + " + additionRequest.getArg2()
                                + " = " + additionRequest.getResult());
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

        MessageListener listener = new ControllerListener();

        consumer.setMessageListener(listener);


        ObjectMapper objectMapper = new ObjectMapper();
        TextMessage message;

        message = session.createTextMessage(objectMapper
                .writeValueAsString(new AdditionRequest(2,2,0)));
        producer.send(message);
        System.out.println("Sent request: " + message.getText());

        message = session.createTextMessage(objectMapper
                .writeValueAsString(new AdditionRequest(3,3,0)));
        producer.send(message);
        System.out.println("Sent request: " + message.getText());

        message = session.createTextMessage(objectMapper
                .writeValueAsString(new AdditionRequest(4,4,0)));
        producer.send(message);
        System.out.println("Sent request: " + message.getText());

        message = session.createTextMessage(objectMapper
                .writeValueAsString(new AdditionRequest(5,5,0)));
        producer.send(message);
        System.out.println("Sent request: " + message.getText());

        message = session.createTextMessage(objectMapper
                .writeValueAsString(new AdditionRequest(6,6,0)));
        producer.send(message);
        System.out.println("Sent request: " + message.getText());

    }
}
