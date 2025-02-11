# hivemq-spring-boot-demo
A _Spring Boot 3_ demo application showing the usage of [hivemq-spring-boot-starter](https://github.com/janosch24/hivemq-spring-boot-starter).

### Usage
In order to use this demo, you first need to build the _hivemq-spring-boot-starter_.  
Follow the section on **building** the starter at [hivemq-spring-boot-starter](https://github.com/janosch24/hivemq-spring-boot-starter).

If you're done, you can clone this repository and start the demo:

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

### Some caveats
Following some restrictions figured out so far.

#### JAXB
As stated already for the _hivemq-spring-boot-starter_ we had to _downgrade_ some _Spring_ managed dependencies regarding
the _JAXB_ XML config file parsing for _HiveMQ_ (_javax_ style vs. _jakarta_ style).  
Take a look at the _gradle.properties_ file of this project to find out about the used dependency versions.   
So, if there are other components in your application, requiring the original _Spring_ managed dependencies, it probably
will result in some clashes. Currently, there is no work-around for such cases.

#### Logging
_HiveMQ_ comes with a _logback.xml_, which will take precedence unless you have defined your own one.
If you did, in turn you cannot include the one from _HiveMQ_, as it is not ready to be included.
Recommendation is, to force _Spring Boot_ to use your own _Spring_-ready _logback-spring.xml_ like this:

~~~yaml
logging:
  config: classpath:logback-spring.xml
~~~

Then don't forget to include the _logback-spring-hivemq.xml_ provided by the _hivemq-spring-boot-starter_ in your own
_logback-spring.xml_.
