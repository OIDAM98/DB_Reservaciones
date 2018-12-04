package reservaciones.model

import java.sql.{Timestamp}
import java.time.{LocalDateTime}

import doobie.implicits._

case class CursoActivo(clave: String, secc: Int, periodo: String){
  require(clave.length <= 10)
  require(periodo.length <= 20)

  override def toString: String = s"$clave $secc $periodo"
}

object CursosActivosModel extends SearchableCurActivo with InsertableCurActivo with DeletableCurActivo

trait SearchableCurActivo {

  def getAllActiveCourses() =
    sql"select * from cursosactivos"
    .query[CursoActivo]

  def getAllCurrentActiveCourses() = {
    val today = Timestamp.valueOf(LocalDateTime.now())
    sql"select c.clave, c.secc, c.periodo from cursosactivos c, periodos p where c.periodo = p.titulo and ($today between p.fechaini and p.fechafin)"
      .query[CursoActivo]
  }

  def findCursoActivo(toSearch: CursoActivo) =
    toSearch match {
      case CursoActivo(clave, secc, periodo) => sql"select * from cursosactivos where clave = $clave and secc = $secc and periodo = $"
        .query[CursoActivo]
    }

  def findCursosActivoByClave(clave: String) =
    sql"select * from cursosactivos where clave = $clave"
    .query[CursoActivo]

  def findCursosActivoBySecc(secc: Int) =
    sql"select * from cursosactivos where secc = $secc"
    .query[CursoActivo]


  def findCursosActivoByPeriodo(periodo: String) =
    sql"select * from cursosactivos where periodo = $periodo"
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
      case CursoActivo(clave, secc, periodo) => sql"delete from cursosactivos where clave = $clave and secc = $secc and periodo = $periodo"
        .update
    }
}
