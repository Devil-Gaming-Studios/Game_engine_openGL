package game_engine_opengl2;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import entity.Entity;
import entity.Model;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.utils.Transformation;
import game_engine_opengl2.utils.Utils;


public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager(WindowManager windowManager)
    {
        window = windowManager;
    }

    public void init() throws Exception
    {
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
        
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
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
        shader.createPointLightUniform("pointLight");
    }

    public void render(Entity entity, Camera camera, DirectionalLight directionalLight, PointLight pointLight)
    {
        Model model = entity.getModel();
        clear();

        if(window.isResize())
        {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
        }
        shader.bind();
        shader.setUniform("textureSampler",0);
        shader.setUniform("transformationMatrix",Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix",window.updateProjectionMatrix());
        shader.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
        // Material uniform not available
        shader.setUniform("material", entity.getModel().getMaterial());
        shader.setUniform("ambientLight",Consts.AMBIENT_LIGHT);
        shader.setUniform("specularPower",Consts.SPECULAR_POWER);
        shader.setUniform("directionalLight", directionalLight);
        shader.setUniform("pointLight",pointLight);

        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL20.glEnableVertexAttribArray(2);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT,0);
        GL20.glDisableVertexAttribArray(0);//position 
        GL20.glDisableVertexAttribArray(1);//texture coordinates
        GL20.glDisableVertexAttribArray(2);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }
    
    public void clear()
    {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup()
    {
        shader.cleanup();
    }
}
