package reservaciones.view

import java.awt.Color

import reservaciones.model._

import swing.Swing._
import scala.swing._
import scala.swing.BorderPanel.Position._

object MainPanel {

  var info = new Table(){
    showGrid = false
    border = LineBorder(Color.BLACK)
    autoResizeMode = Table.AutoResizeMode.AllColumns

  }

  def getPanel: Panel = {
    new BorderPanel {

      val buttons = new BoxPanel(Orientation.Vertical){
        contents += new Button( Action("Mostrar Horarios Salones") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Horarios Salones Segun Criterio") { ReservSearch.top.visible_=(true) } )
        contents += new Button( Action("Mostrar Horarios Cursos") { showClassTimetables } )
        contents += new Button( Action("Mostrar Todas Reservaciones") { showReservations } )
        contents += new Button( Action("Mostrar Salones") { showRooms } )
        contents += new Button( Action("Mostrar Cursos Activos") { showActiveCourses } )
        contents += new Button( Action("Mostrar Periodos") { showPeriods } )
        contents += new Button( Action("Consulta Salas") { ClassRoomsSearch.top.visible_=(true) } )
        contents += new Button( Action("Modificacion Salas") { ModReserv.top.visible_=(true) } )
      }

      layout(buttons) = West
      layout(new ScrollPane(info)) = Center

    }
  }

  private def showRoomsTimetables = {

    val rowData = Connection.executeListQuery(ReservacionesModel.getAllActiveReservaciones()).map{
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(salon, fechaini, fechafin, clave.getOrElse("-"), secc.getOrElse("-"), periodo.getOrElse("-"), nombre).map(_.asInstanceOf[Any])
    }.toArray

    val names = ColumnInfo.getReservaciones()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()

  }

  private def showClassTimetables = {

    val rowData = Connection.executeListQuery(ReservacionesModel.getTimetableAllCursosActivos()).map{
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(salon, fechaini.toString, fechafin.toString, clave.getOrElse("-"), secc.getOrElse("-"), periodo.getOrElse("-"), nombre)
    }.toArray

    val names = ColumnInfo.getReservaciones()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()

  }

  private def showReservations = {

    val rowData = Connection.executeListQuery(ReservacionesModel.getAllReservaciones()).map{
      case Reservacion(salon, fechaini, fechafin, clave, secc, periodo, nombre) => Array(salon, fechaini.toString, fechafin.toString, clave.getOrElse("-"), secc.getOrElse("-"), periodo.getOrElse("-"), nombre).map(_.asInstanceOf[Any])
    }.toArray

    val names = ColumnInfo.getReservaciones()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()
  }

  private def showPeriods = {

    val rowData = Connection.executeListQuery(PeriodosModel.getAllPeriods()).map{
      case Periodo(titulo, fechaini, fechafin) => Array(titulo, fechaini.toString, fechafin.toString).map(_.asInstanceOf[Any])
    }.toArray

    val names = ColumnInfo.getPeriodos()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()
  }

  private def showRooms = {
    val rowData = Connection.executeListQuery(SalonesModel.getAllClassrooms()).map{
      case Salon(idsalon, capacidad, tipo) => Array(idsalon, capacidad, tipo).map(_.asInstanceOf[Any])
    }.toArray

    val names = ColumnInfo.getSalones()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()
  }

  private def showActiveCourses = {
    val rowData = Connection.executeListQuery(CursosActivosModel.getAllCurrentActiveCourses()).map{
      case CursoActivo(clave, secc, periodo) => Array(clave, secc, periodo).map(_.asInstanceOf[Any])
    }.toArray

    val names = ColumnInfo.getCursosActivos()

    val toUpdate = new Table(rowData, names)

    info.model = toUpdate.model

    info.revalidate()
    info.peer.repaint()
    info.peer.updateUI()
  }

}
