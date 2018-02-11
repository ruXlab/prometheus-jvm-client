# Prometheus JVM Client

[Public sandbox] JVM client for prometheus database

Intent: there is no client library for java/kotlin/groovy/scala/etc(JVM) for prometheus database.
Although database has very simple REST interface I believe it's still worth to
write easy-to-use and **type-safe** client   

### Current state

* Allows to construct client
* Instant vector queries (1 value for multiple metrics)
* Range vector queries (time series data for multiple metrics)
* Metrics are fully typed, user can supply it's own schema



### FAQ

* Q: Tests? Where are tests?
  
  A: Comming soon (c)


* Q: Can I use java?
  A: Sure, it's fully JVM compatible, thanks to kotlin
  
 
* Q: How can I help?

  A: There are a lot of things to do, please contact me
  
------------------------------

originally published on [github](https://github.com/ruXlab/prometheus-jvm-client)  by [ruX](https://rux.vc)

