package reservaciones.model

import cats.effect.IO
import doobie.free.connection.ConnectionIO
import doobie.util.query.Query0
import doobie.util.transactor.Transactor

import scala.concurrent.ExecutionContext
import doobie.implicits._


object Connection {
  implicit val cs = IO.contextShift(ExecutionContext.global)
  val xa = Transactor.fromDriverManager[IO](
    "org.postgresql.Driver", "jdbc:postgresql:universitydb", "postgres", "dulioscar"
  )

  def executeQuery[A](t: A, query: Query0[A]) = query.option.transact(xa).unsafeRunSync
  def executeListQuery[A](t: A, query: Query0[A]) = query.to[List].transact(xa).unsafeRunSync

  def executeUpdate[A](up: ConnectionIO[A]) = up.transact(xa).unsafeRunSync

}
