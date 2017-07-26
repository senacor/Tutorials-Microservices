# Hints for Tutorial stage 00

Use the [String Boot Documentation](https://spring.io/guides/gs/rest-service/) for creating a REST controller for reference. You can just implement and run the service in IntelliJ IDEA.

You can use this code snippet to retrieve the IP address of the server as String:

```java

import java.net.InetAddress;

// (...)

    private String retrieveServerIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

// (...)
```

