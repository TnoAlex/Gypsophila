# Gypsophila

___
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.0-blue.svg?logo=kotlin)](http://kotlinlang.org)![license](https://img.shields.io/github/license/TnoAlex/Gypsophila)![build: Gradle (shields.io)](https://img.shields.io/badge/build-Gradle-blue)![GitHub Workflow Status](https://img.shields.io/github/actions/workflow/status/TnoAlex/Gypsophila/main.yml)

### Gypsophila 编译前端

这是一个由`kotlin`编写的，基于`Lr(1)`的编译器前端，支持从给定的词法与文法分析源代码。其中，词法需以正则式描述，文法为二型文法。

###### 词法文件编写规则

词法文件中需要包含以下几部分`Keywords`,`Identifier` ,`Operator` ,`Literal`, `Qualifier`,`Separator`,`Comment`
每部分规定了源代码中可能出现的几种`Token`，文件以`YAML`格式组织，以下是一个简单的例子

```yaml
Literal:
  Int:
    value: null
    regex: (\-[1-9]|[0-9])[0-9]*
Keywords:
  int:
    value: null
    regex: int
Identifier:
  Identifier:
    value: null
    regex: (_[A-Za-z]|[A-Za-z])([A-Z]|[a-z]|[0-9]|_)*
```

具体的例子在`sample`文件夹中的`sample_lexical.yml`中

###### 文法文件的编写规则

文法文件同样以`YAML`格式组织，每个文法文件分为两部分：文法起始产生式和其他文法产生式。文法起始产生式需要在文法文件头单独声明。

__⚠️文法仅支持以单独产生式为起始产生式的文法，非终结符需以`<>`包裹，符号之间以空格区分，对于`A -> A B|A c`这样带有`或`
的产生式，需要改写为以下格式 __

```yaml
<A>:
  - <A> <B>
  - <A> c
```

以下是一个简单的例子

```yaml
<Program>:
  <Block>
---
<Program>:
  - <Block>

<Block>:
  - <GlobalDeclList> <FuncList>
  - <FuncList>
```

其中，`<Program> -> <Block>`为此文法的起始产生式，更具体的例子在`sample`文件夹的`sample_syntax.yml`中

###### 代码源文件

代码源文件无特殊要求，符合所编写的文法即可，在`sample`文件夹的`sample_source.cc`中有一个符合实例文法的例子

###### 使用方法

支持的命名参数

```bash
Options:
  -i, --input PATH         Source code to be processed
  -tp, --token_path PATH   Lexical file path
  -sl                      Whether to output token file
  -sp, --syntax_path PATH  Syntax file path
  -sa                      Whether to output predictive analytics tables
  -h, --help               Show this message and exit
```

可以按照以下方法使用

```bash
java -jar gypsophila_version.jar -i test.c -lp lexer.yml -sl -sp syntax.yml -sa
```

如果想要可视化查看抽象语法树，可以使用以下命令：

```bash
java -jar gypsophila_version.jar -show xxx.ast
```

__⚠️最低JDK版本需要为 Java 17 __
