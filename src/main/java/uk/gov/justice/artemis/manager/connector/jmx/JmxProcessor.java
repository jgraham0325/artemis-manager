package uk.gov.justice.artemis.manager.connector.jmx;

import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import static javax.management.MBeanServerInvocationHandler.newProxyInstance;
import static javax.management.remote.JMXConnectorFactory.connect;
import static org.apache.activemq.artemis.api.config.ActiveMQDefaultConfiguration.getDefaultJmxDomain;

import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;

import org.apache.activemq.artemis.api.core.management.ObjectNameBuilder;
import org.apache.activemq.artemis.api.jms.management.JMSQueueControl;

public class JmxProcessor {

    private static final String JMX_URL = "service:jmx:rmi:///jndi/rmi://%s:%s/jmxrmi";

    public <T> T process(final String host,
                         final String port,
                         final String brokerName,
                         final String destinationName,
                         final JmxManagementFunction<T> jmxManagementFunction) throws Exception {

        final ObjectName on = ObjectNameBuilder.create(getDefaultJmxDomain(), brokerName, true).getJMSQueueObjectName(destinationName);

        try (final JMXConnector connector = connect(new JMXServiceURL(format(JMX_URL, host, port)), emptyMap())) {
            final JMSQueueControl queueControl = newProxyInstance(connector.getMBeanServerConnection(), on, JMSQueueControl.class, false);

            return jmxManagementFunction.apply(queueControl);
        }
    }
}
