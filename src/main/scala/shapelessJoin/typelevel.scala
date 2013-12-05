package shapelessJoin

import shapeless._
import shapeless.ops.hlist._
import shapeless.nat._
import shapeless.NatMacros._
import shapelessJoin.HListOps._

import shapeless.ops.nat._

class Compare[N1 <: Nat, N2 <: Nat] (v1 : ToInt[N1], v2 : ToInt[N1]) {
  def getValue1 = v1
  def getValue2 = v2
}

class RichNat[N1 <: Nat](n1 : N1) {
  def ===[N2 <: Nat](n2 : N2)(implicit v1 : ToInt[N1], v2 : ToInt[N1]) = {
     new Compare[N1,N2](v1, v2)
  }
}

object Compare{
    implicit def natToRNat[N1<: Nat](n : N1) = new RichNat[N1](n)
}

object HListOps {

  /**
   * removes an element with a specific index from an HList.
   */
  trait RemoveIndex[L <: HList, N <: Nat] { type Out <: HList }

  object RemoveIndex {
    type Aux[L <: HList, N <: Nat, Out0 <: HList] = RemoveIndex[L, N] { type Out = Out0 }

    implicit def hlistRemoveIndex[L1 <: HList, L2 <: HList, L3 <: HList, N <: Nat]
      (implicit take    : Take.Aux[L1, N,       L2],
                drop    : Drop.Aux[L1, Succ[N], L3],
                prepend : Prepend[L2, L3]) =
      new RemoveIndex[L1, N] {
        type Out = prepend.Out
      }
  }
  /**
   * joins to hlists on two specific indeces.
   */
  trait Join[N1<: Nat, N2 <: Nat, L1 <: HList, L2 <: HList]

  object Join {
    type Aux[N1<: Nat, N2 <: Nat, L1 <: HList, L2 <: HList, Out0 <: HList] =
      Join[N1, N2, L1, L2] { type Out = Out0 }

    implicit def hlistJoin[T, N1<: Nat, N2 <: Nat, L1 <: HList, L2 <: HList, L3 <: HList]
      (implicit removeIndex : RemoveIndex.Aux[L2, N2, L3],
                prepend     : Prepend[L1, L3],
                at1         : At.Aux[L1, N1, T],
                at2         : At.Aux[L2, N2, T]) =
        new Join[N1, N2, L1, L2] {
            type Out = prepend.Out
        }
  }
}

object TupleOps {
  trait Join[N1<: Nat, N2 <: Nat, TUP1 <: Product, TUP2 <: Product] { type Out }

  object Join {
    type Aux[N1<: Nat, N2 <: Nat, TUP1<: Product, TUP2<: Product, Out0] =
      Join[N1,N2, TUP1, TUP2] { type Out = Out0 }

    implicit def hlistJoin[N1<: Nat, N2 <: Nat, TUP1<: Product, TUP2<: Product, L1 <: HList, L2 <: HList, L3 <: HList]
      (implicit gen1   : Generic.Aux[TUP1, L1],
                gen2   : Generic.Aux[TUP2, L2],
                join   : HListOps.Join.Aux[N1,N2, L1, L2, L3],
                tupler : Tupler[L3]) =
        new Join[N1,N2, TUP1, TUP2] {
            type Out = tupler.Out
        }
  }
}
