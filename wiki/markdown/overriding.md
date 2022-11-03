## Overriding Message Service Library

When building ``eiffel-remrem-generate`` and ``eiffel-remrem-publish`` services,
target packages include also ``eiffel-remrem-semantics`` library
of a version given by services' ``POM`` file. This can be easily verified sending
``/versions`` request to the services. It produces something like that
~~~
{
  "serviceVersion": {
    "version": "2.1.4"
  },
  "endpointVersions": {
    "semanticsVersion": "2.2.1"
  }
}
~~~

If there is a need, for whatever reason, to use different version of semantics
library, appropriate JAR file can be stored in a directory listed as value of Java
option ``-classpath`` utilizing [wildcard mechanism](https://docs.oracle.com/javase/7/docs/technotes/tools/windows/classpath.html).
For example, when ``eiffel-remrem-semantics-2.1.3.jar``
is stored in, say, ``/eiffel/protocols``, the directory must be included in ``java``
command
~~~
java ... -classpath "...:/eiffel/protocols/*:..." -jar .../generate.war
~~~

Note, that [Resources 'earlier' on the classpath take precedence over resources that are specified after them](https://stackoverflow.com/questions/6644440/java-which-of-multiple-resources-on-classpath-jvm-takes).

New request to ``/versions`` now clearly proves that implementation from newly
deployed library is used instead of build-in one.
~~~
{
  "serviceVersion": {
    "version": "2.1.4"
  },
  "endpointVersions": {
    "semanticsVersion": "2.1.3"
  }
}
~~~


This  feature can be utilized to load completely different implementation of Eiffel
protocol, for example ``eiffel3``, by adding corresponding library into directory
included in ``-classpath``, i.e. ``/eiffel/protocols``. The newly added library
causes that output of ``/versions`` request looks like this
~~~
{
  "serviceVersion": {
    "version": "2.1.4"
  },
  "endpointVersions": {
    "semanticsVersion": "2.1.3",
    "eiffel3MessagingVersion": "29.0.9"
  }
}
~~~

Particular version of ``eiffel-remrem-semantics`` can be downloaded from
[JitPack](https://jitpack.io/#eiffel-community/eiffel-remrem-semantics).

A practial example can be found in chapter [Override RemRem-Publish Eiffel Protocol Version](https://github.com/eiffel-community/eiffel-remrem-publish/blob/master/wiki/markdown/run.md#override-remrem-publish-eiffel-protocol-version).
