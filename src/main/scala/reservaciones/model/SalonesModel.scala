package reservaciones.model

import doobie.implicits._

case class Salon(idsalon: String, capacidad: Int, tipo: String)

object SalonesModel extends SearchableClassroom with InsertableClassroom

trait SearchableClassroom{

  def getAllClassrooms() =
    sql"select * from salones"
      .query[Salon]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalon(toFind: Salon) =
    toFind match {
      case Salon(idsalon, capacidad, tipo) => sql"select * from salones where idsalon = $idsalon and capacidad = $capacidad and tipo = $tipo"
        .query[Salon]
        .option
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def findSalonesByCap(cap: Int) =
    sql"select * from salones where capacidad = $cap"
      .query[Salon]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalonesByTipo(tipo: String) =
    sql"select * from salones where tipo = $tipo"
      .query[Salon]
      .to[List]
      .transact(Connection.xa)
      .unsafeRunSync()
}

trait InsertableClassroom {

  def insertSalon(toIns: Salon) =
    toIns match {
      case Salon(id, cap, tipo) => sql"insert into salones (idsalon, capacidad, tipo) values ($id, $cap, $tipo)"
        .update
        .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
        .transact(Connection.xa)
        .unsafeRunSync
    }

}