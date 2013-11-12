import HListOps._
import shapeless._
import shapeless.NatMacros._
import org.scalatest._

class TypeLevelSpec extends FunSpec with Matchers {

  val l1 = 1 :: 2 :: "three" :: HNil
  val l2 = 2 :: "three" :: HNil
  val l3 = 1 :: "three" :: HNil
  val l4 = 1 :: 2 :: HNil;

  val intEq : Eq[Int, Int] = Ordering[Int].eq(_ : Int, _ : Int)
  val strEq : Eq[String, String] = Ordering[String].eq(_ : String, _ : String)

  describe("An Element of an HList") {
    it("can be removed") {

      // 'should be' for some strange reason does not work
      assert(removeIndex(l1, Nat(0)) == l2)
      assert(removeIndex(l1, Nat(1)) == l3)
      assert(removeIndex(l1, Nat(2)) == l4)
    }
  }

  describe("Two HLists") {
    it("can be joined on two different indices") {
      assert(join(intEq, l1, l1, Nat(0), Nat(0)) == Some (l1 ++ l2))
      assert(join(intEq, l1, l1, Nat(0), Nat(1)) == None)
      assert(join(strEq, l1, l1, Nat(2), Nat(2)) == Some (l1 ++ l4))
    }
  }

  val t1 = (1, 2, "three")
  val t2 = (2, "three")
  val t3 = (1, "three")
  val t4 = (1, 2)

  describe("Two Tuples") {
    it("can be joined on two different indices") {
      import shapeless.syntax.std.tuple._
      TupleOps.join(intEq, t1, t1, Nat(0), Nat(0)) should be (Some (t1 ++ t2))
      TupleOps.join(intEq, t1, t1, Nat(0), Nat(1)) should be (None)
      TupleOps.join(strEq, t1, t1, Nat(0), Nat(2)) should be (Some (t1 ++ t4))
    }
  }
}
