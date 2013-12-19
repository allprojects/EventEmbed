package shapelessJoin

import shapeless._
import shapeless.ops.hlist._
import shapeless.nat._
import shapeless.NatMacros._
import shapelessJoin.HListOps._

import shapeless.ops.nat._

class Compare[N1 <: Nat, N2 <: Nat](v1: ToInt[N1], v2: ToInt[N2]) {
  def getValue1 = v1()
  def getValue2 = v2()
}

class RichNat[N1 <: Nat](n1: N1) {
  def =:=[N2 <: Nat](n2: N2)(implicit v1: ToInt[N1], v2: ToInt[N2]) = {
    new Compare[N1, N2](v1, v2)
  }
}

object Compare {
  implicit def natToRNat[N1 <: Nat](n: N1) = new RichNat[N1](n)
}

object HListOps {

  /**
   * removes an element with a specific index from an HList.
   */
  trait RemoveIndex[L <: HList, N <: Nat] { type Out <: HList }

  object RemoveIndex {
    type Aux[L <: HList, N <: Nat, Out0 <: HList] = RemoveIndex[L, N] { type Out = Out0 }

    implicit def hlistRemoveIndex[L1 <: HList, L2 <: HList, L3 <: HList, N <: Nat](implicit take: Take.Aux[L1, N, L2],
                                                                                   drop: Drop.Aux[L1, Succ[N], L3],
                                                                                   prepend: Prepend[L2, L3]) =
      new RemoveIndex[L1, N] {
        type Out = prepend.Out
      }
  }
  /**
   * joins to hlists on two specific indeces.
   */
  trait Join[N1 <: Nat, N2 <: Nat, L1 <: HList, L2 <: HList]

  object Join {
    type Aux[N1 <: Nat, N2 <: Nat, L1 <: HList, L2 <: HList, Out0 <: HList] = Join[N1, N2, L1, L2] { type Out = Out0 }

    implicit def hlistJoin[T, N1 <: Nat, N2 <: Nat, L1 <: HList, L2 <: HList, L3 <: HList](implicit removeIndex: RemoveIndex.Aux[L2, N2, L3],
                                                                                           prepend: Prepend[L1, L3],
                                                                                           at1: At.Aux[L1, N1, T],
                                                                                           at2: At.Aux[L2, N2, T]) =
      new Join[N1, N2, L1, L2] {
        type Out = prepend.Out
      }
  }
}

object TupleOps {
  trait Join[N1 <: Nat, N2 <: Nat, TUP1 <: Product, TUP2 <: Product] { type Out }

  object Join {
    type Aux[N1 <: Nat, N2 <: Nat, TUP1 <: Product, TUP2 <: Product, Out0] = Join[N1, N2, TUP1, TUP2] { type Out = Out0 }

    implicit def hlistJoin[N1 <: Nat, N2 <: Nat, TUP1 <: Product, TUP2 <: Product, L1 <: HList, L2 <: HList, L3 <: HList](implicit gen1: Generic.Aux[TUP1, L1],
                                                                                                                          gen2: Generic.Aux[TUP2, L2],
                                                                                                                          join: HListOps.Join.Aux[N1, N2, L1, L2, L3],
                                                                                                                          tupler: Tupler[L3]) =
      new Join[N1, N2, TUP1, TUP2] {
        type Out = tupler.Out
      }
  }
}

object BoolASTObs{
  import shapeless.ops.tuple._

  trait BoolAST[T <: Product ,U <: Product] {
    def name(ev1 : String, ev2 : String) : String
    // Would be nice to use &&& but somehow type inference fails. It works however if
    // we use plain And.
    //def &&&(ast : BoolAST[T,U]) : BoolAST[T,U] = And[T,U](this,ast)
  }

  case class And[T <: Product, U <: Product](a1 : BoolAST[T,U], a2 : BoolAST[T,U]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = "(" + a1.name(ev1, ev2) + " AND " + a2.name(ev1, ev2) + ")"
  }

