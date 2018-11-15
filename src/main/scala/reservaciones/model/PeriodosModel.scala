package reservaciones.model

import java.sql.Date

import doobie.implicits._

case class Periodo(titulo: String, fechaini: Date, fechafin: Date)

object PeriodosModel extends SearchablePeriod with InsertablePeriod with DeletablePeriod with UpdateablePeriod

trait SearchablePeriod {

  def getAllPeriods() =
    sql"select * from periodos"
      .query[Salon]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync()

  def findPeriodo(toSearch: Periodo) =
    toSearch match {
      case Periodo(titulo, ini, fin) => sql"select * from periodos where titulo = $titulo and fechaini = $ini and fechafin = $fin"
        .query[Periodo]
        .option
        .transact(Connection.xa)
        .unsafeRunSync()
    }

  def findPeriodoByTitulo(titulo: String) =
    sql"select * from periodo where titulo = $titulo"
      .query[Periodo]
      .option
      .transact(Connection.xa)
      .unsafeRunSync()

  def findPeriodoByIni(ini: Date) =
    sql"select * from periodo where fechaini = $ini"
      .query[Periodo]
      .option
      .transact(Connection.xa)
      .unsafeRunSync()

  def findPeriodoByFin(fin: Date) =
    sql"select * from periodo where fechafin = $fin"
      .query[Periodo]
      .option
      .transact(Connection.xa)
      .unsafeRunSync()
}

trait InsertablePeriod {

  def insertPeriodo(toIns: Periodo) =
    toIns match {
      case Periodo(titulo, ini, fin) => sql"insert into periodos (titulo, fechaini, fechafin) values ($titulo, $ini, $fin)"
        .update
        .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def insertPeriodoByTitulo(titulo: String) = {
    val today = new Date( new java.util.Date().getTime )
    val tomorrow = new Date( today.getTime + 24*60*60*1000 )
    insertPeriodo(Periodo(titulo, today, tomorrow))
  }

}

trait DeletablePeriod {
  def deletePeriodo(toDel: Periodo) =
    toDel match {
      case Periodo(titulo, ini, fin) => sql"delete from periodos where titulo = $titulo and fechaini = $ini and fechafin = $fin"
        .update
        .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def deletePeriodoByTitulo(titulo: String) =
    sql"delete from periodos where titulo = $titulo"
      .update
      .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
      .transact(Connection.xa)
      .unsafeRunSync

  def deletePeriodoByIni(ini: Date) =
    sql"delete from periodos where fechaini = $ini"
      .update
      .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
      .transact(Connection.xa)
      .unsafeRunSync

  def deletePeriodoByFin(fin: Date) =
    sql"delete from periodos where fechafin = $fin"
      .update
      .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
      .transact(Connection.xa)
      .unsafeRunSync
}

trait UpdateablePeriod {

  def updateIniPeriod(toUp: Periodo) =
    toUp match {
      case Periodo(titulo, ini, _) => sql"update salones set fechaini = $ini where titulo = $titulo"
        .update
        .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def updateFinPeriod(toUp: Periodo) =
    toUp match {
      case Periodo(titulo, _, fin) => sql"update salones set fechafin = $fin where titulo = $titulo"
        .update
        .withUniqueGeneratedKeys("titulo","fechaini","fechafin")
        .transact(Connection.xa)
        .unsafeRunSync
    }

}
