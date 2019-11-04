# Spring REST Docs DSL

[![Build Status](https://travis-ci.com/jntakpe/spring-restdocs-dsl.svg?branch=master)](https://travis-ci.com/jntakpe/spring-restdocs-dsl)
![License](https://img.shields.io/badge/license-Apache%202-blue.svg)

Provides a convenient way to document and test APIs with [Spring REST Docs](https://spring.io/projects/spring-restdocs) leveraging Kotlin DSL.

Our primary goal is to : 
* Document APIs using [Spring REST Docs](https://spring.io/projects/spring-restdocs)
* Preserve coherent order between JSON and documentation
* Make API documentation code more readable
* Enable view filtering

This library comes with 3 levels of maturity ([AutoDsl](#AutoDsl), [Reflection](#Reflection) and [Standard](#Standard-API)),
each one alleviating the boilerplate you need to write to document your API.

## Index

* [Installation](#Installation)
* [Configuration](#Configuration)
  * [Maven](#Maven)
  * [Gradle](#Gradle)
* [Usage](#Usage)
  * [AutoDsl](#AutoDsl)
  * [Reflection API](#Reflection)
  * [Standard API](#Standard-API)
  * [WebTestClient usage](#WebTestClient-usage)
  * [Compared to vanilla RestDocs](#Standard-Spring-REST-Docs-usage)

## Installation

Spring REST Docs DSL depends on Kotlin standard library and Spring REST Docs.

The current release is [0.6.2](https://github.com/jntakpe/spring-restdocs-dsl/releases/tag/v0.6.2).

## Configuration

#### Maven

```xml
<dependency>
  <groupId>com.github.jntakpe</groupId>
  <artifactId>spring-restdocs-dsl</artifactId>
  <version>0.6.2</version>
  <scope>test</scope>
</dependency>
```

#### Gradle

```groovy
testImplementation 'com.github.jntakpe:spring-restdocs-dsl:0.6.2'
```

If you want to use autoDsl feature you must also add

```groovy
compileOnly 'com.github.jntakpe:spring-restdocs-dsl-annotations:0.6.2'
compileOnly 'com.github.jntakpe:spring-restdocs-dsl-core:0.6.2'
testImplementation 'com.github.jntakpe:spring-restdocs-dsl-core:0.6.2'
kapt 'com.github.jntakpe:spring-restdocs-dsl-processor:0.6.2'
```

## Usage

### Sample

Given the following JSON document : 

```json
{
  "module" : "",
  "questions" : [ {
    "label" : "",
    "configuration" : {
      "duration" : "",
      "code" : false,
      "multipleChoice" : false
    },
    "answerOptions" : [{
      "label" : "",
      "valid" : false,
      "id" : ""
    }],
    "answers" : [ ],
    "valid" : false,
    "id" : ""
  }],
  "configuration" : {
    "shuffled" : false,
    "duration" : ""
  },
  "id" : ""
}
```

## AutoDsl

AutoDsl generates some helper functions from your Kotlin classes.

#### Configuration

You can configure AutoDsl globally thanks to `@EnableRestDocsAutoDsl` annotation. It has the following options :

|  Name | Kind | Description | Default |
|:-------:|:------:|:------:|:--------:|
|  packages  | Array<String> | Packages containing classes to generate DSL from. As an alternative you can indivually mark such classes with @Doc annotation |    empty    |
|  trimSuffixes | Array<String> | Trims suffixes of generated DSL function e.g. PetDto is generated as `pet {}` instead of ~~`petDto {}`~~ |  empty   |

**Note:** Kapt is triggered before Kotlin compilation. If you use Intellij, kapt is not currently supported. To overcome this it is recommended
to use Gradle to build your project. To do so choose ‘Gradle’ in ‘Settings > Build, Execution, Deployment > Build Tools > Gradle > Build.

#### Usage

Kapt generates DSL functions you can then use like this :

```kotlin
val initDoc =  {
    durationType = String::class
    answerOption {
        label = "Option's label"
        valid = "Indicates if the option is valid"
        id = "Option's unique identifier"
    }
    questionConfiguration {
        duration = "Question's maximum duration"
        code = "Indicates if the label should be formatted as code"
        multipleChoice = "Indicates if question accepts multiple answers"
    }
    question {
        label = "Question's label"
        configuration = "Object containing question's configuration"
        answerOptions = "Array containing the different possible answer options for the question"
        answers = "Array containing the answer given by an user"
        valid = "Field indicating if the given answer is valid"
        id = "Question's unique identifier"
    }
    quizConfiguration {
        duration = "Duration of the quiz. Equivalent of the total duration of all questions"
        shuffled = "Indicates if the questions should be shuffled"
    }
    quiz {
        module = "Module related to the quiz"
        questions = "Array containing quiz questions"
        configuration = "Object containing quiz configuration"
        id = "Quiz unique identifier"
    }
}
```

With AutoDsl you just have to type field's description. The rest is inferred thanks to reflection.  

**Note about `initDoc`** : if you use IntelliJ and chose to run your tests using JUnit, you need to call `initDoc()` method
in either a `@BeforeAll` or a `@BeforeEach` function ; otherwise it won't get evaluated. Using Gradle works thine.

**Note about external classes :** in this example we use `java.time.Duration` which we don't own.
AutoDsl identifies those classes alongside those not picked up by `EnableRestDocsAutoDsl.packages` as external classes.  
It then leaves you with 2 options regarding those classes :

* Document their fields like others. In this case syntax differs a bit and uses [reflection](#Reflection) syntax.
For `java.time.Duration` a durationDoc field is generated which we would initialize like :
```kotlin
durationDoc = root {
    field(Duration::nano, "Nanoseconds")
    field(Duration::seconds, "seconds")
    field(Duration::units, "Unit")
}
```
* Else you might have defined a custom way to serialize this type. In this case, for `java.time.Duration` as an example
you can simply use the durationType field and pass it a Kotlin type matching the Json type once serialized : 
```kotlin
durationType = String::class
// or if you serialize it with nanos
durationType = Long::class
```

**Note :** in your tests you can import auto-generated FieldDescriptors e.g. given a Quiz class, you can import a quizDoc top-level property.

## Reflection

Reflection API brings some syntactic sugar compared to [standard usage](#Standard-API). Especially it alleviates :

* Path is inferred from given KProperty e.g. instead of ~~`QuizDTO::module.name`~~ you can just pass `QuizDTO::module` 
* Type is also inferred. You can just use the `field()` method instead of `string(), boolean(), json(), array()...`
* View and optionality are inferred

**Note :** in order to use it you must also add [kotlin-reflect](https://mvnrepository.com/artifact/org.jetbrains.kotlin/kotlin-reflect) to your test classpath.

This enables us to write this :

```kotlin
val answerOptionDoc by obj {
    field(AnswerOptionDTO::label, "Option's label")
    field(AnswerOptionDTO::valid, "Indicates if the option is valid")
    field(AnswerOptionDTO::id, "Option's unique identifier")
}
val questionConfigurationDoc by obj {
    field<String>(QuestionConfigurationDTO::duration, "Question's maximum duration")
    field(QuestionConfigurationDTO::code, "Indicates if the label should be formatted as code")
    field(QuestionConfigurationDTO::multipleChoice, "Indicates if question accepts multiple answers")
}
val questionDoc by obj {
    field(QuestionDTO::label, "Question's label")
    field(QuestionDTO::configuration, questionConfigurationDoc, "Object containing question's configuration")
    field(QuestionDTO::answerOptions, answerOptionDoc, "Array containing the different possible answer options for the question")
    field(QuestionDTO::answers, "Array containing the answer given by an user")
    field(QuestionDTO::valid, "Field indicating if the given answer is valid")
    field(QuestionDTO::id, "Question's unique identifier")
}
val quizConfigurationDoc by obj {
    field<String>(QuizConfigurationDTO::duration, "Duration of the quiz. Equivalent of the total duration of all questions")
    field(QuizConfigurationDTO::shuffled, "Indicates if the questions should be shuffled")
}
val quizDoc by obj {
    field(QuizDTO::module, "Module related to the quiz")
    field(QuizDTO::questions, questionDoc, "Array containing quiz questions")
    field(QuizDTO::configuration, quizConfigurationDoc, "Object containing quiz configuration")
    field(QuizDTO::id, "Quiz unique identifier")
}
```

If you need to document an array of something (e.g. QuizDTO) you can use : 

```kotlin
// reusing previously defined quizDoc
val quizzesDoc by arr<QuizDTO>(quizDoc) // Description will be inferred from reified type
// or if you want to explicitly define the description
val explicitQuizzesDoc by arr<QuizDTO>(quizDoc, "An array of quizzes")
```

If you need to enforce JSON type of a field e.g. `java.time.Duration` you can used reified `field()` method like :
```kotlin
field<String>(QuestionConfigurationDTO::duration, "Question's maximum duration")
```

## Standard API

Using the standard Kotlin DSL, we write : 

```kotlin
private fun quizResponse() = responseFields(quizDesc())
    
private fun quizDesc() = root {
    string(QuizDTO::module.name, "Module related to the quiz")
    array(QuizDTO::questions.name, "Array containing quiz questions") {
        fields += questionDesc()
    }
    json(QuizDTO::configuration.name, "Object containing quiz configuration") {
        string(QuizConfigurationDTO::duration.name, "Duration of the quiz. Equivalent of the total duration of all questions")
        boolean(QuizConfigurationDTO::shuffled.name, "Indicates if the questions should be shuffled")
    }
    string(QuizDTO::id.name, "Quiz unique identifier")
}

private fun questionDesc() = root {
    string(QuestionDTO::label.name, "Question's label")
    json(QuestionDTO::configuration.name, "Object containing question's configuration") {
        string(QuestionConfigurationDTO::duration.name, "Question's maximum duration")
        boolean(QuestionConfigurationDTO::code.name, "Indicates if the label should be formatted as code")
        boolean(QuestionConfigurationDTO::multipleChoice.name, "Indicates if question accepts multiple answers")
    }
    array(QuestionDTO::answerOptions.name, "Array containing the different possible answer options for the question") {
        string(AnswerOptionDTO::label.name, "Option's label")
        boolean(AnswerOptionDTO::valid.name, "Indicates if the option is valid")
        string(AnswerOptionDTO::id.name, "Option's unique identifier")
    }
    array(QuestionDTO::answers.name, "Array containing the answer given by an user")
    boolean(QuestionDTO::valid.name, "Field indicating if the given answer is valid")
    string(QuestionDTO::id.name, "Question's unique identifier")
}
```

It feels natural and close to JSON syntax !

### WebTestClient usage

In order to use those FieldDescriptors in our tests, some helpers are also provided :
```kotlin
// given our quizDoc previously written
quizDoc.asList<QuizDTO>() // An array of quizzes
quizDoc.asReq() // In request payload
quizDoc.asResp() // In response payload
quizDoc.asList<QuizDTO>().asResp() // Array of quizzes in response payload
quizDoc.asList<QuizDTO>("A list of quizzes").asReq() // Array of quizzes with explicit description in request payload
```

### Standard Spring REST Docs usage

Using standard Spring REST Docs, we write :

```kotlin
 private fun quizResponse() = responseFields(quizDesc())

private fun quizDesc() = mutableListOf(
        fieldWithPath(QuizDTO::id.name).type(STRING).description("Quiz unique identifier"),
        fieldWithPath(QuizDTO::questions.name).type(ARRAY).description("Array containing the quiz questions")
)
        .apply { addAll(questionDesc("${QuizDTO::questions.name}[].")) }
        .apply {
            addAll(listOf(
                    fieldWithPath(QuizDTO::module.name).type(STRING).description("Module related to the quiz"),
                    fieldWithPath(QuizDTO::configuration.name).type(OBJECT).description("Object containing quiz configuration"),
                    fieldWithPath("${QuizDTO::configuration.name}.${QuizConfigurationDTO::duration.name}").type(STRING).description("Duration of the quiz. Equivalent of the total duration of all questions"),
                    fieldWithPath("${QuizDTO::configuration.name}.${QuizConfigurationDTO::shuffled.name}").type(BOOLEAN).description("Indicates if the questions should be shuffled")
            ))
        }

fun questionDesc(prefix: String) = listOf(
        fieldWithPath("$prefix${QuestionDTO::label.name}").type(STRING).description("Question's label"),
        fieldWithPath("$prefix${QuestionDTO::configuration.name}").type(OBJECT).description("Object containing question's configuration"),
        fieldWithPath("$prefix${QuestionDTO::configuration.name}.${QuestionConfigurationDTO::duration.name}").type(STRING).description("Question's maximum duration"),
        fieldWithPath("$prefix${QuestionDTO::configuration.name}.${QuestionConfigurationDTO::code.name}").type(BOOLEAN).description("Indicates if the label should be formatted as code"),
        fieldWithPath("$prefix${QuestionDTO::configuration.name}.${QuestionConfigurationDTO::multipleChoice.name}").type(BOOLEAN).description("Indicates if question accepts multiple answers"),
        fieldWithPath("$prefix${QuestionDTO::answerOptions.name}").type(ARRAY).description("Array containing the different possible answer options for the question"),
        fieldWithPath("$prefix${QuestionDTO::answerOptions.name}[].${AnswerOptionDTO::label.name}").type(STRING).description("Option's label"),
        fieldWithPath("$prefix${QuestionDTO::answerOptions.name}[].${AnswerOptionDTO::valid.name}").type(BOOLEAN).description("Indicates if the option is valid"),
        fieldWithPath("$prefix${QuestionDTO::answerOptions.name}[].${AnswerOptionDTO::id.name}").type(STRING).description("Option's unique identifier"),
        fieldWithPath("$prefix${QuestionDTO::answers.name}").type(ARRAY).description("Array containing the given answer options identifiers"),
        fieldWithPath("$prefix${QuestionDTO::valid.name}").type(BOOLEAN).description("Field indicating if the given answer is valid"),
        fieldWithPath("$prefix${QuestionDTO::id.name}").type(STRING).description("Question's unique identifier")
)
```

The previous code has few majors flaws : 
* It's cumbersome to write
* The fields ordering is hard to maintain
* The field prefix has to be explicit

## For contributors

#### Debugging kapt

* In order to trigger kapt you need to execute `./gradlew kaptKotlin`
* To enable debugging add `kapt.use.worker.api=true` and `org.gradle.caching=false` to your `gradle.properties` file
