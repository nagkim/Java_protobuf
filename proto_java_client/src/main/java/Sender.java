import jakarta.jms.*;
import org.apache.qpid.jms.JmsConnectionFactory;
import java.util.Random;

public class Sender {

    public static void main(String[] args) {
        String artemisAddress = "amqp://10.37.129.2:61616";
        String username = "admin";
        String password = "admin";
        String queueName = "test/java";

        int sizeOFArray = 1000;

        try {
            JmsConnectionFactory connectionFactory = new JmsConnectionFactory(artemisAddress);
            connectionFactory.setUsername(username);
            connectionFactory.setPassword(password);

            Connection connection = connectionFactory.createConnection();
            connection.start();

            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            Queue queue = session.createQueue(queueName);

            MessageProducer producer = session.createProducer(queue);

            // Create your Proto message (Api.Foo in this case)
            Api.Foo.Builder fooBuilder = Api.Foo.newBuilder();

            // array int
            for (int i = 0; i < sizeOFArray; i++) {
                fooBuilder.addIntArray(i);
            }

            // array float
            for (int j = 0; j < sizeOFArray; j++) {
                fooBuilder.addFloatArray(j);
            }


            // array string
            String[] stringArray = new String[sizeOFArray];
            Random random = new Random();
            for (int i = 0; i < sizeOFArray; i++) {
                StringBuilder randomString = new StringBuilder();
                for (int j = 0; j < 10; j++) {
                    char randomChar = (char) (random.nextInt(26) + 'a');
                    randomString.append(randomChar);
                }
                stringArray[i] = randomString.toString();
                fooBuilder.addStringArray(stringArray[i]);
            }




            Api.Foo fooMessage = fooBuilder.build();


            // Start the timer
            long startTime = System.currentTimeMillis();
            // Convert the Proto message to bytes
            byte[] protoBytes = fooMessage.toByteArray();

            // Create a BytesMessage and set the Proto bytes as its payload
            BytesMessage bytesMessage = session.createBytesMessage();
            bytesMessage.writeBytes(protoBytes);

            // Send the message
            producer.send(bytesMessage);

            // End the timer
            long endTime = System.currentTimeMillis();

            long elapsedTime = endTime - startTime;
            System.out.println("Sent JSON Message in " + elapsedTime + " ms");

            System.out.println("Sent Proto Message:");
            System.out.println(fooMessage.toString());

            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
