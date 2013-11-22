package tuplejoin

import shapeless._
import nat._
import shapeless.test.illTyped

class TypeLevelSpec {

  type cmp1 = Compare {type arg1 = _0; type arg2 = _0}
  type cmp2 = Compare {type arg1 = _0; type arg2 = _2}
  type cmp3 = Compare {type arg1 = _9; type arg2 = _9}

  class HListOpsSpec {
    import tuplejoin.HListOps._

    type l1 = ::[Int, ::[Int, ::[String, HNil]]]
    type l2 = ::[Int, ::[String, HNil]]
    type l3 = ::[Int, ::[Int, HNil]]
    type l4 = ::[Int, ::[Int, ::[String, ::[Int, ::[String, HNil]]]]]
    type l5 = ::[Int, ::[Int, ::[String, ::[Int, HNil]]]]

    implicitly[RemoveIndex.Aux[l1, _0, l2]]
    implicitly[RemoveIndex.Aux[l1, _1, l2]]
    implicitly[RemoveIndex.Aux[l1, _2, l3]]
    illTyped("implicitly[RemoveIndex[l1, _20]]")
    illTyped("implicitly[RemoveIndex.Aux[l1, _2, l1]]")

    implicitly[Join.Aux[cmp1, l1, l1, l4]]
    implicitly[Join.Aux[cmp1, l1, l3, l5]]
    illTyped("implicitly[Join[cmp2, l1, l1]]")
    illTyped("implicitly[Join.Aux[cmp1, l2, l2, l3]]")
    illTyped("implicitly[Join[cmp3, l1, l1]]")
  }

  object TupleOpsSpec {
    import tuplejoin.TupleOps._

    type t1 = (Int, Int, String)
    type t2 = (Int, String)
    type t3 = (Int, Int)
    type t4 = (Int, Int, String, Int, String)
    type t5 = (Int, Int, String, Int)

    implicitly[Join.Aux[cmp1, t1, t1, t4]]
    implicitly[Join.Aux[cmp1, t1, t3, t5]]
    illTyped("implicitly[Join[cmp2, t1, t1]]")
    illTyped("implicitly[Join.Aux[cmp1, t2, t2, t3]]")
    illTyped("implicitly[Join[cmp3, t1, t1]]")
  }
}
