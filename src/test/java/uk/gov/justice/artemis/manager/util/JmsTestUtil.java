package uk.gov.justice.artemis.manager.util;

import static org.apache.activemq.artemis.api.jms.ActiveMQJMSClient.createQueue;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.artemis.jms.client.ActiveMQQueueConnectionFactory;

public class JmsTestUtil {

    private static final QueueConnectionFactory JMS_CF = new ActiveMQQueueConnectionFactory("tcp://localhost:61616");
    private static QueueConnection JMS_CONNECTION;
    private static QueueSession JMS_SESSION;
    private static Map<String, Queue> QUEUES = new HashMap<>();
    private static Map<String, MessageConsumer> CONSUMERS = new HashMap<>();
    private static Map<String, MessageProducer> PRODUCERS = new HashMap<>();

    public static void putInQueue(final String queueName, final String msgText, final String... origAddress) throws JMSException {
        TextMessage message = JMS_SESSION.createTextMessage(msgText);
        if (origAddress.length > 0) {
            message.setStringProperty("_AMQ_ORIG_ADDRESS", origAddress[0]);
        }
        producerOf(queueName).send(message);
    }

    public static void putInQueue(final String queueName, final InputStream messageInput, final String... origAddress) throws JMSException {
        final Message message = JMS_SESSION.createBytesMessage();

        message.setObjectProperty("JMS_AMQ_InputStream", messageInput);

        if (origAddress.length > 0) {
            message.setStringProperty("_AMQ_ORIG_ADDRESS", origAddress[0]);
        }

        producerOf(queueName).send(message);
    }

    /**
     * Returns the number of messages that were removed from the queue.
     *
     * @param queueName - the name of the queue that is to be cleaned
     * @return the number of cleaned messagesß
     */
    public static int cleanQueue(final String queueName) throws JMSException {
        JMS_CONNECTION.start();
        final MessageConsumer consumer = consumerOf(queueName);

        int cleanedMessage = 0;
        while (consumer.receiveNoWait() != null) {
            cleanedMessage++;
        }
        JMS_CONNECTION.stop();
        return cleanedMessage;
    }

    public static MessageConsumer consumerOf(final String queueName) throws JMSException {
        return CONSUMERS.computeIfAbsent(queueName, name -> {
            try {
                return JMS_SESSION.createConsumer(queueOf(name));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }

    private static MessageProducer producerOf(final String queueName) throws JMSException {
        return PRODUCERS.computeIfAbsent(queueName, name -> {
            try {
                return JMS_SESSION.createProducer(queueOf(name));
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
        });
    }


    public static void closeJmsConnection() throws JMSException {
        CONSUMERS.clear();
        PRODUCERS.clear();
        QUEUES.clear();
        JMS_CONNECTION.close();
    }

    public static void openJmsConnection() throws JMSException {
        JMS_CONNECTION = JMS_CF.createQueueConnection();
        JMS_SESSION = JMS_CONNECTION.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);

    }

    private static Queue queueOf(final String queueName) {
        return QUEUES.computeIfAbsent(queueName, name -> createQueue(queueName));
    }


}
