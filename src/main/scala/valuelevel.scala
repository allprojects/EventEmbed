object ValueLevel {

  def join[T](l1 : List[T], l2 : List[T], n1 : Int, n2 : Int) =
    l1 ++ removeIndex(l2, n2)

  def removeIndex[T](l : List[T], n : Int) = (l take n) ++ (l drop (n+1))
}
