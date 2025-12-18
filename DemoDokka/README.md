# Dokka Javadoc Example

This example demonstrates how to produce an output that emulates Javadoc
using Dokka in a simple single-project Gradle build

- [Dokka Javadoc Example](https://github.com/Kotlin/dokka/tree/master/examples/javadoc)

### guide
1. add plugins in project top-level build.gradle
   - [dokka plugin](https://plugins.gradle.org/plugin/org.jetbrains.dokka/2.0.0)
   - [dokka-javadoc plugin](https://plugins.gradle.org/plugin/org.jetbrains.dokka-javadoc/2.1.0)
2. add dokka configuration in app module build.gradle
3. browse dokka guide
   - [dokka guide](https://kotlinlang.org/docs/dokka-migration.html#visibility-settings)
4. edit dokka configuration in app module build.gradle.
   - [example](https://github.com/Kotlin/dokka/tree/2.0.0/examples/gradle-v2/javadoc-example)
5. read dokka source code annotation.
   - [DokkaExtension](https://github.com/Kotlin/dokka/blob/v2.0.0/dokka-runners/dokka-gradle-plugin/src/main/kotlin/DokkaExtension.kt)
   - [DokkaSourceSetSpec](https://github.com/Kotlin/dokka/blob/v2.0.0/dokka-runners/dokka-gradle-plugin/src/main/kotlin/engine/parameters/DokkaSourceSetSpec.kt)
   - [DokkaPluginParametersBaseSpec](https://github.com/Kotlin/dokka/blob/v2.0.0/dokka-runners/dokka-gradle-plugin/src/main/kotlin/engine/plugins/DokkaPluginParametersBaseSpec.kt)
   - [DokkaPackageOptionsSpec](https://github.com/Kotlin/dokka/blob/v2.0.0/dokka-runners/dokka-gradle-plugin/src/main/kotlin/engine/parameters/DokkaPackageOptionsSpec.kt)
   - [DokkaPublication](https://github.com/Kotlin/dokka/blob/v2.0.0/dokka-runners/dokka-gradle-plugin/src/main/kotlin/formats/DokkaPublication.kt)


### Running

Run `:dokkaGenerate` task to generate documentation for this example:

```bash
./gradlew :dokkaGenerateJavadoc
```
