package reservaciones.model

import java.sql.Timestamp
import java.time.{DayOfWeek, LocalDateTime}

import doobie.implicits._


case class Reservacion(salon: String, fechaini: Timestamp, fechafin: Timestamp, clave: Option[String], secc: Option[Int], periodo: Option[String], nombre: String)

object ReservacionesModel extends SearchableReserv with TimetableSalones with TimetableCursos with DeleteableReserv with CheckInput

trait SearchableReserv{

  def getAllReservaciones() =
    sql"select * from reservaciones"
      .query[Reservacion]

  def getAllActiveReservaciones() = {
    val today = Timestamp.valueOf(LocalDateTime.now())
    sql"select * from reservaciones where fechafin >= $today and fechafin >= $today"
      .query[Reservacion]
  }

}

trait TimetableSalones{

  def insertReserv(toRes: Salon, day: Timestamp, h: Int, min: Int, name: String) = {
    val after = Timestamp.valueOf(day.toLocalDateTime.plusHours(h).plusMinutes(min))
    toRes match {
      case Salon(idsalon, capacidad, tipo) => sql"insert into reservaciones (idsalon, fechaini, fechafin, nombre) values ($idsalon, $day, $after, $name)".update
    }
  }

  def insertReservSpecificDay(toRes: Salon, day: Int, h: Int, min: Int, cur: Option[CursoActivo], name: String) = {
    val periodo = Connection.executeQuery(PeriodosModel.getCurrentPeriod())
    val ini = periodo match {
      case Some(Periodo(titulo, fechaini, fechafin)) => fechaini.toLocalDateTime.getDayOfWeek.getValue
    }
    val inter1 = Math.abs(day - ini)
    val start = Timestamp.valueOf(periodo.get.fechaini.toLocalDateTime.plusDays(inter1))
    val ini2 = periodo match {
      case Some(Periodo(titulo, fechaini, fechafin)) => fechafin.toLocalDateTime.getDayOfWeek.getValue
    }
    val inter2 = Math.abs(ini2 - day)
    val fin = Timestamp.valueOf(periodo.get.fechaini.toLocalDateTime.minusDays(inter2))

    val range = (0L to (fin.toLocalDateTime.toLocalDate.toEpochDay - start.toLocalDateTime.toLocalDate.toEpochDay) by 7)
    val days = range.map(d => Timestamp.valueOf(start.toLocalDateTime.plusDays(d)))

    days.foreach(day => {
      val after = Timestamp.valueOf(day.toLocalDateTime.plusHours(h).plusMinutes(min))
      toRes match {
        case Salon(idsalon, capacidad, tipo) => cur match {
          case Some(CursoActivo(clave, secc, periodo)) => sql"insert into reservaciones (idsalon, fechaini, fechafin, clave, secc, periodo, nombre) values ($idsalon, $day, $after, $clave, $secc, $periodo, $name)".update
          case None => sql"insert into reservaciones (idsalon, fechaini, fechafin, nombre) values ($idsalon, $day, $after, $name)".update
        }
      }
    })

  }

  def getTimetableSalon(id: String) =
    sql"select * from reservaciones where idsalon = $id"
      .query[Reservacion]

  def getFreeSalones(day: Timestamp): List[Salon] = {
    val r = sql"select idsalon from reservaciones where idsalon not in (select idsalon from reservaciones where $day::date >= fechaini::date and $day <= fechafin::date) group by idsalon having sum(fechafin::time - fechaini::time) < time '14:00:00'"
      .query[String]
    val salones = Connection.executeListQuery(r)
    salones flatMap(s => Connection.executeQuery(SalonesModel.findSalon(s)))
  }

  def getFreeSalonesInterval(ini: Timestamp, fin: Timestamp) = {
    val r = sql"select idsalon from reservaciones where idsalon not in (select idsalon from reservaciones where $ini >= fechaini and $fin <= fechafin)"
      .query[String]
    val salones = Connection.executeListQuery(r)
    salones flatMap(s => Connection.executeQuery(SalonesModel.findSalon(s)))
  }

  def getTimetableDia(dia: Timestamp) =
    sql"select * from reservaciones where fechaini >= $dia and $dia <= fechafin".query[Reservacion]

}

trait TimetableCursos {

  def getTimetableCurso(toSearch: CursoActivo) =
    toSearch match {
      case CursoActivo(clave, secc, titulo) => sql"select * from reservaciones where clave = $clave and secc = $secc and periodo = $titulo"
        .query[Reservacion]
    }

  def getTimetableAllCursosActivos() =
    sql"select * from reservaciones where clave is not null and secc is not null and periodo is not null"
      .query[Reservacion]

  def insertReservCurso(toRes: Salon, day: Timestamp, h: Int, min: Int, cur: CursoActivo, name: String) = {
    val after = Timestamp.valueOf(day.toLocalDateTime.plusHours(h).plusMinutes(min))
    toRes match {
      case Salon(idsalon, capacidad, tipo) => cur match {
        case CursoActivo(clave, secc, periodo) => sql"insert into reservaciones (idsalon, fechaini, fechafin, clave, secc, periodo, nombre) values ($idsalon, $day, $after, $clave, $secc, $periodo, $name)".update
      }
    }
  }

}

trait DeleteableReserv {

  def deleteHorarioCurso(toDel: CursoActivo) =
    toDel match {
      case CursoActivo(clave, secc, periodo) => sql"delete from reservaciones where clave = $clave and secc = $secc and periodo = $periodo"
        .update
    }

  def deleteFromDia(ini: Timestamp, fin: Timestamp) =
    sql"delete from reservaciones where (fechaini between $ini and $fin) and (fechafin between $ini and $fin)".update

}

trait CheckInput {

  def isValidDateWithoutCourse(check: Timestamp): Boolean = {
    val today = Timestamp.valueOf(LocalDateTime.now())
    check after today
  }

  def isValidDateWithCourse(cDay: Timestamp, cCurso: CursoActivo) = {
    val periodoCurso = Connection.executeQuery(PeriodosModel.findPeriodosByTitulo(cCurso.periodo))

    periodoCurso match {
      case Some(cur) => (cDay after cur.fechaini) && (cDay before cur.fechafin)
      case None => false
    }
  }
}

trait ModifiableTimetable {

  def modifyTimetable(res: Reservacion) = {

  }

  def modifyTimetable() =
    sql""

}