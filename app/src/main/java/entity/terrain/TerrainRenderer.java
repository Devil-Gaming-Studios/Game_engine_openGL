package entity.terrain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import entity.Entity;
import entity.Model;
import game_engine_opengl2.Camera;
import game_engine_opengl2.Consts;
import game_engine_opengl2.ShaderManager;
import game_engine_opengl2.launcher;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;
import game_engine_opengl2.rendering.IRenderer;
import game_engine_opengl2.rendering.RenderManager;
import game_engine_opengl2.utils.Transformation;
import game_engine_opengl2.utils.Utils;

public class TerrainRenderer implements IRenderer<Terrain>{

    ShaderManager shader;
    private List<Terrain> terrains;

    public TerrainRenderer() throws Exception
    {
        terrains = new ArrayList<>();
        shader = new ShaderManager();
        
    }
    
    @Override
    public void bind(Model model) {
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        shader.setUniform("material", model.getMaterial());
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        
    }

    @Override
    public void cleanup() {
        shader.cleanup();
        
    }

    @Override
    public void init() throws Exception {
        shader.createVertexShader(Utils.loadResource("/shaders/terrain_vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/terrain_fragments.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
        shader.createUniform("ambientLight");
        // Material uniforms are optimized away by shader compiler if unused
        shader.createMaterialUniform("material");
        shader.createUniform("specularPower");
        shader.createDirectionalLightUniform("directionalLight");
        shader.createPointLightListUniform("pointLights",Consts.MAX_POINT_LIGHTS);
        shader.createSpotLightListUniform("spotLights",Consts.MAX_SPOT_LIGHTS);

        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        
    }

    @Override
    public void prepare(Terrain terrain, Camera camera) {
        shader.setUniform("textureSampler",0);
        shader.setUniform("transformationMatrix",Transformation.createTransformationMatrix((Terrain) terrain));
        shader.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
        
    }

    @Override
    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights,
            DirectionalLight directionalLight) {
        shader.bind();
        shader.setUniform("projectionMatrix",launcher.getWindow().updateProjectionMatrix());
        RenderManager.renderLights(pointLights, spotLights, directionalLight,shader);

        for(Terrain terrain : terrains)
        {
            bind(terrain.getModel());
            prepare(terrain, camera);
            GL11.glDrawElements(GL11.GL_TRIANGLES, terrain.getModel().getVertexCount(), GL11.GL_UNSIGNED_INT,0);
            unbind();
        }
        terrains.clear();
        shader.unbind();
        
    }

    @Override
    public void unbind() {
        GL20.glDisableVertexAttribArray(0);//position 
        GL20.glDisableVertexAttribArray(1);//texture coordinates
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }
    
}

