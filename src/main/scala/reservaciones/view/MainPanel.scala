package reservaciones.view

import scala.swing.{Action, BorderPanel, Button, FlowPanel, Panel}

object MainPanel {

  def getPanel: Panel = {
    new BorderPanel {
      val buttons = new FlowPanel{
        contents += new Button( Action("Mostrar Horarios Salones") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Horarios Cursos") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Reservaciones") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Salones") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Cursos Activos") { showRoomsTimetables } )
        contents += new Button( Action("Mostrar Periodos") { showRoomsTimetables } )
      }
    }
  }

  private def showRoomsTimetables = ???

  private def showClassTimetables = ???

  private def showReservationsTimetables = ???

  private def showClassrooms = ???

  private def showActiveCourses = ???

  private def showPeriods = ???

}
