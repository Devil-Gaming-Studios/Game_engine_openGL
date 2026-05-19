package game_engine_opengl2;

public class launcher {
    public String getGreeting() {
        return "Welcome to Game Engine OpenGL2";
    }

    public static void main(String args[])
    {
        launcher app = new launcher();
        System.out.println(app.getGreeting());
        
        WindowManager window = new WindowManager("Game Engine",1600,900,false);
        window.init();
        while(!window.windowShouldClose())
        {
            window.update();
        }
        window.cleanup();
    }
}
