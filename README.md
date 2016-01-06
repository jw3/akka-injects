Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Dependency Injection DSL for Akka, using Google Guice

Uses an [Akka Extension](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html) to propagate the Injector, for a seamless DI experience.

#### Goals:

- Concise DSL
- Injects to ```val```
- Annotations not required
- Clean ```Option``` integration
- Actors inject as children when in parent scope
- Transparently propagate Injector instance
- Full support of traditional Guice patterns

#### Installation:

The Inject Extension will be loaded on demand by default.

If you want to load the extension at ```ActorSystem``` creation time you can [Load from Configuration](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html#Loading_from_Configuration).

```HOCON
akka {
  extensions = ["wiii.inject.InjectExt"]
}
```

#### Configuration Options:

- "akka.inject.mode": specifies the module discovery strategy [manual | config | spi]
- "akka.inject.modules": specifies the FQCN list of Modules when in 'config' mode
- "akka.inject.cfg": specify whether to provide the application Config through the injector

#### Modes:

- Manual : "manual" : discovery mode that only uses Modules added through InjectExtBuilder
- Configuration : "config" : discovery mode that uses modules specified in the CfgModuleDiscoveryKey
- SPI : "spi" : discovery mode that uses modules specified through SPI

The default discovery mode is "manual"

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
val configed: Option[ActorRef] = InjectActor[Actor] fromConfig "path.from.cfg"
```

```scala
val cfg: Config = Inject[Config]
```

#### Notes:

- ```annotated``` with a name (ie. @Named)
- ```fromConfig``` use value of path for AnyVal or fqcn to create instance
- ```args``` passed to ctor of the bound type
- if the lhs type is specified the builder can be build implicitly
- use ```InjectActor``` to inject Akka Actors, use ```Inject``` for everything else
- ```fromConfig``` will override module bindings, and will fallback on them if needed
- SPI is provided by registering Modules as services
- Use lazy to break cycles
- Actor injection is for new instances only
- the application config is available by default through the Config binding

#### Todos

- Config key support, ie ```fromConfig```, is not implemented yet
- Using ```Manifest``` for now as ScalaGuice is still bound to them

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/RxGpio/issues).

## LICENSE

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
