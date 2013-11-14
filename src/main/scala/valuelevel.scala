object ValueLevel {

  def join[T](eq : (T, T) => Boolean, l1 : List[T], l2 : List[T], n1 : Int, n2 : Int) =
    // Wrong types at the joint slots
    if (l1(n1) != l2(n2)) None
    // Compare function returned 'Not Equal'
    else if (!eq(l1(n1), l2(n2))) None
    // Tuples are comparable and have the fitting values in the joined slots
    else Some(l1 ++ removeIndex(l2, n2))

  def removeIndex[T](l : List[T], n : Int) = (l take n) ++ (l drop (n+1))
}
