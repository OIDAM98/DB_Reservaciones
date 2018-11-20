package reservaciones.model

import java.sql.{Timestamp}
import java.time.{LocalDateTime}

import doobie.implicits._

case class CursoActivo(clave: String, secc: Int, titulo: String){
  require(clave.length <= 10)
  require(titulo.length <= 20)

  override def toString: String = s"$clave $secc $titulo"
}

object CursosActivosModel extends SearchableCurActivo with InsertableCurActivo with DeletableCurActivo

trait SearchableCurActivo {

  def getAllActiveCourses() =
    sql"select * from cursosactivos"
    .query[CursoActivo]

  def getAllCurrentActiveCourses() = {
    val today = Timestamp.valueOf(LocalDateTime.now())
    sql"select c.clave, c.secc, c.titulo from cursosactivos c natural join periodos where $today between fechainicio and fechafin"
      .query[CursoActivo]
  }

  def findCursoActivo(toSearch: CursoActivo) =
    toSearch match {
      case CursoActivo(clave, secc, titulo) => sql"select * from cursosactivos where clave = $clave and secc = $secc and titulo = $titulo"
        .query[CursoActivo]
    }

  def findCursosActivoByClave(clave: String) =
    sql"select * from cursosactivos where clave = $clave"
    .query[CursoActivo]

  def findCursosActivoBySecc(secc: Int) =
    sql"select * from cursosactivos where secc = $secc"
    .query[CursoActivo]


  def findCursosActivoByPeriodo(titulo: String) =
    sql"select * from cursosactivos where titulo = $titulo"
      .query[CursoActivo]

}

trait InsertableCurActivo {

  def insertCursoActivo(toIns: CursoActivo) =
    toIns match {
      case CursoActivo(clave, secc, titulo) => sql"insert into cursosactivos (clave, secc, titulo) values ($clave, $secc, $titulo)"
        .update
    }

}

trait DeletableCurActivo {
  def deleteCursoActivo(toDel: CursoActivo) =
    toDel match {
      case CursoActivo(clave, secc, titulo) => sql"delete from cursosactivos where clave = $clave and secc = $secc and titulo = $titulo"
        .update
    }
}
