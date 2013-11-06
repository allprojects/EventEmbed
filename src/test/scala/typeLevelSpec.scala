import HListOps._
import shapeless._
import shapeless.NatMacros._
import org.scalatest._

class TypeLevelSpec extends FunSpec with Matchers {

  val l1 = 1 :: 2 :: "three" :: HNil
  val l2 = 2 :: "three" :: HNil
  val l3 = 1 :: "three" :: HNil
  val l4 = 1 :: 2 :: HNil

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
      assert(join(l1, l1, Nat(0), Nat(0)) == l1 ++ l2)
      assert(join(l1, l1, Nat(0), Nat(1)) == l1 ++ l3)
      assert(join(l1, l1, Nat(0), Nat(2)) == l1 ++ l4)
    }
  }

  val t1 = (1, 2, "three")
  val t2 = (2, "three")
  val t3 = (1, "three")
  val t4 = (1, 2)

  describe("Two Tuples") {
    it("can be joined on two different indices") {
      import shapeless.syntax.std.tuple._
      TupleOps.join(t1, t1, Nat(0), Nat(0)) should be (t1 ++ t2)
      TupleOps.join(t1, t1, Nat(0), Nat(1)) should be (t1 ++ t3)
      TupleOps.join(t1, t1, Nat(0), Nat(2)) should be (t1 ++ t4)
    }
  }
}
