import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.activemq.ActiveMQConnectionFactory;

import javax.jms.*;

public class Model34_01 {
    public static void main(String[] args) throws Exception {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        Connection connection = connectionFactory.createConnection();
        connection.start();
        Session session = connection.createSession(false,
                Session.AUTO_ACKNOWLEDGE);

        TopicConnectionFactory topicConnectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");
        TopicConnection connection2 = topicConnectionFactory.createTopicConnection();
        connection2.start();
        TopicSession session2 = connection2.createTopicSession(false,
                Session.AUTO_ACKNOWLEDGE);

        Queue requestQueue = session.createQueue("RequestQueue");
        Topic replyTopic = session2.createTopic("ReplyTopic");

        MessageConsumer consumer = session.createConsumer(requestQueue);
        TopicPublisher producer = session2.createPublisher(replyTopic);

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

                        Thread.sleep(500);
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
