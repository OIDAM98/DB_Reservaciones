package reservaciones.model

import java.sql.Date

import doobie.implicits._


case class Reservacion(salon: String, fechaini: Date, fechafin: Date, clave: String, secc: Int, titulo: String, nombre: String)

object ResevacionesModel

trait SearchableReserv{

  def getAllReservaciones(): List[Reservacion] =
    sql"select * from reservaciones"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync

  def getAllActiveReservaciones(): List[Reservacion] = {
    val today = new Date( new java.util.Date().getTime )
    sql"select * from reservaciones where fechafin >= $today"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync
  }

}

trait TimetableSalones{

  def getTimetableSalon(id: String): List[Reservacion] = {
    sql"select * from reservaciones where idsalon = $id"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync
  }

  def getFreeSalones(day: Date): List[Reservacion] =
    sql"select * from reservaciones where idsalon in (select idsalon from reservaciones where $day is between fechaini and fechafin group by idsalon having sum(fechafin::time - fechaini::time) < 14)"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync


  def getFreeSalonesInterval(ini: Date, fin: Date) = {
    sql"select * from reservaciones where ($ini not between fechaini and fechafin) and ($fin not between fechaini and fechafin)"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync
  }
}

trait DeleteableReserv {

  def deleteHorarioCurso(toDel: CursoActivo) =
    toDel match {
      case CursoActivo(clave, secc, titulo) => sql"delete from reservaciones where clave = $clave and secc = $secc and titulo = $titulo"
        .update
        .withUniqueGeneratedKeys("clave","secc","titulo")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def deleteHorarioCursos(del: List[CursoActivo]) = del foreach deleteHorarioCurso


}

trait CheckInput {

  def isValidDateWithoutCourse(check: Date): Boolean = {
    val today = new Date( new java.util.Date().getTime )
    check after today
  }

  def isValidDateWithCourse(cDay: Date, cCurso: CursoActivo) = {
    val periodoCurso = cCurso match {
      case CursoActivo(clave, secc, titulo) => sql"select * from periodos where titulo = $titulo"
        .query[Periodo]
        .option
        .transact(Connection.xa)
        .unsafeRunSync()
    }

    periodoCurso match {
      case Some(cur) => (cDay after cur.fechaini) && (cDay before cur.fechafin)
      case None => None
    }
  }
}

trait TimetableCursos {

  def getTimetableCurso(toSearch: CursoActivo): List[Reservacion] =
    toSearch match {
      case CursoActivo(clave, secc, titulo) => sql"select * from reservaciones where clave = $clave and secc = $secc and titulo = $titulo"
        .query[Reservacion]
        .to[List]
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def getTimetableAllCursosActivos(): List[Reservacion] =
    sql"select * from reservaciones where clave is not null and secc is not null and titulo is not null"
      .query[Reservacion]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync

}
