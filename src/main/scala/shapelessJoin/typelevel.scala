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
  def ===[N2 <: Nat](n2: N2)(implicit v1: ToInt[N1], v2: ToInt[N2]) = {
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

//object BE {
//  import shapeless.ops.tuple._
//  trait BoolAST[T <: Product, U <: Product]
//  class BoolExprLeaf[T <: Product, U <: Product, N1 <: Nat, N2 <: Nat](op: String, at1: At[T, N1],
//                                                                       at2: At[U, N2]) extends BoolAST[T, U]
//  class BoolExprNode[T <: Product, U <: Product](astL: BoolAST[T, U], astR: BoolAST[T, U], op: String) extends BoolAST[T, U]
//

//  object BoolAST {
//    
//    
//    def and[T <: Product, U <: Product](ast1: BoolAST[T, U], ast2: BoolAST[T, U]) = {
//      new BoolExprNode(ast1, ast2, "and")
//    }
//    def or[T <: Product, U <: Product](ast1: BoolAST[T, U], ast2: BoolAST[T, U]) = {
//      new BoolExprNode(ast1, ast2, "or")
//    }
//    
//      
//    
//    def <=[N1 <: Nat, N2 <: Nat, T <: Product, U <: Product](n1: N1, n2: N2)(implicit tup : (T,U), at1: At[T, N1], at2: At[U, N2]) = new BoolExprLeaf("<=", at1, at2)
//    def ===[N1 <: Nat, N2 <: Nat, T <: Product, U <: Product](n1: N1, n2: N2)(implicit tup : (T,U), at1: At[T, N1], at2: At[U, N2]) = new BoolExprLeaf("=", at1, at2)
//    
//    def create[T <: Product, U <: Product]( t : T, u : U)(f : ((T,U)) => BoolAST[T,U]){
//      f((t,u))
//    }
//  }
//
//}
//object Test extends App {
//  import BE._
//  import BE.BoolAST._
//  
//  create((3, "Hallo"), (3, Some(3), "Du"))( x  => {
//    implicit val contex = x
//    and(and(<=(Nat(1), Nat(2)), <=(Nat(1), Nat(2))), <=(_1, _2))
//  })
//}
//
  
object BoolASTObs{
  trait BoolAST[T <: Product ,U <: Product] {
    //def &&&(ast : BoolAST[T,U]) : BoolAST[T,U] = And[T,U](this,ast)
  }
  //case class LEq[T <: Product ,U <: Product,N1 <: Nat,N2 <: Nat](n1 : N1,  n2 : N2) extends BoolAST[T,U]
  case class And[T <: Product ,U <: Product](a1 : BoolAST[T,U], a2 : BoolAST[T,U]) extends BoolAST[T,U]
 
  //implicit def natToMyNat[N <: Nat](n : N) = new MyNat(n)
  implicit class MyNat[N <: Nat](n : N){
    import LEq._
    def <==[T <: Product ,U <: Product,N2 <:Nat](n2 : N2)(implicit  leq : LEq[T,U,N,N2]) = leq
  }
  
  
  
  class BoolASTOp[T <: Product, U <: Product](ast1 : BoolAST[T,U]){   
     def &&&(ast2 : BoolAST[T,U]) : BoolAST[T,U] = And[T,U](ast1,ast2)
  }
  implicit def boolASTtoBoolASTOp[T <: Product, U <: Product](ast1 : BoolAST[T,U]) = new BoolASTOp[T,U](ast1)
  
  
  
  
  trait LEq[T <: Product ,U <: Product,N1 <: Nat,N2 <: Nat] extends BoolAST[T,U]  

  object LEq {
    import shapeless.ops.tuple._
   // type Aux[T<: Product,  U <: Product, N1 <: Nat, N2 <: Nat] = LEq[T,U,N1,N2]
    implicit def leq[T<: Product, U <: Product, N1 <: Nat, N2 <: Nat](implicit at1 : At[T, N1],
                                                            at2: At[U, N2]) =
      new LEq[T,U,N1,N2] {        
      }
  }
  
implicitly[LEq[(Int,Int), (Int,Int), _0,_0]]
 // val e : BoolAST[(Int,Int),(Int,Int)] =( (_0 <== _0) &&& (_0 <== _0))
 join((1,2,3,4),(1,2,3,4))(  And(_0 <== _0,  And(_0 <== _0, _0 <== _0) )  )
  
  
  object join{
    def apply[T <: Product ,U <: Product](t : T , u : U)(ast : BoolAST[T,U]){
      
    }
  }
}  