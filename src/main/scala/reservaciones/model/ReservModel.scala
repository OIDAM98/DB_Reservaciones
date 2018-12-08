package reservaciones.model

import java.sql.Timestamp
import java.time.{DayOfWeek, LocalDateTime}

import doobie.implicits._


case class Reservacion(salon: String, fechaini: Timestamp, fechafin: Timestamp, clave: Option[String], secc: Option[Int], periodo: Option[String], nombre: String){
  val c = clave.getOrElse("-")
  val s = secc.getOrElse("-")
  val p = periodo.getOrElse("-")
  override  def toString = s"$salon, ${fechaini.toString}, ${fechafin.toString}, $c, $s, $p, $nombre"
}

object ReservacionesModel extends SearchableReserv with TimetableSalones with TimetableCursos with DeleteableReserv with ModifiableTimetable

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

  def insertReservSpecificDay(toRes: Salon, day: Int, ini: Tuple2[Int, Int], end: Tuple2[Int, Int], cur: Option[CursoActivo], name: String) = {

    val periodo = Connection.executeQuery(PeriodosModel.getCurrentPeriod())

    val periodStart = periodo match {
      case Some(Periodo(titulo, fechaini, fechafin)) => fechaini.toLocalDateTime.getDayOfWeek.getValue
    }
    val inter1 = day - periodStart

    val dateStart =
      if (inter1 > 0) periodo.get.fechaini.toLocalDateTime.toLocalDate.plusDays(inter1).atTime(ini._1, ini._2)
      else if (inter1 < 0) periodo.get.fechaini.toLocalDateTime.toLocalDate.plusDays(7 + inter1).atTime(ini._1, ini._2)
      else periodo.get.fechaini.toLocalDateTime.toLocalDate.atTime(ini._1, ini._2)


    val start = Timestamp.valueOf(dateStart)

    val periodEnd = periodo match {
      case Some(Periodo(titulo, fechaini, fechafin)) => fechafin.toLocalDateTime.toLocalDate.getDayOfWeek.getValue
    }
    val inter2 = periodEnd - day
    val dateEnd =
       if (inter2 > 0) periodo.get.fechafin.toLocalDateTime.toLocalDate.minusDays(inter2).atTime(ini._1, ini._2)
      else if (inter2 < 0) periodo.get.fechafin.toLocalDateTime.toLocalDate.minusDays(7 + inter2).atTime(ini._1, ini._2)
      else periodo.get.fechafin.toLocalDateTime.toLocalDate.atTime(ini._1, ini._2)

    val fin = Timestamp.valueOf(dateEnd)


    val range = (0L to (fin.toLocalDateTime.toLocalDate.toEpochDay - start.toLocalDateTime.toLocalDate.toEpochDay) by 7)
    println(range)
    val days = range.map(d => Timestamp.valueOf(start.toLocalDateTime.plusDays(d)))
    println(days)

    days.foreach(day => {
      val after = Timestamp.valueOf(day.toLocalDateTime.plusHours(end._1).plusMinutes(end._2))
      toRes match {
        case Salon(idsalon, capacidad, tipo) => cur match {
          case Some(CursoActivo(clave, secc, periodo)) => {
            val ins = sql"insert into reservaciones (idsalon, fechaini, fechafin, clave, secc, periodo, nombre) values ($idsalon, $day, $after, $clave, $secc, $periodo, $name)".update
            Connection.executeUpdate(Reservacion, ins)
          }
          case None => {
            val ins = sql"insert into reservaciones (idsalon, fechaini, fechafin, nombre) values ($idsalon, $day, $after, $name)".update
            Connection.executeUpdate(Reservacion, ins)
          }
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
    sql"select * from reservaciones where fechaini >= $dia and $dia <= fechafin"
      .query[Reservacion]

  def getTimetablePeriodo(ini: Timestamp, fin: Timestamp) =
    sql"select * from reservaciones where (fechaini between $ini and $fin) and (fechafin between $ini and $fin)"
    .query[Reservacion]

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

  def deleteHorarioSalon(toDel: Salon) =
    toDel match {
      case Salon(idsalon, capacidad, tipo) => sql"delete from reservaciones where idsalon = $idsalon"
        .update
    }

  def deleteFromPeriodo(ini: Timestamp, fin: Timestamp) =
    sql"delete from reservaciones where (fechaini between $ini and $fin) and (fechafin between $ini and $fin)".update

  def deleteFromDia(day: Timestamp) =
    sql"delete from reservaciones where $day between fechaini and fechafin".update

}

trait ModifiableTimetable {

  def modifyTimetable(res: Reservacion, nRes: Reservacion) = {
    val id = res.salon
    val ini = res.fechaini
    val fin = res.fechafin
    nRes match {
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => {
        sql"""update reservaciones
             |clave= $clave,
             |secc= $secc,
             |titulo= $periodo,
             |nombre= $nombre
             |where idsalon = $id and
             |fechaini = $ini and
             |fechafin = $fin
             |"""
          .update
      }
    }
  }

}