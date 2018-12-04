package reservaciones.model

import doobie.implicits._


case class Curso(clave: String, secc: Int, titulo: String, prof: String) {
  require(clave.length <= 10)
  require(titulo.length <= 30)
  require(prof.length <= 30)

  override def toString: String = s"$clave $secc $titulo $prof"
}

object CursosModel extends SearchableCourse


trait SearchableCourse {

  def getAllCourses() =
    sql"select * from cursos"
      .query[Curso]

  def findCurso(toS: Curso) =
    toS match{
      case Curso(clave, secc, titulo, prof) => sql"select * from cursos where clave = $clave and secc = $secc and titulo = $titulo and prof = $prof"
        .query[Curso]
    }

  def findCurso(toS: String) =
    sql"select * from cursos where clave = $toS"
    .query[Curso]

  def findCursosBySecc(secc: Int) =
    sql"select * from cursos where secc = $secc"
      .query[Curso]

  def findCursosByTitulo(titulo: String) =
    sql"select * from cursos where titulo = $titulo"
      .query[Curso]

  def findCursosByProfesor(prof: String) =
    sql"select * from cursos where prof = $prof"
    .query[Curso]
}