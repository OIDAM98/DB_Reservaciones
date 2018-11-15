package reservaciones.model

import doobie.implicits._


case class Curso(clave: String, secc: Int, titulo: String, prof: String)

object CursosModel extends SearchableCourse with InsertableCourse with DeletableCourse with UpdateableCourse

trait SearchableCourse {

  def getAllCourses() =
    sql"select * from cursos"
      .query[Curso]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync

  def findCurso(toS: Curso) =
    toS match{
      case Curso(cKey, cSecc, _, _) => sql"select * from cursos where clave = $cKey and secc = $cSecc"
        .query[Curso]
        .option
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findSeccionesCurso(toS: Curso) =
    toS match {
      case Curso(cKey, _, _, _) => sql"select * from cursos where clave = $cKey"
        .query[Curso]
        .to[Array]
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findTituloCurso(toS: Curso) =
    toS match {
      case Curso(_, _, cTitulo, _) => sql"select * from cursos where titulo = $cTitulo"
        .query[Curso]
        .to[Array]
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findProfesorCurso(toS: Curso) =
    toS match {
      case Curso(_, _, _, cProf) => sql"select * from cursos where prof = $cProf"
        .query[Curso]
        .to[Array]
        .transact(Connection.xa)
        .unsafeRunSync
    }
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

  def insertCursoKey(cKey: String, cSecc: Int) =
    insertCurso(Curso(cKey, cSecc, "", ""))

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

  def deleteCursosByClave(cKey: String) =
    sql"delete from cursos where clave = $cKey"
      .update
      .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
      .transact(Connection.xa)
      .unsafeRunSync

  def deleteCursosByTitulo(cTitulo: String) =
    sql"delete from cursos where titulo is not NULL and titulo = $cTitulo"
      .update
      .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
      .transact(Connection.xa)
      .unsafeRunSync

  def deleteCursosByProf(cProf: String) =
    sql"delete from cursos where prof is not NULL and prof = $cProf"
      .update
      .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
      .transact(Connection.xa)
      .unsafeRunSync
}

trait UpdateableCourse {

  def updateCurso(toUp: Curso) =
    toUp match {
      case Curso(cKey, cSecc, cTitulo, cProf) => sql"update curso set titulo = $cTitulo, prof = $cProf where clave = $cKey and secc = $cSecc"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def updateTituloCurso(toUp: Curso) =
    toUp match {
      case Curso(cKey, cSecc, cTitulo, _) => sql"update curso set titulo = $cTitulo where clave = $cKey and secc = $cSecc"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def updateProfCurso(toUp: Curso) =
    toUp match {
      case Curso(cKey, cSecc, _, cProf) => sql"update curso set prof = $cProf where clave = $cKey and secc = $cSecc"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo", "prof")
        .transact(Connection.xa)
        .unsafeRunSync
    }
}