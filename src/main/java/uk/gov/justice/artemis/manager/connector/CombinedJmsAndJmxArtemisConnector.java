package uk.gov.justice.artemis.manager.connector;

import uk.gov.justice.artemis.manager.connector.jms.JmsManagement;
import uk.gov.justice.artemis.manager.connector.jms.JmsProcessor;
import uk.gov.justice.artemis.manager.connector.jmx.JmxManagement;
import uk.gov.justice.artemis.manager.connector.jmx.JmxProcessor;
import uk.gov.justice.output.ConsolePrinter;

import java.util.Iterator;
import java.util.List;

public class CombinedJmsAndJmxArtemisConnector implements ArtemisConnector {

    private final JmxProcessor jmxProcessor = new JmxProcessor();
    private final JmxManagement jmxManagement = new JmxManagement(new ConsolePrinter());
    private final JmsProcessor jmsProcessor = new JmsProcessor();
    private final JmsManagement jmsManagement = new JmsManagement();

    @Override
    public List<MessageData> messagesOf(final String host, final String port, final String brokerName, final String destinationName) throws Exception {
        return jmsProcessor.process(host, port, destinationName, jmsManagement.browseMessages());
    }

    @Override
    public long remove(final String host, final String port, final String brokerName, final String destinationName, final Iterator<String> msgIds) throws Exception {
        return jmxProcessor.process(host, port, brokerName, destinationName, jmxManagement.removeMessages(msgIds));
    }

    @Override
    public long reprocess(final String host, final String port, final String brokerName, final String destinationName, final Iterator<String> msgIds) throws Exception {
        return jmxProcessor.process(host, port, brokerName, destinationName, jmxManagement.reprocessMessages(msgIds));
    }
}
