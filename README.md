# Common utilities library

## Introspection
TODO...

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