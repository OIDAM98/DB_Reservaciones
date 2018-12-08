package reservaciones.view

import java.sql.{Timestamp}
import java.time.{LocalDate, LocalDateTime, LocalTime}

import javax.swing.{ JOptionPane}
import javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE

import scala.swing._
import reservaciones.model._

object ModCurso extends SimpleSwingApplication{

  var selectionReserv: Reservacion = null
  var selectionCursoActivo:CursoActivo = null

  var comboCursoActivo: ComboBox[CursoActivo] = null
  var comboReserv: ComboBox[Reservacion] = null

  var selectAdd:Button = null
  var selectSuprim:Button = null

  def top = new MainFrame {

    peer.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE)
    override def closeOperation() { dispose() }

    title = "Modificación de Cursos"

    val reservaciones = Connection.executeListQuery(ReservacionesModel.getAllReservaciones())
    comboReserv = new ComboBox(reservaciones)
    comboReserv.visible = false

    val cursos = Connection.executeListQuery(CursosActivosModel.getAllActiveCourses())

    comboCursoActivo = new ComboBox(cursos)
    comboCursoActivo.visible = false

    selectAdd = new Button( Action("Modificar reservación") {
      selectionReserv = comboReserv.selection.item
      selectionCursoActivo = comboCursoActivo.selection.item

      modifyReserv
    })
    selectAdd.visible = false

    selectSuprim = new Button( Action("Suprimir curso de Horarios") {
      selectionCursoActivo = comboCursoActivo.selection.item

      deleteCurso

    })
    selectSuprim.visible = false

    preferredSize = new Dimension(600, 450)

    contents = new GridPanel(8, 1){
      contents += new Button( Action("Modificar un Curso") {
        selectSuprim.visible = false

        comboReserv.visible = true
        comboCursoActivo.visible = true
        selectAdd.visible = true
      })
      contents += new Button( Action("Eliminar un Curso") {
        selectAdd.visible = false
        comboReserv.visible = false

        comboCursoActivo.visible = true
        selectSuprim.visible = true
      } )

      contents += comboReserv
      contents += comboCursoActivo
      contents += selectAdd
      contents += selectSuprim
    }

  }

  def deleteCurso() = {

      try{
        Connection.executeUpdate(Reservacion, ReservacionesModel.deleteHorarioCurso(selectionCursoActivo))
        JOptionPane.showMessageDialog(null, "Borrado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
      }
      catch {
        case e: java.sql.SQLException => {
          JOptionPane.showMessageDialog(null,
            s"${e.getErrorCode}: + ${e.getMessage}",
            "SQLError",
            JOptionPane.ERROR_MESSAGE
          )
        }
      }

      comboReserv.enabled = true

      selectSuprim.visible = false


  }

  def modifyReserv() = {

    try{
      val name = JOptionPane.showInputDialog("Introduce quien desea reservar")
      val c = selectionCursoActivo
      val nRes = Reservacion(selectionReserv.salon, selectionReserv.fechaini, selectionReserv.fechafin, Some(c.clave), Some(c.secc), Some(c.periodo), name)
      Connection.executeUpdate(Reservacion, ReservacionesModel.modifyTimetable(selectionReserv, nRes))
      JOptionPane.showMessageDialog(null, "Modificado exitoso", "Confirmacion", JOptionPane.INFORMATION_MESSAGE)
    }
    catch {
      case e: java.sql.SQLException => {
        JOptionPane.showMessageDialog(null,
          s"${e.getErrorCode}: + ${e.getMessage}",
          "SQLError",
          JOptionPane.ERROR_MESSAGE
        )
      }
    }

    comboReserv.enabled = true
    selectAdd.visible = false

  }

}
