# Hints for Tutorial stage 01

Use the [String Boot Documentation]() for creating a REST controller.

You can use this code snippet to retrieve the IP address of the server as String:

```java

import java.net.InetAddress;

// (...)

    private String retrieveServerIpAddress() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }

// (...)
```

