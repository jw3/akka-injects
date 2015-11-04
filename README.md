Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Dependency Injection DSL for Akka, using Guice

#### Goals:

- Inject to val
- Annotation-less injections
- Support traditional Guice patterns
- Flexibility
- Context sensitive Akka Actor injection
- Concise, Intuitive DSL

#### Examples:

```val something = Inject[Something].build```

```val foo: ActorRef = InjectActor[IMyActor]```

```val bar: String = Inject[String] annotated "the.prop.path"```

```val baz: IBaz = Inject[IBaz]```

```val ctord: Bing = Inject[Bing] arguments("name", 1001)```

```val actor: ActorRef = InjectActor[IMyActor] named "bob" specified "path.from.cfg"```

```val somthing: Option[Something] = Inject[Something] optional```

```val composed: Option[Bing] = Inject[Bing] annotated "the.prop.path" arguments("name", 1001) optional```

- ```annotated``` with a name (ie. @Named)
- ```specified``` at path in config use fqcn to create instance
- ```arguments``` passed to ctor of the bound type
- if the lhs type is specified the builder can be build implicitly
- use ```InjectActor``` to inject Akka Actors, use ```Inject``` for everything else
- the use of specified will override any binding that may exist in a module
- SPI is provided by registering Modules as services
- Use lazy to break cycles
- Actor injection is for new instances only

- Todo: Config key support, ie ```specified```, is not implemented yet
- Todo: Using ```Manifest``` for now as ScalaGuice is still bound to them
