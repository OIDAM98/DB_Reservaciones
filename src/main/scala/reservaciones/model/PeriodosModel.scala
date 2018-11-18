package reservaciones.model

import java.sql.Date

import doobie.implicits._

case class Periodo(titulo: String, fechaini: Date, fechafin: Date){
  require(titulo.length <= 20)
  override def toString: String = s"$titulo $fechaini $fechafin"
}

object PeriodosModel extends SearchablePeriod with InsertablePeriod

trait SearchablePeriod {

  def getAllPeriods() =
    sql"select * from periodos"
      .query[Periodo]

  def getCurrentPeriod() ={
    val today = new Date( new java.util.Date().getTime )
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

  def findPeriodosByIni(ini: Date) =
    sql"select * from periodos where fechainicio = $ini"
      .query[Periodo]

  def findPeriodosByFin(fin: Date) =
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
