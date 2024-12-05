# hivemq-spring-boot-demo
A _Spring Boot_ demo application showing the usage of [hivemq-spring-boot-starter](https://github.com/janosch24/hivemq-spring-boot-starter).

### Usage
In order to use this demo, you first need to build the _hivemq-spring-boot-starter_.  
Follow the section on **building** the starter at [hivemq-spring-boot-starter](https://github.com/janosch24/hivemq-spring-boot-starter).

If you're done you can clone this repository and start the demo:

~~~cmd
git clone https://github.com/janosch24/hivemq-spring-boot-demo
gradlew bootRun
~~~

Using the given settings in _src/resources/application.yml_, you should then be able to connect to **localhost:1883** using
any _MQTT_-client, like [MQTT Explorer](https://mqtt-explorer.com/) or [NodeRED](https://nodered.org/docs/getting-started/).
   
* Subscribe to topic _boot/extensions/#_ to see some information about the running embedded extensions.  
* Subscribe to topic _boot/ping/#_ to see the output of the running embedded extensions.
   
For a general understanding of _HiveMQ Extension SDK_, see [_HiveMQ Extension Developer Guide_](https://docs.hivemq.com/hivemq/latest/extensions/index.html).  
To learn more about _HiveMQ-CE_ and embedded extensions, read the [_HiveMQ Community Edition_ Wiki](https://github.com/hivemq/hivemq-community-edition).  
