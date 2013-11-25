
import shapeless._
import nat._
import shapeless.test.illTyped
import shapeless.ops.hlist._
import org.scalatest.Matchers
import org.scalatest.FunSpec



class TypeLevelSpec extends FunSpec with Matchers{



  
    import HListOps._

    type cmp1 = Compare[_0, _0]
    type cmp2 = Compare[_2,_1]
    type cmp3 = Compare[_2,_2]
    type cmps = cmp2 :: cmp1 :: HNil
    
    
    
    type cs = Compare[_3,_7] :: Compare[_2,_2] :: Compare[_0,_0] :: HNil
    type c2=Compare[_2,_2] ::  Compare[_3,_7]:: Compare[_0,_0] :: HNil
    type t1 = ::[Int, ::[Boolean, ::[String, ::[Option[Int], ::[String, HNil]]]]]
    type t2 =  ::[Int, ::[Int, ::[String, ::[Option[String], ::[Float, ::[Double, ::[Object, ::[Option[Int], HNil]]]]]]]]
    type t2Removed = ::[Int, ::[Option[String], ::[Float, ::[Double, ::[Object,  HNil]]]]]
    type t3 = ::[Int, ::[Boolean, ::[String, ::[Option[Int], ::[String, t2Removed]]]]]
    
    implicitly[Join.Aux[t1,t2, cs,t3]]
    //implicitly[Join.Aux[t1,t2, c2,t3]]
    
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
    implicitly[RemoveIndex.Aux[h2,_0,h1]]
    implicitly[At.Aux[h1,_0,Int]]
    implicitly[At.Aux[h2,_0,Int]]
    implicitly[Join[h1,h1,HNil]]
    
    
    //val t : Join.Aux[h1,h2, cmp1 :: HNil,h2] = Join.recCase[_0,_0,h1,h2,HNil, Int, h1]
     
    implicitly[Join.Aux[h1,h1,HNil ,h3]]
    implicitly[Join.Aux[h1,h2, cmp1 :: HNil,h2]]
    implicitly[Join.Aux[l1,l2, cmp2 :: cmp1 :: HNil,res]]
    

    implicitly[LTEqCom[cmp1, cmp2]]
    implicitly[ComIsSorted[cmp3 :: cmp2 :: cmp1 :: HNil]]
    //implicitly[ComIsSorted[cmp1 :: cmp2 :: cmp3 :: cmp1 :: HNil]]
    //implicitly[ComIsSorted[cmp2 :: cmp1 :: cmp2 :: HNil]]
    illTyped("implicitly[LTEqCom[cmp2,cmp1]]") 
  
}
