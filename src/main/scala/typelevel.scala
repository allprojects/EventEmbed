package tuplejoin

import shapeless._
import shapeless.ops.hlist._
import shapeless.nat._
import shapeless.NatMacros._

trait HListAux {
  trait DepFn3[T1, T2, T3] {
    type Out
    def apply(t1 : T1, t2 : T2, t3 : T3) : Out
  }

  type Eq[T1, T2] = (T1, T2) => Boolean
}

object HListOps extends HListAux {

  /**
   * removes an element with a specific index from an HList.
   */
  def removeIndex[L <: HList, N <: Nat](l : L, n : N)
    (implicit removeIndex : RemoveIndex[L, n.N]) : removeIndex.Out = removeIndex(l)

  trait RemoveIndex[L <: HList, N <: Nat] extends DepFn1[L] { type Out <: HList }

  object RemoveIndex {

    def apply[L <: HList, N <: Nat](implicit remove : RemoveIndex[L, N]) : Aux[L, N, remove.Out] = remove

    type Aux[L <: HList, N <: Nat, Out0 <: HList] = RemoveIndex[L, N] { type Out = Out0 }

    implicit def hlistRemoveIndex[L1 <: HList, L2 <: HList, L3 <: HList, N <: Nat]
      (implicit take    : Take.Aux[L1, N,       L2],
                drop    : Drop.Aux[L1, Succ[N], L3],
                prepend : Prepend[L2, L3]) =
      new RemoveIndex[L1, N] {
        type Out = prepend.Out
        def apply(l : L1) : Out = prepend(take(l), drop(l))
      }
  }

  trait Compare {
    type arg1 <: Nat
    type arg2 <: Nat
  }
  /**
   * joins to hlists on two specific indeces.
   */
  trait Join[C <: Compare, L1 <: HList, L2 <: HList]

  object Join {
    type Aux[CMP <: Compare, L1 <: HList, L2 <: HList, Out0 <: HList] =
      Join[CMP, L1, L2] { type Out = Out0 }

    implicit def hlistJoin[T, CMP <: Compare, N1  <: Nat, N2 <: Nat, L1 <: HList, L2 <: HList, L3 <: HList]
      (implicit removeIndex : RemoveIndex.Aux[L2, N2, L3],
                prepend     : Prepend[L1, L3],
                at1         : At.Aux[L1, CMP#arg1, T],
                at2         : At.Aux[L2, CMP#arg2, T]) =
        new Join[CMP, L1, L2] {
            type Out = prepend.Out
        }
  }
}

//object TupleOps extends HListAux {
//  trait Join[T1, T2, TUP1 <: Product, TUP2 <: Product, N1 <: Nat, N2 <: Nat]
//
//  object Join {
//
//    type Aux[T1, T2, TUP1<: Product, TUP2<: Product, N1 <: Nat, N2 <: Nat, Out0] = Join[T1, T2, TUP1, TUP2, N1, N2] { type Out = Out0 }
//
//    implicit def hlistJoin[T1, T2, TUP1<: Product, TUP2<: Product, L1 <: HList, L2 <: HList, L3 <: HList, N1 <: Nat, N2 <: Nat]
//      (implicit gen1   : Generic.Aux[TUP1, L1],
//                gen2   : Generic.Aux[TUP2, L2],
//                join   : HListOps.Join.Aux[T1, T2, L1, L2, N1, N2, L3],
//                tupler : Tupler[L3]) =
//        new Join[T1, T2, TUP1, TUP2, N1, N2] {
//            type Out = tupler.Out
//        }
//  }
//}
