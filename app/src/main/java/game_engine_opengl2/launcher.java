package game_engine_opengl2;

public class launcher {
    private static WindowManager window;
    private static EngineManager engine;
    private static ILogic game;

    public String getGreeting() {
        return "Welcome to Game Engine OpenGL2";
    }

    public static void main(String args[])
    {
        launcher app = new launcher();
        System.out.println(app.getGreeting());
        
        window = new WindowManager(Consts.TITLE, 0, 0, false);
        game = new TestGame();
        engine = new EngineManager();
        
        try
        {
            engine.start();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public static WindowManager getWindow()
    {
        return window;
    }
    
    public static ILogic getGame()
    {
        return game;
    }
}
