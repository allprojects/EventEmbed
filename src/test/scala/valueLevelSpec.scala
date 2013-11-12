import org.scalatest._
import ValueLevel._

class ValueLevelSpec extends FlatSpec with Matchers {

  val l1 = List("Int", "Int", "String")
  val l2 = List("Int", "String", "String")
  val l3 = List("String", "String")

  // place holder, the type level functions would have a type of (Int, Int) => Boolean
  val intEq : (String, String) => Boolean = (_, _) => true

  val strEq : (String, String) => Boolean = (_, _) => true

  "A Join of two tuples" should "create a new tuple with combined columns" in {
    join(intEq, l1, l2, 0, 0) should be (Some (l1 ++ l3))
  }
}
