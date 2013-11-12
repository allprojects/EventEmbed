object ValueLevel {

  sealed trait Ordering
  case object LT extends Ordering
  case object EQ extends Ordering
  case object GT extends Ordering

  def join[T](cmp : (T, T) => Ordering, l1 : List[T], l2 : List[T], n1 : Int, n2 : Int) =
    // Wrong types at the joint slots
    if (l1(n1) != l2(n2)) None
    // Compare function returned 'Not Equal'
    else if (cmp(l1(n1), l2(n2)) != EQ) None
    // Tuples are comparable and have the fitting values in the joined slots
    else l1 ++ removeIndex(l2, n2)

  def removeIndex[T](l : List[T], n : Int) = (l take n) ++ (l drop (n+1))
}
