# Common utilities library

## Introspection
TODO...

### The Lookup object
From: [Java 17 API](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/lang/invoke/MethodHandles.Lookup.html)
A Lookup object can be shared with other trusted code, such as a metaobject protocol.
A shared Lookup object delegates the capability to create method handles on private members of the lookup class.
Even if privileged code uses the Lookup object, the access checking is confined to the privileges of the original lookup class.

### Accessing base Java classes
Add the following JVM options to your launch configuration:
```
--add-opens java.base/java.lang=ALL-UNNAMED
--add-opens java.base/java.nio=ALL-UNNAMED
--add-opens java.base/sun.nio.ch=ALL-UNNAMED
```
Read [Five Command Line Options To Hack The Java Module System](https://nipafx.dev/five-command-line-options-hack-java-module-system/#Reflectively-Accessing-Internal-APIs-With--add-opens)
to know more.
### Access related exceptions
See: https://stackoverflow.com/a/41265267

### Last resort of access in your own classes
The Invokation API enunciates the pattern of having your own classes 
declare a method that returns a Lookup to themselves. You could then
pass the result of calling that method on a target class to this library.
