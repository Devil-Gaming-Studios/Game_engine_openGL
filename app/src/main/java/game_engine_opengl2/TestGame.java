package game_engine_opengl2;

import javax.print.attribute.standard.MediaName;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import entity.Entity;
import entity.Model;
import entity.Texture;
import game_engine_opengl2.lighting.DirectionalLight;

public class TestGame implements ILogic
{
    private static final float CAMERA_MOVE_SPEED = Consts.CAMERA_STEP;

    private final RenderManager renderer;
    private final WindowManager window;
    public final ObjectLoader loader;

    private Entity entity;
    private Camera camera;
    
    Vector3f cameraInc;

    private float lightAngle;
    private DirectionalLight directionalLight;
    
    public TestGame(WindowManager windowManager)
    {
        renderer = new RenderManager(windowManager);
        window = windowManager;
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0,0,0);
        lightAngle = -90.0f;
    }

    @Override
    public void init() throws Exception
    {
        renderer.init();
        // float[] vertices = new float[] {
        //     -0.5f, 0.5f, 0.5f,
        //     -0.5f, -0.5f, 0.5f,
        //     0.5f, -0.5f, 0.5f,
        //     0.5f, 0.5f, 0.5f,
        //     -0.5f, 0.5f, -0.5f,
        //     0.5f, 0.5f, -0.5f,
        //     -0.5f, -0.5f, -0.5f,
        //     0.5f, -0.5f, -0.5f,
        //     -0.5f, 0.5f, -0.5f,
        //     0.5f, 0.5f, -0.5f,
        //     -0.5f, 0.5f, 0.5f,
        //     0.5f, 0.5f, 0.5f,
        //     0.5f, 0.5f, 0.5f,
        //     0.5f, -0.5f, 0.5f,
        //     -0.5f, 0.5f, 0.5f,
        //     -0.5f, -0.5f, 0.5f,
        //     -0.5f, -0.5f, -0.5f,
        //     0.5f, -0.5f, -0.5f,
        //     -0.5f, -0.5f, 0.5f,
        //     0.5f, -0.5f, 0.5f,
        // };
        // float[] textureCoords = new float[]{
        //             0.0f, 0.0f,
        //             0.0f, 0.5f,
        //             0.5f, 0.5f,
        //             0.5f, 0.0f,
        //             0.0f, 0.0f,
        //             0.5f, 0.0f,
        //             0.0f, 0.5f,
        //             0.5f, 0.5f,
        //             0.0f, 0.5f,
        //             0.5f, 0.5f,
        //             0.0f, 1.0f,
        //             0.5f, 1.0f,
        //             0.0f, 0.0f,
        //             0.0f, 0.5f,
        //             0.5f, 0.0f,
        //             0.5f, 0.5f,
        //             0.5f, 0.0f,
        //             1.0f, 0.0f,
        //             0.5f, 0.5f,
        //             1.0f, 0.5f,
        // };
        // int[] indices = new int[]{
        //             0, 1, 3, 3, 1, 2,
        //             8, 10, 11, 9, 8, 11,
        //             12, 13, 7, 5, 12, 7,
        //             14, 15, 6, 4, 14, 6,
        //             16, 18, 19, 17, 16, 19,
        //             4, 6, 7, 5, 4, 7,
        // };
      
            Model model = loader.loadResourceModel("/models/Datsun_280Z.obj");
            model.setTexture(new Texture(loader.loadResourceTexture("/textures/HatchbackYellow.png")),1.0f);
            entity = new Entity(model, new Vector3f(1,0,0), new Vector3f(0,0,0),1);
            
            float lightIntensity = 0.0f;
            Vector3f lightPosition = new Vector3f(-1,-10,0);
            Vector3f lightColour = new Vector3f(1,1,1);
            directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);
    }

    @Override
    public void input()
    {
        cameraInc.set(0,0,0);
        if(window.isKeyPressed(GLFW.GLFW_KEY_W))
            cameraInc.z = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_S))
            cameraInc.z = 1;
        
        if(window.isKeyPressed(GLFW.GLFW_KEY_A))
            cameraInc.x = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_D))
            cameraInc.x = 1;

        if(window.isKeyPressed(GLFW.GLFW_KEY_UP))
            cameraInc.y = -1;
        if(window.isKeyPressed(GLFW.GLFW_KEY_DOWN))
            cameraInc.y = 1;
        
    }

    @Override
    public void update(MouseInput mouseInput)
    {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED, cameraInc.y*CAMERA_MOVE_SPEED, cameraInc.z*CAMERA_MOVE_SPEED);

        if(mouseInput.isRightButtonPress())
        {
            Vector2f rotVec = mouseInput.getDisplVec();
            camera.moveRotation(rotVec.x * Consts.MOUSE_SENSITIVITY, rotVec.y * Consts.MOUSE_SENSITIVITY, 0);
        }
        //entity.incRotation(0.0f ,0.5f, 0.0f);
        lightAngle += 0.5f;
        if(lightAngle > 90)
        {
            directionalLight.setIntensity(0);
            if(lightAngle >= 360)
                lightAngle =- 90;
        }
        else if(lightAngle <= -80 || lightAngle >= 80)
        {
            float factor = 1 - (float)(Math.abs(lightAngle) - 80) / 10.0f;
            directionalLight.setIntensity(factor);
            directionalLight.getColour().y = Math.max(factor,0.9f);
            directionalLight.getColour().z = Math.max(factor,0.5f);
        }
        else
        {
            directionalLight.setIntensity(1);
            directionalLight.getColour().x = 1;
            directionalLight.getColour().y = 1;
            directionalLight.getColour().z = 1;
        }
        double angRad = Math.toRadians(lightAngle);
        directionalLight.getDirection().x = (float)Math.sin(angRad);
        directionalLight.getDirection().y = (float)Math.cos(angRad);
    }   

    @Override
    public void render()
    {
        if(window.isResize())
        {
            GL11.glViewport(0,0,window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColour(0.0f,0.0f,0.0f,0.0f);
        renderer.render(entity,camera,directionalLight);
    }

    @Override
    public void cleanup()
    {
        renderer.cleanup();
        loader.cleanup();
    }
}
