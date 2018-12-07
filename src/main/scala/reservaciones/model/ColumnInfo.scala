package reservaciones.model

import doobie.implicits._

object ColumnInfo {

  def getReservaciones() = Connection.executeListQuery(getColumn("reservaciones"))


  def getSalones() = Connection.executeListQuery(getColumn("salones"))

  def getCursos() = Connection.executeListQuery(getColumn("cursos"))

  def getCursosActivos() = Connection.executeListQuery(getColumn("cursosactivos"))

  def getPeriodos() = Connection.executeListQuery(getColumn("periodos"))

  private def getColumn(table: String) =
    sql"select column_name from information_schema.columns where table_schema = 'public' and table_name = $table"
      .query[String]


}
