Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Dependency Injection DSL for Akka, using Google Guice.

Implemented as an [Akka Extension](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html) which allows for hands-off creation and management of the Injector.

#### Installation

The Inject Extension will be loaded on demand by default.

If you want to load the extension at ```ActorSystem``` creation time you can [Load from Configuration](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html#Loading_from_Configuration).

```HOCON
akka {
  extensions = ["com.rxthings.di.InjectExt"]
}
```

#### Artifacts

Add a resolver to your sbt build

```resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"```

Add dependency

```"com.rxthings" %% "akka-injects" % "0.3"```

#### Imports
```import com.rxthings.di._```

#### Configuration Options

- ```akka.inject.mode```: specifies the module discovery strategy ```[manual | config | spi]```
- ```akka.inject.modules```: specifies the FQCN list of Modules when in ```config``` mode
- ```akka.inject.cfg```: specify whether to provide the application Config through the Injector

#### Modes

- ```manual```: Manual discovery mode that only uses Modules added through ```InjectExtBuilder```
- ```config```: Configuration discovery mode that uses modules specified in the ```CfgModuleDiscoveryKey```
- ```spi```: SPI discovery mode uses modules provided by the [Java Service Provider Interface](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) (SPI)

The default discovery mode is ```manual```

#### Examples

The injection is automatic when the lhs is explicitly typed
```scala
val config: Config = inject[Config]

val thing: Thing = inject[Thing]

val named: String = inject[String] annotated "namedString"

val actor: ActorRef = injectActor[MyActor]

```

Optional injection when the lhs is an ```Option```
```scala
val optionalThing: Option[Thing] = inject[Thing]

val optionalActor: Option[ActorRef] = injectActor[MyActor]

val noThing: Option[Unbound] = inject[Unbound] // == None

val noActor: Option[ActorRef] = injectActor[UnboundActor] // == None
```

When lhs is not explicitly typed the ```required``` or ```optional``` method must be called
```scala
val thing = inject[Thing] required

val actor = injectActor[MyActor] required

val optionalThing = inject[Thing] optional

val optionalActor = injectActor[MyActor] optional
```

Parameters can be passed to ctors using ```arguments```
```scala
val thingWithCtorArgs: Thing = inject[Thing] arguments("foo", 999)
```

Shortcuts for binding annotations, like ```@Named```
```scala
val bob: Option[ActorRef] = injectActor[MyActor] named "bob"
```

Injection within an Actor is easy and to ```val```
```scala
class MyActor extends Actor {
    val otherActor: ActorRef = inject[MyOtherActor] // otherActor parent == this
    val configProp: String = inject[String] named "myactor.hostname"
}
```

#### Goals

- Concise intuitive DSL
- Inject values to ```val```
- Do not require Annotations
- Do not require special bindings to inject ```Option```
- Actors injection that respects parent scope (ie. inject as child)
- Hands off Injector management
- Full support of standard Guice patterns

#### Notes

- [SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) is provided by registering ```com.google.inject.Module``` implementations as services
- Use ```lazy``` to break cycles
- The application config is available by default through the Config binding
- Using ```Manifest``` for now as ScalaGuice is still bound to them

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/akka-injects/issues).

## License

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

<https://www.apache.org/licenses/LICENSE-2.0>

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
