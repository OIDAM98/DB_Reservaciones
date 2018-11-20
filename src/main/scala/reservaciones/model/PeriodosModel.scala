package reservaciones.model

import java.sql.Timestamp
import java.time.LocalDateTime

import doobie.implicits._

case class Periodo(titulo: String, fechaini: Timestamp, fechafin: Timestamp){
  require(titulo.length <= 20)
  override def toString: String = s"$titulo $fechaini $fechafin"
}

object PeriodosModel extends SearchablePeriod with InsertablePeriod with DeletablePeriod

trait SearchablePeriod {

  def getAllPeriods() =
    sql"select * from periodos"
      .query[Periodo]

  def getCurrentPeriod() ={
    val today = Timestamp.valueOf(LocalDateTime.now())
    sql"select * from periodos where $today between fechainicio and fechafin"
      .query[Periodo]
  }

  def findPeriodo(toSearch: Periodo) =
    toSearch match {
      case Periodo(titulo, ini, fin) => sql"select * from periodos where titulo = $titulo and fechainicio = $ini and fechafin = $fin"
        .query[Periodo]
    }

  def findPeriodosByTitulo(titulo: String) =
    sql"select * from periodos where titulo = $titulo"
      .query[Periodo]

  def findPeriodosByIni(ini: Timestamp) =
    sql"select * from periodos where fechainicio = $ini"
      .query[Periodo]

  def findPeriodosByFin(fin: Timestamp) =
    sql"select * from periodos where fechafin = $fin"
      .query[Periodo]
}

trait InsertablePeriod {

  def insertPeriodo(toIns: Periodo) =
    toIns match {
      case Periodo(titulo, ini, fin) => sql"insert into periodos (titulo, fechainicio, fechafin) values ($titulo, $ini, $fin)"
        .update
    }

}

trait DeletablePeriod {

  def deletePeriodo(toDel: Periodo) =
    toDel match {
      case Periodo(titulo, fechaini, fechafin) => sql"delete from periodos where titulo = $titulo and fechainicio = $fechaini and fechafin = $fechafin"
        .update
    }

}
