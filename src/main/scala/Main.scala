import scala.swing.{Dimension, MainFrame, SimpleSwingApplication}
import reservaciones._

object Main extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "Reservaciones"
    preferredSize = new Dimension(700,400)
    contents = view.MainPanel.getPanel
  }

}
