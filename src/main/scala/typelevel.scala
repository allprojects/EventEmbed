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

  /**
   * joins to hlists on two specific indeces.
   */
  def join[T1, T2, L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat]
    (eq : Eq[T1, T2], l1 : L1, l2 : L2, n1 : N1, n2 : N2)
    (implicit join : Join[T1, T2, L1, L2, N1, N2]) : join.Out = join(eq, l1, l2)

  trait Join[T1, T2, L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat] extends DepFn3[Eq[T1, T2], L1, L2] { type Out <: Option[HList] }

  object Join {
    def apply[T1, T2, L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat]
        (implicit join : Join[T1, T2, L1, L2, N1, N2]) : Aux[T1, T2, L1, L2, N1, N2, join.Out] = join

    type Aux[T1, T2, L1 <: HList, L2 <: HList, N1 <: Nat, N2 <: Nat, Out0 <: Option[HList]] =
      Join[T1, T2, L1, L2, N1, N2] { type Out = Out0 }

    implicit def hlistJoin[T1, T2, L1 <: HList, L2 <: HList, L3 <: HList, N1 <: Nat, N2 <: Nat]
      (implicit removeIndex : RemoveIndex.Aux[L2, N2, L3],
                prepend     : Prepend[L1, L3],
                at1         : At.Aux[L1, N1, T1],
                at2         : At.Aux[L2, N2, T2]) =
        new Join[T1, T2, L1, L2, N1, N2] {
            type Out = Option[prepend.Out]
            def apply(eq : Eq[T1, T2], l1 : L1, l2 : L2) =
              if (eq(at1(l1), at2(l2)))
                Some(prepend(l1, removeIndex(l2)))
              else
                None
        }
  }
}

object TupleOps extends HListAux {

  def join[T1, T2, TUP1 <: Product, TUP2 <: Product, N1 <: Nat, N2 <: Nat]
    (eq : (T1, T2) => Boolean, l1 : TUP1, l2 : TUP2, n1 : N1, n2 : N2)
    (implicit join : Join[T1, T2, TUP1, TUP2, N1, N2]) : join.Out = join(eq, l1, l2)

  trait Join[T1, T2, TUP1 <: Product, TUP2 <: Product, N1 <: Nat, N2 <: Nat] extends DepFn3[Eq[T1, T2], TUP1, TUP2]

  object Join {
    def apply[T1, T2, TUP1<: Product, TUP2<: Product, N1 <: Nat, N2 <: Nat]
        (implicit join : Join[T1, T2, TUP1, TUP2, N1, N2]) : Aux[T1, T2, TUP1, TUP2, N1, N2, join.Out] = join

    type Aux[T1, T2, TUP1<: Product, TUP2<: Product, N1 <: Nat, N2 <: Nat, Out0] = Join[T1, T2, TUP1, TUP2, N1, N2] { type Out = Out0 }

    implicit def hlistJoin[T1, T2, TUP1<: Product, TUP2<: Product, L1 <: HList, L2 <: HList, L3 <: HList, N1 <: Nat, N2 <: Nat]
      (implicit gen1   : Generic.Aux[TUP1, L1],
                gen2   : Generic.Aux[TUP2, L2],
                join   : HListOps.Join.Aux[T1, T2, L1, L2, N1, N2, Option[L3]],
                tupler : Tupler[L3]) =
        new Join[T1, T2, TUP1, TUP2, N1, N2] {
            type Out = Option[tupler.Out]
            def apply(eq: Eq[T1, T2], t1: TUP1, t2: TUP2) = join(eq, gen1.to(t1), gen2.to(t2)).map(tupler(_))
        }
  }
}
