package reservaciones.model

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._

case class Periodo(titulo: String, fechaini: Timestamp, fechafin: Timestamp){
  require(titulo.length <= 20)
  require(fechaini before fechafin)
  override def toString: String = s"$titulo, $fechaini, $fechafin"
}

object PeriodosModel extends SearchablePeriod

trait SearchablePeriod {

  def getAllPeriods() =
    sql"select * from periodos"
      .query[Periodo]

  def getCurrentPeriod() ={
    val today = Timestamp.valueOf(LocalDateTime.now())
    sql"select * from periodos where $today between fechaini and fechafin"
      .query[Periodo]
  }

  def findPeriodo(toSearch: Periodo) =
    toSearch match {
      case Periodo(titulo, ini, fin) => sql"select * from periodos where titulo = $titulo and fechaini = $ini and fechafin = $fin"
        .query[Periodo]
    }

  def findPeriodosByTitulo(titulo: String) =
    sql"select * from periodos where titulo = $titulo"
      .query[Periodo]

  def findPeriodosByIni(ini: Timestamp) =
    sql"select * from periodos where fechaini = $ini"
      .query[Periodo]

  def findPeriodosByFin(fin: Timestamp) =
    sql"select * from periodos where fechafin = $fin"
      .query[Periodo]
}
