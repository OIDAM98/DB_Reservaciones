package reservaciones.model

import doobie.implicits._

case class Salon(idsalon: String, capacidad: Int, tipo: String)

object SalonesModel extends SearchableClassroom with InsertableClassroom with DeletableClassroom with UpdateableClassroom

trait SearchableClassroom{

  def getAllClassrooms() =
    sql"select * from salones"
      .query[Salon]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalonByID(search: String) =
    sql"select * from salones where idsalon = $search"
      .query[Salon]
      .option
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalonByCap(cap: Int) =
    sql"select * from salones where capacidad = $cap"
      .query[Salon]
      .option
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalonByTipo(tipo: String) =
    sql"select * from salones where tipo = $tipo"
      .query[Salon]
      .option
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

  def insertSalonOnlyID(id: String) =
    insertSalon(Salon(id, 0, ""))
}

trait DeletableClassroom {

  def deleteSalon(toDel: Salon) =
    toDel match {
      case Salon(id, cap, tipo) => sql"delete from salones where idsalon = $id"
        .update
        .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def deleteSalonById(id: String) =
    deleteSalon(Salon(id, 0, ""))

  def deleteSalonesByCap(cap: Int) =
    sql"delete from salones where capacidad = $cap"
      .update
      .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
      .transact(Connection.xa)
      .unsafeRunSync

  def deleteSalonesByTipo(tipo: String) =
    sql"delete from salones where tipo = $tipo"
      .update
      .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
      .transact(Connection.xa)
      .unsafeRunSync
}

trait UpdateableClassroom {

  def updateCapSalon(toUp: Salon) =
    toUp match {
      case Salon(id, cap, _) => sql"update salones set capacidad = $cap where idsalon = $id"
        .update
        .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
        .transact(Connection.xa)
        .unsafeRunSync
    }

  def updateTipoSalon(toUp: Salon) =
    toUp match {
      case Salon(id, _, tipo) => sql"update salones set tipo = $tipo where idsalon = $id"
        .update
        .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
        .transact(Connection.xa)
        .unsafeRunSync
    }
}