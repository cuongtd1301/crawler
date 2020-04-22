package server;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.net.UnknownHostException;

/*
 * This class is used to receive the text message from the queue.
 */
public class Server {

    /*
     * URL of the JMS server. DEFAULT_BROKER_URL will just mean that JMS server is on localhost
     *
     * default broker URL is : tcp://localhost:61616"
     */
    private static String url = ActiveMQConnection.DEFAULT_BROKER_URL;

    /*
     * Queue Name.You can create any/many queue names as per your requirement.
     */
    private static String queueURLRequest = "URL_Request_QUEUE";
    private static String queueResponse = "Response_QUEUE";

    public static void main(String[] args) throws JMSException, IOException {
        System.out.println("url = " + url);

        /*
         * Getting JMS connection from the JMS server and starting it
         */
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        /*
         * Creating session for receiving messages
         */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        /*
         * Destination represents here our queue 'MESSAGE_QUEUE' on the JMS server.
         *
         * MessageConsumer is used for receiving messages from the queue.
         */
        Destination destinationURLRequest = session.createQueue(queueURLRequest);
        Destination destinationResponse = session.createQueue(queueResponse);

        /*
         * MessageConsumer is used for receiving (consuming) messages
         */
        MessageConsumer consumerRequest = session.createConsumer(destinationURLRequest);
        MessageProducer producerResponse = session.createProducer(destinationResponse);

        /*
         * Here we receive the message.
         */
        while (true) {
            Message messageRequest = consumerRequest.receive();

            if (messageRequest instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) messageRequest;
                System.out.println("Received message '" + textMessage.getText() + "'");

                String response;
                try {
                    Document doc = Jsoup.connect(textMessage.getText()).get();
                    System.out.println(doc);
                    response = "Crawl successfully";
                } catch (UnknownHostException e) {
                    System.out.println("Unknown host");
                    response = "Unknown host";
                } catch (IllegalArgumentException e) {
                    System.out.println("Malformed URL");
                    response = "Malformed URL";
                }

                TextMessage messageResponse = session.createTextMessage(response);
                producerResponse.send(messageResponse);
            }
        }

//        connection.close();
    }
}

