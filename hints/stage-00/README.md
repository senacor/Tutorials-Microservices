# Hints for Tutorial stage 00

Use this [String Boot Guide on RestControllers](https://spring.io/guides/gs/rest-service/) for reference. You can just implement and run the service in IntelliJ IDEA after importing the customer project (generated with the Spring Initializr).

You can use this code snippet to retrieve the IP address of the server as String:

```java

import java.net.InetAddress;

// (...)

    private String retrieveServerIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

// (...)
```

