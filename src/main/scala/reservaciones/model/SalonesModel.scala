package reservaciones.model

import doobie.implicits._

case class Salon(idsalon: String, capacidad: Int, tipo: String)

trait SalonesModel {
  def getAllClassrooms() =
    sql"select * from salones"
      .query[Salon]
      .to[Array]
      .transact(Connection.xa)
      .unsafeRunSync()

  def findSalonByID(search: String) =
    sql"select * from salones where idsalon = $search".query[Salon]
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

  def insertSalon(id: String, cap: Int, tipo: String) =
    sql"insert into salones (idsalon, capacidad, tipo) values ($id, $cap, $tipo)"
      .update
      .withUniqueGeneratedKeys("idsalon","capacidad","tipo")
      .transact(Connection.xa)
      .unsafeRunSync

  def insertSalonOnlyID(id: String) =
    insertSalon(id, 0, "")

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