import org.scalatest._
import ValueLevel._

class ValueLevelSpec extends FlatSpec with Matchers {

  val l1 = List("Int", "Int", "String")
  val l2 = List("Int", "String", "String")
  val l3 = List("String", "String")

  "A Join of two tuples" should "create a new tuple with combined columns" in {
    join(l1, l2, 0, 0) should be (l1 ++ l3)
  }
}
