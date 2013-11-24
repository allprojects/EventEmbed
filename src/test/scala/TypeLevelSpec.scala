
import shapeless._
import nat._
import shapeless.test.illTyped
import shapeless.ops.hlist._


class TypeLevelSpec {

  type cmp1 = Compare {type arg1 = _0; type arg2 = _0}
  type cmp2 = Compare {type arg1 = _2; type arg2 = _1}
  type cmp3 = Compare {type arg1 = _9; type arg2 = _9}
  type cmp4 = Compare {type arg1 = _2; type arg2 = _2}
  type cmps = cmp4 :: cmp1 :: HNil

  class HListOpsSpec {
    import HListOps._

    
    type l4 = ::[Int, ::[Int, ::[String, ::[Int, ::[String, HNil]]]]]
    type l6 = ::[Int, ::[Int, ::[String, HNil]]]
    
    
    type l1 = ::[Int, ::[Int, ::[String, HNil]]]
    type l2 = ::[Int, ::[String, ::[Option[String], HNil]]]
    type res = ::[Int, ::[Int, ::[String, ::[Option[String], HNil]]]]
    
    
    
    type l3 = ::[Int, ::[Int, HNil]]
    
    
    type l7 = ::[Int, ::[Int, ::[Int, ::[Int, HNil]]]]
    type l8 = ::[Int, ::[Int, ::[Int, HNil]]]
    
    type l5 = ::[Int, ::[Int, ::[String, ::[Int, HNil]]]]
    
    type h1 = ::[Int, HNil]
    type h2 = ::[Int, ::[Int , HNil]]
    type h3 = ::[Int, ::[Int , HNil]]
    type c =  ::[cmp1, HNil]
    		
    //implicitly[RemoveIndex.Aux[l6, _1, l2]]
    implicitly[Join.Aux[l3, l3, HNil,l7]]	
    implicitly[RemoveIndex.Aux[h2,cmp1#arg2,h1]]
    implicitly[At.Aux[h1,cmp1#arg1,Int]]
    implicitly[At.Aux[h2,cmp1#arg2,Int]]
    implicitly[Join[h1,h1,HNil]]
    
    val t : Join.Aux[h1,h2, cmp1 :: HNil,h2] = Join.recCase[cmp1,h1,h2,HNil, Int, h1]
     
    implicitly[Join.Aux[h1,h1,HNil ,h3]]
    implicitly[Join.Aux[h1,h2, cmp1 :: HNil,h2]]
    implicitly[Join.Aux[l1,l2, cmp2 :: cmp1 :: HNil,res]]
    

  }
}
