Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Some DI for Akka using Guice

#### Goals:
```val explicit = Inject[Something].build```
```val foo: ActorRef = InjectActor[IMyActor]```
```val bar: String = Inject[String] annotated "the.prop.path"```
```val baz: IBaz = Inject[IBaz]```
```val ctord: Bing = Inject[Bing] arguments("name", 1001)```
```val actor: ActorRef = InjectActor[IMyActor] named "bob" specified "path.from.cfg"```
```val somthing: Option[Something] = Inject[Something] optional```
```val composed: Option[Bing] = Inject[Bing] annotated "the.prop.path" arguments("name", 1001) optional```

- ```annotated``` uses the @Named annotation
- ```specified``` uses the path to find a fqcn in the app config
- ```arguments``` are ctor args for the bound implementation
- if the lhs type is specified the builder can be build implicitly
- use ```InjectActor``` to inject Akka Actors, use ```Inject``` for everything else

- Todo: Config key support, ie ```specified```, is not implemented yet
- Todo: Using ```Manifest``` for now as ScalaGuice is still bound to them

### Common Errors

1) Injecting an Actor with ```Inject``` rather than ```InjectActor```.


    Error injecting constructor, akka.actor.ActorInitializationException: You cannot create an instance of [...] explicitly using the constructor (new). You have to use one of the 'actorOf' factory methods to create a new actor.
