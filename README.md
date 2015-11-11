Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Dependency Injection DSL for Akka, using Guice

#### Goals:

- Concise DSL
- Inject to ```val```
- No annotations
- Elegant optionals
- Akka Actor injection that is parent aware
- Full support for traditional Guice patterns

#### Examples:

```scala
val something = Inject[Something] build
```

```scala
val something: Something = Inject[Something]
```

```scala
val somthingOptional = Inject[Something] optional
```

```scala
val somthingOptional: Option[Something] = Inject[Something]
```

```scala
val actor = InjectActor[IMyActor] build
```

```scala
val actor: ActorRef = InjectActor[IMyActor]
```

```scala
val actor = InjectActor[IMyActor] optional
```

```scala
val actor: Option[ActorRef] = InjectActor[IMyActor]
```

```scala
val namedString: String = Inject[String] annotated "namedThis"
```

```scala
val obj: IBar = Inject[IBar]
```

```scala
val optionalObj: Option[IBar] = Inject[IBaz]
```

```scala
val withArgsCtor: Baz = Inject[Baz] arguments("name", 1001)
```

```scala
val bob: Option[ActorRef] = InjectActor[IMyActor] named "bob"
```

```scala
val fromConfig: Option[ActorRef] = InjectActor[Actor] specified "path.from.cfg"
```

```scala
val cfg: Config = Inject[Config]
```

#### Notes:

- ```annotated``` with a name (ie. @Named)
- ```specified``` at path in config use fqcn to create instance
- ```arguments``` passed to ctor of the bound type
- if the lhs type is specified the builder can be build implicitly
- use ```InjectActor``` to inject Akka Actors, use ```Inject``` for everything else
- the use of specified will override any binding that may exist in a module
- SPI is provided by registering Modules as services
- Use lazy to break cycles
- Actor injection is for new instances only
- the application config is available by default through the Config binding

#### Todos

- Config key support, ie ```specified```, is not implemented yet
- Using ```Manifest``` for now as ScalaGuice is still bound to them