  case class Or[T <: Product, U <: Product](a1 : BoolAST[T,U], a2 : BoolAST[T,U]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = "(" + a1.name(ev1, ev2) + " OR " + a2.name(ev1, ev2) + ")"
  }

  case class Not[T <: Product, U <: Product](a : BoolAST[T,U]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = "NOT (" + a.name(ev1, ev2) + ")"
  }

  implicit class BoolASTOp[T <: Product, U <: Product](ast1 : BoolAST[T,U]){
    def &&&(ast2 : BoolAST[T,U]) : BoolAST[T,U] = And[T,U](ast1,ast2)
    def |||(ast2 : BoolAST[T,U]) : BoolAST[T,U] = Or[T,U](ast1,ast2)
  }

  implicit class MyNat[N <: Nat](n : N){
    def <==[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit leq : LEq[T,U,N,N2]) = leq
    def ===[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit eeq : EEq[T,U,N,N2]) = eeq
    def !==[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit neq : NEq[T,U,N,N2]) = neq
    def >==[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit geq : GEq[T,U,N,N2]) = geq
    def >[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit gt : GTq[T,U,N,N2]) = gt
    def <[T <: Product, U <: Product, N2 <: Nat](n2 : N2)(implicit lt : LTq[T,U,N,N2]) = lt
  }

  class LEq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit v1 : ToInt[N1], v2 : ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = ev1 + ".P" + (v1() + 1) + " <= " + ev2 + ".P" + (v2() + 1)
  }

  object LEq {
    implicit def leq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2 : At[U, N2], v1 : ToInt[N1], v2 : ToInt[N2]) =
      new LEq[T, U, N1, N2] {}
  }

  class EEq[T <: Product ,U <: Product,N1 <: Nat,N2 <: Nat](implicit v1: ToInt[N1], v2: ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1: String, ev2: String) = ev1 + ".P" + (v1() + 1) + " = " + ev2 + ".P" + (v2() + 1)
  }

  object EEq {
    implicit def eeq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2 : At[U, N2], v1 : ToInt[N1], v2 : ToInt[N2]) =
      new EEq[T, U, N1, N2] {}
  }

  class NEq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit v1 : ToInt[N1], v2 : ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1: String, ev2: String) = ev1 + ".P" + (v1() + 1) + " != " + ev2 + ".P" + (v2() + 1)
  }

  object NEq {
    implicit def neq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2: At[U, N2], v1: ToInt[N1], v2: ToInt[N2]) =
      new NEq[T, U, N1, N2] {}
  }

  class GEq[T <: Product ,U <: Product,N1 <: Nat,N2 <: Nat](implicit v1 : ToInt[N1], v2: ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = ev1 + ".P" + (v1() + 1) + " >= " + ev2 + ".P" + (v2() + 1)
  }

  object GEq {
    implicit def geq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2: At[U, N2], v1: ToInt[N1], v2: ToInt[N2]) =
      new GEq[T, U, N1, N2] {}
  }

  class GTq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit v1 : ToInt[N1], v2: ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = ev1 + ".P" + (v1() + 1) + " > " + ev2 + ".P" + (v2() + 1)
  }

  object GTq {
    implicit def gt[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2: At[U, N2], v1: ToInt[N1], v2: ToInt[N2]) =
      new GTq[T, U, N1, N2] {}
  }

  class LTq[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit v1 : ToInt[N1], v2: ToInt[N2]) extends BoolAST[T,U] {
    def name(ev1 : String, ev2 : String) = ev1 + ".P" + (v1() + 1) + " < " + ev2 + ".P" + (v2() + 1)
  }
  object LTq {
    implicit def lt[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1], at2: At[U, N2], v1: ToInt[N1], v2: ToInt[N2]) =
      new LTq[T, U, N1, N2] {}
  }
}