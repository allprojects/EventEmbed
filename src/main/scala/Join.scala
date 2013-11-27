object Main extends App {
  //possible one
  val e1 = Event((3, "hallo"));
  val e2 = Event((3, "du", "hallo", Some(3)))
  val e3 = Join(e1, e2)(_._1 == _._1)((x, y) => { (x._1, y._2, y._4) })
  val e4 = Join(e1, e2)(_._2 == _._2)((x, y) => { (x._1, y._2, y._4) })
  println(e3.get)
  println(e4.get)
}

trait Event[T] {
  def get(): Option[T]
}
class DummyEvent[T](value: T) extends Event[T] {

  def get(): Option[T] = {
    Some(value)
  }
}
object Event {
  def apply[T](v: T) = new DummyEvent[T](v)
}

object Join {

  def apply[E1, E2, RES](
    e1: Event[E1],
    e2: Event[E2])(where: (E1, E2) => Boolean)(pi: (E1, E2) => RES): Event[RES] = {
    return new Event[RES] {
      def get() = {
        (e1.get, e2.get) match {
          case (Some(v1), Some(v2)) => {
            if (where(v1, v2))
              Some(pi(v1, v2))
            else
              None
          }
          case _ => None
        }
      }
    }
  }
}



