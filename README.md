# Artemis Manager

[![Build Status](https://travis-ci.org/CJSCommonPlatform/artemis-manager.svg?branch=master)](https://travis-ci.org/CJSCommonPlatform/artemis-manager) [![Coverage Status](https://coveralls.io/repos/github/CJSCommonPlatform/artemis-manager/badge.svg?branch=master)](https://coveralls.io/github/CJSCommonPlatform/artemis-manager?branch=master)


## Browse DLQ

**Note: Browse uses JMS tcp port to connect to the Artemis broker.**

_java -jar artemis-manager.jar browse -host localhost -port 61616 -brokerName default_

* port - JMS tcp port
* brokerName - name of the broker configured in the broker.xml, use _default_ if no specific broker configuration provided

## Remove Message from DLQ

* Remove message by id

**Note: Remove uses JMX port to connect to the Artemis broker.**

_java -jar artemis-manager.jar remove -host localhost -port 3000 -brokerName default -msgId 12d8e63e-c842-11e6-986d-00e1000074d2_

* port - JMX port
* brokerName - name of the broker configured in the broker.xml, use _default_ if no specific broker configuration provided

* Remove multiple messages (provide list of message ids on input)

_echo msgId1 msgId2 | java -jar artemis-manager.jar remove -host localhost -port 3000 -brokerName default_

## Reprocess Message from DLQ

* Reprocess message by id

**Note: Reprocess uses JMX port to connect to the Artemis broker.**

_java -jar artemis-manager.jar reprocess -host localhost -port 3000 -brokerName default -msgId 12d8e63e-c842-11e6-986d-00e1000074d2_

* port - JMX port
* brokerName - name of the broker configured in the broker.xml, use _default_ if no specific broker configuration provided

* Reprocess multiple messages (provide list of message ids on input)

_echo msgId1 msgId2 | java -jar artemis-manager.jar reprocess -host localhost -port 3000 -brokerName default_

## Chaining Commands

* Chaining commands

This will remove all messages from DLQ that have been originally sent to the queue abracadabra

_java -jar target/artemis-manager.jar browse -host localhost -port 3000 -brokerName default | jgrep originalDestination=jms.queue.abracadabra -s msgId | java -jar target/artemis-manager.jar remove -host localhost -port 3000 -brokerName default_
