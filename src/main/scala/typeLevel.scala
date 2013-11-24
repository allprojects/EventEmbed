import shapeless.{:: => ::}
import shapeless.HList
import shapeless.HList.hlistOps
import shapeless.HNil
import shapeless.Nat
import shapeless.Succ
import shapeless._0
import shapeless.ops.hlist.At
import shapeless.ops.hlist.Drop
import shapeless.ops.hlist.Prepend
import shapeless.ops.hlist.Take

trait Compare {
  type arg1 <: Nat
  type arg2 <: Nat
}
object HListOps {
  trait Join[L1 <: HList, L2 <: HList, CMP <: HList] {
    type Out <: HList;
  }
  object Join {
    type Aux[L1 <: HList, L2 <: HList, CMP <: HList, Out0 <:HList] = Join[L1, L2, CMP] { type Out = Out0 }

    implicit def baseCase[L1 <: HList, L2 <: HList, Out0 <: HList](implicit prepend: Prepend[L1, L2]) = new Join[L1, L2, HNil] {
      type Out = prepend.Out;
    }

    implicit def recCase[HeadCMPS <: Compare,L1 <: HList, L2 <: HList, TailCMPs <: HList, T, OutL <: HList](
    			implicit 	removeIdx	: RemoveIndex.Aux[L2, HeadCMPS#arg2,OutL],
    						at1			: At.Aux[L1, HeadCMPS#arg1, T],
    						at2			: At.Aux[L2, HeadCMPS#arg2, T],
    						join		: Join[L1,OutL,TailCMPs]
    			
    			) : Aux[L1,L2,HeadCMPS :: TailCMPs,join.Out]= {      
      new Join[L1, L2, HeadCMPS :: TailCMPs] {
        type Out = join.Out; 
      }
    }

  }
  
  trait LTEq[A <: Compare, B <: Compare]

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
  
}


