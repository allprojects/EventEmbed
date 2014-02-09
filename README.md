EventEmbed Shapeless Join
=========================

This modified CEScala library allows for type safe joins of event streams.

## Motivation
It is common in Scala to write strongly typed programs that allow to identify a lot of errors
during compilation instead of runtime. In this library we provide a strongly typed interface
for event joins. Although CEScala has compile time support for bounds checking and allows only
values of equal types to be compared, it does not produce redundance-free result events for
joins.

Consider the following simple join (simplified code):

```
event1 = Event[(Int, Int)]
event2 = Event[(Int, String)]
event3 = event1 join event2 on (event1._0 == event2._0)
```

In CEScala the type of `event3` is `Event[(Int, Int, Int, String)]` (the event of the concatenated
tuples). As the join selected only those events in which the first parts of the tuples are equal,
the first and third part of the result event are the same. Thus the resulting event in CEScala
contains redundant information. Our join library will take advantage of the join semantics and
produce and event of type `Event[(Int, Int, String)]`, i.e. it will drop the redundant third part.
Redundance-free result events help to improve performance and to create more meaningful event types.

However, sometimes it is not clear which (if any) information are redundant (e.g. comparison of numbers with
<). For this case the library provides a join that is separated in a selection function (that does
the comparison) and a projection function (that removes "redundant" information).

All in all, this library provides a type-safe and on demand fine-grained  interface to specify
redundance-free result events.

## Dependencies
This library depends on `esper` and `shapeless`. Running the tests requires `scalatest`. A detailed
list of the dependencies can be found in the [sbt build file.](build.sbt).

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
`_0` closely resembles `0`, `_2` resembles `2` and so on.


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
    val e3 = e1.window (time(30 sec)) join e2.window(time(30 sec)) on (_0 =:= _0)
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

Also note that projections always have to return a Tuple, even if this turns out to be a 1-tuple.

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
