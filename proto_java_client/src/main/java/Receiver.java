
import jakarta.jms.*;
import org.apache.qpid.jms.JmsConnectionFactory;

public class Receiver {

    public static void main(String[] args) {
        String artemisAddress = "amqp://10.37.129.2:61616";
        String username = "admin";
        String password = "admin";
        String queueName = "test/java";



        try {
            JmsConnectionFactory connectionFactory = new JmsConnectionFactory(artemisAddress);
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);

            MessageConsumer consumer = session.createConsumer(queue);
            Message message = consumer.receive();

            // Start the timer
            long startTime = System.currentTimeMillis();

            if (message instanceof BytesMessage) {
                BytesMessage bytesMessage = (BytesMessage) message;
                byte[] protoBytes = new byte[(int) bytesMessage.getBodyLength()];
                bytesMessage.readBytes(protoBytes);


                // Print the size of the received data
                System.out.println("Received data size: " + protoBytes.length + " bytes");

                // Assuming your Proto class
                Api.Foo api = Api.Foo.parseFrom(protoBytes);

                // End the timer
                long endTime = System.currentTimeMillis();

                long elapsedTime = endTime - startTime;
                System.out.println("Receiver Message in " + elapsedTime + " ms");


                System.out.println("Received Proto Message:");
               // System.out.println(api.toString());
            } else {
                System.out.println("Received message is not a BytesMessage.");
            }

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
