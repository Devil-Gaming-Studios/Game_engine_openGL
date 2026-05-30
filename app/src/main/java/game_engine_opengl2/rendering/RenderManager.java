package game_engine_opengl2.rendering;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import entity.Entity;
import entity.Model;
import entity.SceneManager;
import entity.terrain.Terrain;
import entity.terrain.TerrainRenderer;
import game_engine_opengl2.Camera;
import game_engine_opengl2.Consts;
import game_engine_opengl2.ShaderManager;
import game_engine_opengl2.WindowManager;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;


public class RenderManager {
    private final WindowManager window;
    private EntityRender entityRenderer;
    private TerrainRenderer terrainRenderer;

    public RenderManager(WindowManager windowManager)
    {
        window = windowManager;
    }

    public void init() throws Exception
    {
        entityRenderer = new EntityRender();
        terrainRenderer = new TerrainRenderer();
        
        entityRenderer.init();
        terrainRenderer.init();

    }

    public void bind(Model model)
    {
        
        
    }

    public void unbind()
    {
        
        
    }

    public void prepare(Entity entity,Camera camera)
    {
        
    }

    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight, ShaderManager shader)
    {
        shader.setUniform("ambientLight",Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower",Consts.SPECULAR_POWER);
        shader.setUniform("directionalLight", directionalLight);

        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights;i++)
        {
            shader.setUniform("spotLights",spotLights[i],i);
        }

        numLights = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < numLights;i++)
        {
            shader.setUniform("pointLights",pointLights[i],i);
        }
    }

    public void render(Camera camera, SceneManager sceneManager)
    {
        clear();

        if(window.isResize())
        {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
        }
        
        entityRenderer.render(camera, sceneManager.getPointLights(),sceneManager.getSpotLights(),sceneManager.getDirectionalLight());
        terrainRenderer.render(camera, sceneManager.getPointLights(),sceneManager.getSpotLights(),sceneManager.getDirectionalLight());
    }

    public void processEntity(Entity entity)
    {
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if(entityList != null)
            entityList.add(entity);
        else
        {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain)
    {
        terrainRenderer.getTerrains().add(terrain);
    }
    
    public void clear()
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        
    }

    public void cleanup()
    {
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }
}
