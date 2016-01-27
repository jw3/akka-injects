Akka Injects
==========================
[![Build Status](https://travis-ci.org/jw3/akka-injects.svg?branch=master)](https://travis-ci.org/jw3/akka-injects)

Dependency Injection DSL for Akka, using Google Guice

Injector management implemented as an [Akka Extension](http://doc.akka.io/docs/akka/2.4.1/scala/extending-akka.html).

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
  extensions = ["com.rxthings.di.InjectExt"]
}
```

#### Imports
```import com.rxthings.di._```

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

The implicit injection builders require the lhs to be explicitly typed
```scala
val cfg: Config = inject[Config]

val thing: Thing = inject[Thing]

val named: String = inject[String] annotated "namedString"

val actor: ActorRef = injectActor[MyActorTaggingIface]

```

Optional injection is enabled automatically when the lhs is an ```Option```
```scala
val optionalThing: Option[Thing] = inject[Thing]

val optionalActor: Option[ActorRef] = injectActor[MyActorTaggingIface]
```

If not explicitly typed the ```required``` or ```optional``` method must be called
```scala
val thing = inject[Thing] required

val actor = injectActor[MyActorTaggingIface] required

val optionalActor = injectActor[MyActorTaggingIface] optional
```

Parameters can be passed to ctors using ```arguments```
```scala
val thingWithCtorArgs: Thing = inject[Thing] arguments("foo", 999)
```

Shortcuts for binding annotations, like ```@Named```
```scala
val bob: Option[ActorRef] = injectActor[MyActorTaggingIface] named "bob"
```

#### Notes:

- [SPI](https://docs.oracle.com/javase/tutorial/ext/basics/spi.html) is provided by registering ```Module``` implementations as services
- Use ```lazy``` to break cycles
- Actor injection always creates new instances
- The application config is available by default through the Config binding
- Using ```Manifest``` for now as ScalaGuice is still bound to them

## Bugs and Feedback

For bugs, questions and discussions please use the [Github Issues](https://github.com/jw3/akka-injects/issues).

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
