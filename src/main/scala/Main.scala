import scala.swing.{Dimension, MainFrame, SimpleSwingApplication}
import reservaciones._

object Main extends SimpleSwingApplication {

  def top = new MainFrame {
    title = "Hello"
    preferredSize = new Dimension(400,400)
    contents = view.MainPanel.getPanel
  }

}
