# Spring REST Docs DSL

[![Build Status](https://travis-ci.com/jntakpe/spring-restdocs-dsl.svg?branch=master)](https://travis-ci.com/jntakpe/spring-restdocs-dsl)
![License](https://img.shields.io/badge/license-Apache%202-blue.svg)

Provides a convenient way to document and test APIs with [Spring REST Docs](https://spring.io/projects/spring-restdocs) leveraging Kotlin DSL.

Our primary goal is to : 
* Document APIs using [Spring REST Docs](https://spring.io/projects/spring-restdocs)
* Preserve coherent order between JSON and documentation
* Make API documentation code more readable

## Installation

Spring REST Docs DSL depends on Kotlin standard library and Spring REST Docs.

The current release is [3.0.0](https://github.com/jntakpe/spring-restdocs-dsl/releases/tag/v3.0.0).

#### Maven configuration

```xml
<dependency>
  <groupId>com.github.jntakpe</groupId>
  <artifactId>spring-restdocs-dsl</artifactId>
  <version>3.0.0</version>
  <scope>test</scope>
</dependency>
```

#### Gradle configuration

```groovy
testCompile 'com.github.jntakpe:spring-restdocs-dsl:3.0.0'
```

## Usage

### Use case

To document the following JSON : 

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

### Kotlin DSL usage

Using the Kotlin DSL, we write : 

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
