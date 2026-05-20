package game_engine_opengl2;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

public class EngineManager {
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE= 1000;

    private static int fps;
    private static final float FRAMETIME = 1.0f/FRAMERATE;

        private boolean isRunning;

        private WindowManager window;
        private GLFWErrorCallback errorCallback;
        private ILogic gameLogic;

        private void init() throws Exception
        {
            GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
            window = launcher.getWindow();
            gameLogic = launcher.getGame();
            window.init();
            gameLogic.init();
        }

        public void start() throws Exception
        {
            init();
            if(isRunning)
            {
                return;
            }
            run();
        }
        public void run()
        {
            this.isRunning = true;
            int frames = 0;
            long frameCounter = 0;
            long lastTime = System.nanoTime();
            double unprocessedTime = 0;

            while(isRunning)
            {
                boolean render = false;
                long startTime = System.nanoTime();
                long passedTime = startTime - lastTime;
                
                unprocessedTime += passedTime/(double)NANOSECOND;
                frameCounter += passedTime;

                input();

                while(unprocessedTime > FRAMETIME)
                {
                    render = true;
                    unprocessedTime -= FRAMETIME;
                    
                    if(window.windowShouldClose())
                        stop();

                    if(frameCounter >= NANOSECOND)
                    {
                        setFps(frames);
                        window.setTitle(Consts.TITLE + getFps());
                        System.out.println(getFps());
                        frames = 0;
                        frameCounter = 0;
                    }
                }
                if(render)
                {
                    update();
                    render();
                    frames++;
                }

            }

        }
        
        private void stop()
        {
            if(!isRunning)
                return;
            isRunning = false;
        }
        private void setFps(int f)
        {
            fps = f;
        }
        private int getFps()
        {
            return fps;
        }
        private void input()
        {
            gameLogic.input();
        }
        private void render()
        {
            gameLogic.render();
            window.update();
        }
        private void update()
        {
            gameLogic.update();
        }

        private void cleanup()
        {
            window.cleanup();
            gameLogic.cleanup();
            errorCallback.free();
            GLFW.glfwTerminate();
        }
}
