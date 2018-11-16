package reservaciones.model

import doobie.implicits._

case class CursoActivo(clave: String, secc: Int, titulo: String)

object CursosActivosModel extends SearchableCurActivo with InsertableCurActivo

trait SearchableCurActivo {

  def getAllActiveCourses =
    sql"select * from cursosactivos"
    .query[CursoActivo]
    .to[Array]
    .transact(Connection.xa)
    .unsafeRunSync

  def findCursoActivo(toSearch: CursoActivo) =
    toSearch match {
      case CursoActivo(clave, secc, titulo) => sql"select from cursosactivos where clave = $clave and secc = $secc and titulo = $titulo"
        .query[CursoActivo]
        .option
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findCursosActivoByClave(clave: String) =
    sql"select * from cursosactivos where clave = $clave"
    .query[CursoActivo]
    .to[Array]
    .transact(Connection.xa)
    .unsafeRunSync

  def findCursosActivoBySecc(secc: Int) =
    sql"select * from cursosactivos where secc = $secc"
    .query[CursoActivo]
    .to[Array]
    .transact(Connection.xa)
    .unsafeRunSync


  def findCursosActivoByPeriod(titulo: String) =
    sql"select * from cursosactivos where titulo = $titulo"
      .query[CursoActivo]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync

}

trait InsertableCurActivo {

  def insertCursoActivo(toIns: CursoActivo) =
    toIns match {
      case CursoActivo(clave, secc, titulo) => sql"insert into cursosactivos ($clave, $secc, $titulo)"
        .update
        .withUniqueGeneratedKeys("clave", "secc", "titulo")
        .transact(Connection.xa)
        .unsafeRunSync
    }

}
