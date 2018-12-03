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
    case Salon => up.withUniqueGeneratedKeys[Salon]("idsalon", "capacidad", "tipo").transact(xa).unsafeRunSync
    case Reservacion => up.withUniqueGeneratedKeys[Reservacion]("idsalon", "fechaini", "fechafin", "clave","secc","periodo", "nombre").transact(xa).unsafeRunSync
    case Curso => up.withUniqueGeneratedKeys[Curso]("clave","secc","titulo", "prof").transact(xa).unsafeRunSync
    case CursoActivo => up.withUniqueGeneratedKeys[CursoActivo]("clave", "secc", "periodo").transact(xa).unsafeRunSync
    case Periodo => up.withUniqueGeneratedKeys[Periodo]("titulo", "fechainicio", "fechafin").transact(xa).unsafeRunSync
  }

}
