package game_engine_opengl2;

import org.joml.Matrix4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

public class WindowManager {
    public static final float FOV = Consts.FOV;
    public static final float Z_NEAR = Consts.Z_NEAR;
    public static final float Z_FAR = Consts.Z_FAR;

    private String title;

    private int width, height;
    private long window;

    private boolean resize;
    private final boolean vSync;

    private final Matrix4f projectionMatrix;

    public WindowManager(String title, int width, int height, boolean vSync) {
        this.title = title;
        this.width = width;
        this.height = height;
        this.vSync = vSync;
        projectionMatrix = new Matrix4f();
    }

    public void init() {
        // setting up default error handling mechanism
        GLFWErrorCallback.createPrint(System.err).set();

        if (!GLFW.glfwInit())// initilizes the glfw and return true
        {
            throw new IllegalStateException("unable to initialize GLFW");
        }

        // setting the properties of the window before actually creating the window
        GLFW.glfwDefaultWindowHints();// giving the GLFW window default values
        GLFW.glfwWindowHint(GLFW.GLFW_VISIBLE, GL11.GL_FALSE);
        GLFW.glfwWindowHint(GLFW.GLFW_RESIZABLE, GL11.GL_TRUE);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MAJOR, 3);
        GLFW.glfwWindowHint(GLFW.GLFW_CONTEXT_VERSION_MINOR, 3);// version OpenGL 3.3
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_PROFILE, GLFW.GLFW_OPENGL_CORE_PROFILE);// It removes old/deprecated
                                                                                     // features and forces you to use
                                                                                     // the newer OpenGL pipeline
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_FORWARD_COMPAT, GL11.GL_TRUE);// stops usage of any old function not in
                                                                           // mentioned version of OpenGL
        boolean maximised = false;       

        //setting the window size to max if width or height == 0
        if (width == 0 || height == 0) {
            width = 100;
            height = 100;
            GLFW.glfwWindowHint(GLFW.GLFW_MAXIMIZED, GLFW.GLFW_TRUE);
            maximised = true;
        }

        //creating window
        window = GLFW.glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
        
        if(window == MemoryUtil.NULL)
        {
            throw new RuntimeException("Failed to create the Window");
        }

        //updates the OpenGL viewport whenever the window buffer is resized
        GLFW.glfwSetFramebufferSizeCallback(window, (wnd, w, h)->
        {
            this.height = h;
            this.width = w;
            this.setResize(true);
        });


        //setting keyboard call back for glfw window
        GLFW.glfwSetKeyCallback(window, (w, key, scancode, actions, mods)->
        {
            if(key == GLFW.GLFW_KEY_ESCAPE && actions == GLFW.GLFW_RELEASE)
            {
                GLFW.glfwSetWindowShouldClose(w, true);
            }
        });

    if(maximised)
    {
        GLFW.glfwMaximizeWindow(window);
    }
    else
    {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(window);
        if(vidMode != null)
        {
            GLFW.glfwSetWindowPos(window, (vidMode.width() - width)/2, (vidMode.height() - height)/2);
        }
    }

    //to create the enviornment to work on
    GLFW.glfwMakeContextCurrent(window);

    //setting the sync rate same as screen
    if(getvSync())
    {
        GLFW.glfwSwapInterval(1);
    }

    GLFW.glfwShowWindow(window);

    //Load and connect all the OpenGL functions for the current context
    GL.createCapabilities();

    GL11.glClearColor(0.0f,0.0f,0.0f,0.0f);
    GL11.glEnable(GL11.GL_DEPTH_TEST);//Enables depth
    GL11.glEnable(GL11.GL_STENCIL_TEST);//enables stencil(masks) where drawing should be controlled
    //GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_LINE); // wireframe both sides
    GL11.glEnable(GL11.GL_CULL_FACE);//hides the back sides of the triangles need not to rendered 
    GL11.glCullFace(GL11.GL_BACK);
    }

    //updates the screen by swapping the back and front buffer
    public void update()
    {
        GLFW.glfwSwapBuffers(window);
        GLFW.glfwPollEvents();
    }

    public void cleanup()
    {
        GLFW.glfwDestroyWindow(window);
    }

    public void setClearColour(float r, float b, float g, float a)
    {
        GL11.glClearColor(r,b,g,a);
    }

    public boolean isKeyPressed(int keycode)
    {
        return GLFW.glfwGetKey(window,keycode) == GLFW.GLFW_PRESS;
    }

    public boolean windowShouldClose()
    {
        return GLFW.glfwWindowShouldClose(window);
    }

    public void setTitle(String title)
    {
        GLFW.glfwSetWindowTitle(window, title);
        this.title = title;
    }

    public String getTitle()
    {
        return title;
    }

    public boolean isResize()
    {
        return resize;
    }

    public boolean isvSync()
    {
        return vSync;
    }

    public void setResize(boolean resize)
    {
        this.resize = resize;
    }

    public boolean getvSync()
    {
        return vSync;
    }

    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }

    public long getWindow()
    {
        return window;
    }

    public Matrix4f getProjectionMatrix()
    {
        return projectionMatrix;
    }

    public Matrix4f updateProjectionMatrix()
    {
        float aspectRatio = (float)width/height;
        return projectionMatrix.setPerspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
    }

    public Matrix4f updateProjectionMatrix(Matrix4f matrix, int width, int height)
    {
        float aspectRatio = (float)width/height;
        return matrix.setPerspective(FOV,aspectRatio,Z_NEAR,Z_FAR);
    }

}

