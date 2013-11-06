import shapeless._
import shapeless.ops.hlist._
import shapeless.nat._
import shapeless.NatMacros._

object HListOps {

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

  /**
   * joins to hlists on two specific indeces.
   * 
   * Currently the parameter n1 is unused and the first list is left
   * unmodified. This should change in the future. If the two joined
   * columns have the wrong type, then this should result in an
   * compile error.
   */
  def join[L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat]
    (l1 : L1, l2 : L2, n1 : N1, n2 : N2)
    (implicit join : Join[L1, L2, N1, N2]) : join.Out = join(l1, l2)

  trait Join[L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat] extends DepFn2[L1, L2] { type Out <: HList }

  object Join {
    def apply[L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat]
        (implicit join : Join[L1, L2, N1, N2]) : Aux[L1, L2, N1, N2, join.Out] = join

    type Aux[L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat, Out0 <: HList] =
      Join[L1, L2, N1, N2] { type Out = Out0 }

    implicit def hlistJoin[L1 <: HList, L2 <: HList, L3 <: HList, N1 <: Nat, N2 <: Nat]
      (implicit removeIndex : RemoveIndex.Aux[L2, N2, L3],
                prepend     : Prepend[L1, L3]) =
        new Join[L1, L2, N1, N2] {
            type Out = prepend.Out
            def apply(l1 : L1, l2 : L2) = prepend(l1, removeIndex(l2))
        }
  }
}

object TupleOps {

  def join[T1, T2, N1 <: Nat, N2 <: Nat]
    (t1 : T1, t2 : T2, n1 : N1, n2 : N2)
    (implicit join : Join[T1, T2, N1, N2]) : join.Out = join(t1, t2)

  trait Join[T1, T2, N1 <: Nat, N2 <: Nat] extends DepFn2[T1, T2]

  object Join {
    def apply[T1, T2, N1 <: Nat, N2 <: Nat]
        (implicit join : Join[T1, T2, N1, N2]) : Aux[T1, T2, N1, N2, join.Out] = join

    type Aux[T1, T2, N1 <: Nat, N2 <: Nat, Out0] = Join[T1, T2, N1, N2] { type Out = Out0 }

    implicit def hlistJoin[T1, T2, L1 <: HList, L2 <: HList, L3 <: HList, N1 <: Nat, N2 <: Nat]
      (implicit gen1   : Generic.Aux[T1, L1],
                gen2   : Generic.Aux[T2, L2],
                join   : HListOps.Join.Aux[L1, L2, N1, N2, L3],
                tupler : Tupler[L3]) =
        new Join[T1, T2, N1, N2] {
            type Out = tupler.Out
            def apply(t1 : T1, t2 : T2) = tupler(join(gen1.to(t1), gen2.to(t2)))
        }
  }
}
