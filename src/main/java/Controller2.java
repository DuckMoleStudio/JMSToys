import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;
import java.util.Random;

public class Controller2 {
    public static void main(String[] args) throws Exception {

        String myId = "Second";

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();

        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Queue requestQueue = session.createQueue("RequestQueue");
        Queue replyQueue = session.createQueue(myId);

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

                        System.out.println("Received result: " + additionRequest.getSenderId() +" #"
                                + additionRequest.getReqNo() + " "
                                + additionRequest.getArg1() + " + " + additionRequest.getArg2()
                                + " = " + additionRequest.getResult() + " in "
                                + (System.currentTimeMillis()-additionRequest.getTimeStart())
                                + " ms");
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
        Random random = new Random();

        for(int i=1;;i++){
            message = session.createTextMessage(objectMapper
                    .writeValueAsString(new AdditionRequest(random.nextInt(100), random.nextInt(100),
                            0, i, System.currentTimeMillis(), myId)));
            producer.send(message);
            System.out.println("Sent request: " + message.getText());
            Thread.sleep(1000);
        }
    }
}
