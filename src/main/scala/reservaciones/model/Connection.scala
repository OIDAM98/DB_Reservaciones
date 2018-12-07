package reservaciones.model

import cats.effect.IO
import doobie.util.query.Query0
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import doobie.implicits._
import doobie.util.update.Update0

object Connection {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  def executeQuery[A](query: Query0[A]) = query.option.transact(xa).unsafeRunSync()
  def executeListQuery[A](query: Query0[A]) = query.to[List].transact(xa).unsafeRunSync

  def executeUpdate[A](t: A, up: Update0) = t match {
    case Salon => up.run.transact(xa).unsafeRunSync
    case Reservacion => up.run.transact(xa).unsafeRunSync
    case Curso => up.run.transact(xa).unsafeRunSync
    case CursoActivo => up.run.transact(xa).unsafeRunSync
    case Periodo => up.run.transact(xa).unsafeRunSync
  }

}
