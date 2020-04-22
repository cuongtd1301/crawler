package client;

import javax.jms.*;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

import java.util.Scanner;

public class Client {

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

    public static void main(String[] args) throws JMSException {
        System.out.println("url = " + url);

        /*
         * Getting JMS connection from the JMS server and starting it
         */
        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
        Connection connection = connectionFactory.createConnection();
        connection.start();

        /*
         * Creating a non transactional session to send/receive JMS message.
         */
        Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
//        Session session2 = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        /*
         * The queue will be created automatically on the server.
         */
        Destination destinationURLRequest = session.createQueue(queueURLRequest);
        Destination destinationResponse = session.createQueue(queueResponse);

        /*
         * Destination represents here our queue 'MESSAGE_QUEUE' on the JMS server.
         *
         * MessageProducer is used for sending messages to the queue.
         * MessageConsumer is used for receiving (consuming) messages
         */
        MessageProducer producer = session.createProducer(destinationURLRequest);
        MessageConsumer consumer = session.createConsumer(destinationResponse);

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.print("url: ");
            String txt = sc.nextLine();

            TextMessage message = session.createTextMessage(txt);
            producer.send(message);
            System.out.println("Message '" + message.getText() + "', Sent Successfully to the Queue");

            Message MessageResponse = consumer.receive();
            if (MessageResponse instanceof TextMessage) {
                TextMessage textMessage = (TextMessage) MessageResponse;
                System.out.println("Response: '" + textMessage.getText() + "'\n");
            }
        }

//        connection.close();
    }
}

