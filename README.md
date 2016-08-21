Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)
[![Dependencies](https://app.updateimpact.com/badge/701268856357916672/akka-injects.svg?config=compile)](https://app.updateimpact.com/latest/701268856357916672/akka-injects)

Dependency Injection DSL for Akka.
Powered by [Guice](https://github.com/google/guice) and implemented as an [Akka Extension](http://doc.akka.io/docs/akka/2.4.9/scala/extending-akka.html).

#### Goals

- Inject to ```val```
- Easy optional injects
- Do not require Annotations
- Respect Actor parent context
- Avoid handling Injectors
- Act like Guice
- Concise DSL

#### Installation

By default the Inject Extension will be loaded on first use.

Or

If you want to force load the extension at ```ActorSystem``` creation time you can [Load from Configuration](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html#Loading_from_Configuration).

```HOCON
akka {
  extensions = ["com.rxthings.di.InjectExt"]
}
```

#### Artifacts

To include in your SBT project add a resolver to your sbt build

```resolvers += "jw3 at bintray" at "https://dl.bintray.com/jw3/maven"```

and add the dependency

```libraryDependencies += "com.rxthings" %% "akka-injects" % "0.5"```

#### Imports

All required imports are included with

```import com.rxthings.di._```

#### Configuration Options

- ```akka.inject.mode```: specifies the module discovery strategy ```[manual | config | spi]```
- ```akka.inject.modules```: specifies the FQCN list of Modules when in ```config``` mode
- ```akka.inject.cfg```: specify whether to provide the application Config through the Injector

#### Module Discovery Modes

The default mode is ```manual```

- ```manual```: Manual discovery mode that only uses Modules added through ```InjectExtBuilder```
- ```config```: Configuration discovery mode that uses modules specified in the ```CfgModuleDiscoveryKey```
- ```spi```: SPI discovery mode uses modules provided by the [Java Service Provider Interface](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) (SPI)

#### Example Usage

Injection is implicit when the lhs is explicitly typed
```scala
val config: Config = inject[Config]

val thing: Thing = inject[Thing]

val named: String = inject[String] annotated "namedString"

val actor: ActorRef = injectActor[MyActor]

```

Optional injection is also implicit when the lhs is an ```Option```
```scala
val optionalThing: Option[Thing] = inject[Thing]

val optionalActor: Option[ActorRef] = injectActor[MyActor]

val noThing: Option[Unbound] = inject[Unbound] // == None

val noActor: Option[ActorRef] = injectActor[UnboundActor] // == None
```

When lhs is not explicitly typed, the ```required``` or ```optional``` method must be called
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

Injection within an Actor is easy and final
```scala
class MyActor extends Actor {
    val otherActor: ActorRef = inject[MyOtherActor] // otherActor parent == this
    val configProp: String = inject[String] named "myactor.hostname"
}
```

#### Notes

- [SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) is provided by registering ```com.google.inject.Module``` implementations as services within `META-INF.services`
- Use ```lazy``` to break cycles
- The application config is available by default through the ```Config``` binding
- [ScalaGuice](https://github.com/codingwell/scala-guice) uses ```Manifest``` so we do too

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
