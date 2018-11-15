package reservaciones.model

import doobie.implicits._


case class Curso(clave: String, secc: Int, titulo: String, prof: String)


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
trait CursoModel {

  def string

}
