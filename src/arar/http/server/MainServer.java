package arar.http.server;

/**
 * @author Thomas Arnaud, Bruno Buiret, Sydney Adjou
 */
public abstract class MainServer 
{
    public static void main(String[] args)
    {
        HTTPServer s = new HTTPServer(10080, "D:\\HTTP\\ServerRoot", "D:\\HTTP\\ServerRoot\\", true);
        s.run();
    }
}
