import HListOps._
import shapeless._
import shapeless.NatMacros._
import org.scalatest._

class TypeLevelSpec extends FunSpec with Matchers {

  val l1 = 1 :: 2 :: "three" :: HNil
  val l11 = 1 :: 2 :: "three" :: HNil
  val l2 = 2 :: "three" :: HNil
  val l3 = 1 :: "three" :: HNil
  val l4 = 1 :: 2 :: HNil;
 val n0 = Nat(0)

  val intEq = (x : Int, y  : Int) => x==y ;

  val strEq = (x : String, y : String) =>  x == y 

  describe("An Element of an HList") {
    it("can be removed") {

      // 'should be' for some strange reason does not work
      assert(removeIndex(l1, Nat(0)) == l2)
      assert(removeIndex(l1, Nat(1)) == l3)
      assert(removeIndex(l1, Nat(2)) == l4)
      assert(removeIndex(l1, Nat(0)).at(Nat(0)) == 2)
    }
  }

  describe("Two HLists") {
    it("can be joined on two different indices") {
      assert(join(intEq, l1, l11, Nat(0), Nat(0)) == Some(l1 ++ l2))
      assert(join(intEq, l1, l1, Nat(0), Nat(1)) == None)
      assert(join(strEq, l1, l1, Nat(2), Nat(2)) == Some(l1 ++ l4))
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
       TupleOps.join(strEq, t1, t1, Nat(2), Nat(2)) should be (Some (t1 ++ t4))
      }
    }
}
