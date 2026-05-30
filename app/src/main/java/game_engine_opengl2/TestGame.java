package game_engine_opengl2;

import java.util.ArrayList;
import java.util.List;

import org.joml.Random;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import entity.Entity;
import entity.Material;
import entity.Model;
import entity.Texture;
import entity.terrain.Terrain;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;
import game_engine_opengl2.rendering.RenderManager;

public class TestGame implements ILogic
{
    private static final float CAMERA_MOVE_SPEED = Consts.CAMERA_STEP;

    private final RenderManager renderer;
    private final WindowManager window;
    public final ObjectLoader loader;

    private List<Entity> entities;
    private List<Terrain> terrains;

    private Camera camera;
    
    Vector3f cameraInc;

    private float lightAngle;
    private DirectionalLight directionalLight;
    private PointLight[] pointLights;
    private SpotLight[] spotLights;
    
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

        Model model = loader.loadResourceModel("/models/Datsun_280Z.obj");
        model.setTexture(new Texture(loader.loadResourceTexture("/textures/HatchbackYellow.png")),1.0f);

        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        Terrain terrain = new Terrain(new Vector3f(0, -1, -800), loader, new Material(new Texture(loader.loadResourceTexture("textures/grassBlock.jpg")),0.1f));
        Terrain terrain2 = new Terrain(new Vector3f(-800, -1, -800), loader, new Material(new Texture(loader.loadResourceTexture("textures/HatchbackYellow.png")),0.1f));
        terrains.add(terrain);
        terrains.add(terrain2);

        Random rnd = new Random();

        for(int i = 0; i< 200; i++ )
        {
            float x = rnd.nextFloat() * 100 - 50;
            float y = rnd.nextFloat() * 100 - 50;
            float z = rnd.nextFloat() * -300;
            entities.add(new Entity(model, new Vector3f(x,y,z), new Vector3f(rnd.nextFloat()*180,rnd.nextFloat()* 180, 0),1));
        }
        
        entities.add(new Entity(model, new Vector3f(0,0,0), new Vector3f(0,0, 0),1));

        //point light
        float lightIntensity = 1.0f;
        Vector3f lightPosition = new Vector3f(0,0,-3.2f);
        Vector3f lightColour = new Vector3f(1,1,1);
        PointLight pointLight = new PointLight(lightColour,lightPosition, lightIntensity, 0,0,1);

        //directional light
        lightPosition = new Vector3f(-1,-10,0);
        lightColour = new Vector3f(1,1,1);
        directionalLight = new DirectionalLight(lightColour, lightPosition, lightIntensity);

        //spot light
        Vector3f coneDirection = new Vector3f(0,0,1);
        float cutoff = (float)Math.cos(Math.toRadians(180));
        SpotLight spotLight = new SpotLight(pointLight, coneDirection,cutoff );

        pointLights = new PointLight[]{pointLight};
        spotLights = new SpotLight[]{spotLight};
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

        for(PointLight pointLight : pointLights)
        {
            if(window.isKeyPressed(GLFW.GLFW_KEY_0))
            {
                pointLight.getPosition().x += 0.1f;
            }
            
            if(window.isKeyPressed(GLFW.GLFW_KEY_P))
            {
                pointLight.getPosition().x -= 0.1f;
            }
        }
        
        for(SpotLight spotLight : spotLights)
        {
            float lightPos = spotLight.getPointLight().getPosition().z;
            if(window.isKeyPressed(GLFW.GLFW_KEY_N))
            {
                spotLight.getPointLight().getPosition().z = lightPos + 0.1f;
            }

            if(window.isKeyPressed(GLFW.GLFW_KEY_M))
            {
                spotLight.getPointLight().getPosition().z = lightPos - 0.1f;
            }
        }
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

        for(Entity entity : entities)
        {
            renderer.processEntity(entity);
        }

        for(Terrain terrain : terrains)
        {
            renderer.processTerrain(terrain);
        }
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
        renderer.render(camera,directionalLight,pointLights,spotLights);
    }

    @Override
    public void cleanup()
    {
        renderer.cleanup();
        loader.cleanup();
    }
}
