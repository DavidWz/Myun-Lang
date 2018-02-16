# The Myun Programing Language
The Myun programing language is a small toy language that I developed 
simply because I wanted to try and build a compiler. 

# Getting Started

1. Clone this repository
2. Download and install [ANTLR](http://www.antlr.org/) to generate the parser.
3. Build the project (you might need to make the antlr jar known to your IDE)
4. Run [Main](src/myun/Main.java) with the path to your Myun file (`<FileName>.myun`) as the first argument.
   This will generate LLVM IR code in the same path under `<FileName>.ll`.
5. Now you need to compile that LLVM IR code. If you have `llc` and `gcc` installed, 
   you can use my [compile script](src/myun/compile.sh) script with `FileName` as the first argument 
   like this `./compile <FileName>`. This will produce a binary that you can run. 

# Language Description
In the following I will explain the language, its features, and constraints.

Please note that this is a work in progress, so things might and probably will change drastically 
from time to time (no backwards compatibility).

## Syntax

If you want to take a closer look at Myun's grammar, you can find the ANTLR grammar 
file used to generate the parser at [Myun.g4](src/myun/AST/Myun.g4).

### General Remarks

Myun ignores new lines, tabs, and whitespaces. So you can theoretically write all of your code in one
line, although I would not recommend doing that for readability reasons.

There are no curly brackets or semicolons, although blocks must end with an `end` instruction.

Variable names must begin with a lower-case letter and type names must begin with an upper-case letter.
After that, both can contain letters from a-z (both lower- and upper-case), digits, and underscores.

### Compile Units

A Myun program file consists of arbitrary many [function declarations](#Functions) followed by 
exactly one [script](#Scripts).
A script is basically the main method of that compilation unit.

Myun does not support linking or referencing to other files yet, so you need to define all necessary
function in that one file.

### <a name="Functions">Functions</a>

A function declaration consists of a name, parameters, a body block, and finally an `end` instruction.
The type of parameters and the return type of the function must be annotated with `::` followed by a
type name. Function names follow the same restrictions as variable names.

Myun used Multiple Dispatch, so it is possible to overload function definitions if the amount of parameters or the types differ.
The correct function will be called depending on the types of the arguments to the function call.

Currently, function parameters are immutable, so it is not possible to assign a new value to the 
parameter variables.

Every execution path in the function body must contain a `return` instruction followed by an expression that has
the same type as the return type of the function. `return` statements must be placed at the end of a block.

```
foo(x::Int, y::Float, z::Bool)::Int
    // body
    return 4
end

// overloading of foo
foo(x::Int, z::Bool)::Float
    // body
    return 3.3
end
```



### <a name="Scripts">Scripts</a>

The script (main function) of a Myun program consists of the keyword `script` followed by a name, 
a script body, and finally an `end` instruction. Script names follow the same restrictions as variable name.
 
```
script barScript
    // body
end
```

After compilation, the binary will execute the code in the script body and simply return with exit code 0. 

### Statements

A block (function body, script body, etc) consists of a sequence of statements, 
possibly ended by a `return` or `break` instruction.

Statements can either one of the following: 
* [Variable Declarations](#Decls)
* [Variable Assignments](Assignments)
* [Branches](#Branches)
* [Loops](#Loops)

### <a name="Decls">Variable Declarations</a>

Each variable must be declared before it can be used. The declaration must also initialize the 
variable to some value. However, it is not possible to specifiy any types as the compiler will infer
all types by itself. The compiler will enforce type safety, i.e. it will not perform any implicit
casts but instead throw an error when types mismatch.

A variable declaration simply consists of a variable name on the left-hand side, 
followed by the declaration operator `:=`, and an expression on the right-hand side.

```
script varDecls:
    x := 5  // x is of type Int
    y := -(4.0 * 2.1 + (3.2 mod 1.1))  // y is of type Float
    z := x < y       // error: lessThan(Int, Float) not defined
    b := x is 2+3     // b is of type Bool
end
```

### <a name="Assignments">Variable Assignments</a>

Variable assignments are very similar to declarations. They consist of an already declared variable
on the left-hand side, followed by the assignment operator `=`, and an right-hand side expression.
Every variable used in an assignment must be declared in the current scope.
Again, it is not possible to specify any types as the compiler will infer all types by itself.

```
script varAssignments:
    x := 2     // x is of type Int
    x = x + 2  // ok, x has value 4
    x = x + y  // error: y not declared
    z := x     // z of type Int, value 4
end
```

### <a name="Branches">Branches</a>

Branches in Myun consist of an `if` part, followed by a condition that must be of type `Bool`, 
followed by the `then` keyword and finally a block. It is possible to append `elseif` parts
afterwards, and possibly an `else` part at the end. The whole branch then must be closed with an
`end` instruction.

```
function collatzStep(n::Int)::Int
    if n < 0 then
        return 0
    elseif n mod 2 is 0 then
        return n/2
    else
        return 3*n+1
    end
end
```

### <a name="Loops">Loops</a>
Myun currently supports two kinds of loops: [`for` loops](#ForLoops) and 
[`while` loops](#WhileLoops) loops. In the bodies of both types of loops, it is possible to write a
`break` statement which will jump out of the current loop.

#### <a name="ForLoops">For-Loops</a>
A `for` loop in Myun iterates from a start value to an end value by incrementing an integer variable.
The iteration variable used in the for loop will be declared at that point, so it is not possible to
use an already declared variable as an iterator in `for` loops. The start and end expressions must
be of type `Int`. The syntax is a `for` keyword, followed by a variable name, an expression, the `to` keyword,
another expression, a `do` keyword, a block, and finally an `end` instruction.

```
function sumUpTo(n::Int)::Int
    sum := 0
    for i from 1 to n do
        sum = sum + i
    end
    return sum
end
```


#### <a name="WhileLoops">While-Loops</a>

While loops execute the loop body as long as the condition evaluates to true. The expression used as
the condition must be of type `Bool`. The syntax is a `while` keyword, an expreesion, a `do` keyword,
followed by a block and finally an `end` instruction.

```
function numCollatzSteps(n::Int)::Int
    steps := 0
    while val > 1 do
        val = collatzStep(val)
        steps = steps + 1
    end
    return steps
end
```

### Expressions

Expressions in Myun can consist of literals such as `1`, `2.3`, `false` etc, function calls, and 
predefined operators. Note that using operators is just a shorthand for calling the respective function,
e.g. `x + y` is shorthand for `plus(x, y)`.

The predefined operator functions are as follows:
 
* `and::(Bool, Bool)->Bool` (Infix operator: `and`)
* `or::(Bool, Bool)->Bool` (Infix operator: `or`)
* `is::(Bool, Bool)->Bool` (Infix operator: `is`)
* `isLess::(Int,Int)->Bool` (Infix operator: `<`)
* `isLessEq::(Int,Int)->Bool` (Infix operator: `<=`)
* `isGreater::(Int,Int)->Bool` (Infix operator: `>`)
* `isGreaterEq::(Int,Int)->Bool` (Infix operator: `>=`)
* `plus::(Int,Int)->Int` (Infix operator: `+`)
* `minus::(Int,Int)->Int` (Infix operator: `-`)
* `mult::(Int,Int)->Int` (Infix operator: `*`)
* `div::(Int,Int)->Int` (Infix operator: `/`)
* `exp::(Int,Int)->Int` (Infix operator: `^`)
* `mod::(Int,Int)->Int` (Infix operator: `mod`)
* `is::(Float,Float)->Bool` (Infix operator: `is`)
* `isLess::(Float,Float)->Bool` (Infix operator: `<`)
* `isLessEq::(Float,Float)->Bool` (Infix operator: `<=`)
* `isGreater::(Float,Float)->Bool` (Infix operator: `>`)
* `isGreaterEq::(Float,Float)->Bool` (Infix operator: `>=`)
* `plus::(Float,Float)->Float` (Infix operator: `+`)
* `minus::(Float,Float)->Float` (Infix operator: `-`)
* `mult::(Float,Float)->Float` (Infix operator: `*`)
* `div::(Float,Float)->Float` (Infix operator: `/`)
* `exp::(Float,Float)->Float` (Infix operator: `^`)
* `mod::(Float,Float)->Float` (Infix operator: `mod`)
* `not::Bool->Bool` (Prefix operator: `not`)
* `negate::Int->Int` (Prefix operator: `-`)
* `negate::Float->Float` (Prefix operator: `-`)

### Types

Currently, Myun only supports three primitive types:
* `Int` for signed 32 bit integers (e.g. `0`, `5`, `42`)
* `Float` for 32 bit floating point numbers (e.g. `0.3`, `1.0e2`, `5.2E-12`)
* `Bool` for booleans (`true` or `false`)