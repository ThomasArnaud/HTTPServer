package arar.http.client;

/**
 * @author Bruno Buiret, Thomas Arnaud, Sydney Adjou
 */
public class MainClient
{
    public static void main(String args[])
    {
        try
        {
            javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        }
        catch(ClassNotFoundException|InstantiationException|IllegalAccessException|javax.swing.UnsupportedLookAndFeelException e)
        {
            java.util.logging.Logger.getLogger(ClientView.class.getName()).log(java.util.logging.Level.SEVERE, e.getMessage(), e);
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(() ->
        {
            ClientView dialog = new ClientView();
            dialog.addWindowListener(new java.awt.event.WindowAdapter()
            {
                @Override
                public void windowClosing(java.awt.event.WindowEvent e)
                {
                    System.exit(0);
                }
            });
            dialog.setVisible(true);
        });
    }
}
