package arar.http.server;

/**
 *
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public abstract class MainServer 
{
    public static void main(String[] args)
    {
        HTTPServer s = new HTTPServer(80, "C:\\Users\\thomas\\Documents\\Cours 3A\\Réseau\\ServerRoot", "C:\\Users\\thomas\\Documents\\Cours 3A\\Réseau\\DocumentRoot");
        s.run();
    }
}
