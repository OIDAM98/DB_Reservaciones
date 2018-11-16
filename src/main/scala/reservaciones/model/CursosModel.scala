package reservaciones.model

import doobie.implicits._


case class Curso(clave: String, secc: Int, titulo: String, prof: String)

object CursosModel extends SearchableCourse with InsertableCourse

trait SearchableCourse {

  def getAllCourses() =
    sql"select * from cursos"
      .query[Curso]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync

  def findCurso(toS: Curso) =
    toS match{
      case Curso(clave, secc, titulo, prof) => sql"select * from cursos where clave = $clave and secc = $secc and titulo = $titulo and prof = $prof"
        .query[Curso]
        .option
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findCurosBySecc(secc: Int) =
    sql"select * from cursos where secc = $secc"
      .query[Curso]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync

  def findCursosByTitulo(titulo: String) =
    sql"select * from cursos where titulo = $titulo"
      .query[Curso]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync

  def findCursosByProfesores(prof: String) =
    sql"select * from cursos where prof = $prof"
    .query[Curso]
    .to[List]
    .transact(Connection.xa)
    .unsafeRunSync
}

trait InsertableCourse {

  def insertCurso(toIns: Curso) =
    toIns match {
      case Curso(cKey, cSecc, cTitulo, cProf) => sql"insert into cursos (clave, secc, titulo, prof) values ($cKey, $cSecc, $cTitulo, $cProf)"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
        .transact(Connection.xa)
        .unsafeRunSync
    }

}

trait DeletableCourse {

  def deleteCurso(toDel: Curso) =
    toDel match {
      case Curso(cKey, cSecc, _, _) => sql"delete from cursos where clave = $cKey and secc = $cSecc"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
        .transact(Connection.xa)
        .unsafeRunSync
    }
}