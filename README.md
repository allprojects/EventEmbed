EventEmbed Shapeless Join
=========================

This modified cescala library allows for type safe joins of event streams.

## Dependencies
New Dependencies of this library are Shapeless.

## Usage
The following dependencies are needed in code that wants to use theses modified join operations:

```scala
import shapeless.nat._
import shapelessJoin._
import shapelessJoin.Compare._
import shapelessJoin.BoolASTObs._
```

Throughout this library, Shapeless Nats are used for indexing HLists.
The first couple of integers have a direct representation, for example
_0 closely resembles 0, _2 resembles 2 and so on.


### Simple Joins
Most simple joins can be written very concisely with the `=:=` operator (for equality).
For example, to join two event streams on the 1st entry of the first stream and the
2nd entry of the second stream:
```scala
var testString = ""
val e1 = new ImperativeEvent[(Int)]
val e2 = new ImperativeEvent[(String, Int)]
val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) on (_0 =:= _1)
val r1 = (e: (Int, String)) => testString += e._2
e3 += r1
e1(1)
e1(3)
e2("Hello ", 1)
e2("just ",  2)
e2("World!"  3)
e2("words ", 4)
// testString => "Hello World!"
```

Were one to write instead
```scala
val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) on (_0 =:= _0)
```
the compiler would see that _0 of the first event stream is a Int,
but on the second stream is a String and respond with the following error:
```
Either shapeless._0 or shapeless.nat._0 are out of bounds or have incompatible types.
    val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) on (_0 =:= _1)
                                                                   ^
```

There