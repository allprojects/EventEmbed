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


### Simple joins
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

Only the equality operator (`=:=`) is supported at this time.

### Typesafety
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

### Advanced joins with projection functions
Simple, automatic joins are however often not enough, especially when
joining two large tuples while only needing a small subset of the resulting
tuple.

For this reason, it is possible to use a slightly different syntax to join the
event streams and at the same time specify which elements to keep.
Given the above example, if one were only ever interested in the resulting string,
it could be rewritten as follows:
```scala
var testString = ""
val e1 = new ImperativeEvent[(Int)]
val e2 = new ImperativeEvent[(String, Int)]
val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) where (_0 === _1, (x, y) => { Tuple1(y._1) })
val r1 = (e: Tuple1[String]) => testString += e._2
e3 += r1
e1(1)
e1(3)
e2("Hello ",      1)
e2("just ",       2)
e2("Projection!"  3)
e2("words ",      4)
// testString => "Hello Projection!"
```

Also note, that projections always have to return a Tuple, even if this turns out to be a 1-tuple.

The available compare functions are the following:
```
=== : equals
!== : not equals
<== : less than or equal
>== : greater than or equal
>   : greater than
<   : less than
```

Konjunction, negations or disjunktions are also available.
For this purpose, the `And`, `Or`, and `Not` case Classes are availbale. Example:

```scala
var testString = ""
val e1 = new ImperativeEvent[(Int, String)]
val e2 = new ImperativeEvent[(Int, String)]
val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) where (And(_0 === _0, _1 === _1), (x, y) => { (y._1, y._2) })
val r1 = (e: (Int, String)) => testString += e._2
e3 += r1
e1(1, "Hello ")
e1(2, "...")
e1(4, "World!")
e1(5, "...")
e2(1, "Hello ")
e2(2, "blah")
e2(4, "World!")
e2(5, "doh!")
// testString => "Hello World!"
```