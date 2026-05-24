package game_engine_opengl2;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

import entity.Entity;
import entity.Model;
import game_engine_opengl2.utils.Transformation;
import game_engine_opengl2.utils.Utils;


public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager()
    {
        window = launcher.getWindow();
    }

    public void init() throws Exception
    {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
    }

    public void render(Entity entity, Camera camera)
    {
        Model model = entity.getModel();
        clear();
        shader.bind();
        shader.setUniform("textureSampler",0);
        shader.setUniform("transformationMatrix",Transformation.createTransformationMatrix(entity));
        shader.setUniform("projectionMatrix",window.updateProjectionMatrix());
        shader.setUniform("viewMatrix",Transformation.getViewMatrix(camera));
        GL30.glBindVertexArray(model.getId());
        GL20.glEnableVertexAttribArray(0);
        GL20.glEnableVertexAttribArray(1);
        GL13.glActiveTexture(GL13.GL_TEXTURE0);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, model.getTexture().getId());
        GL11.glDrawElements(GL11.GL_TRIANGLES, model.getVertexCount(), GL11.GL_UNSIGNED_INT,0);
        GL11.glDrawArrays(GL11.GL_TRIANGLES,0,model.getVertexCount());
        GL20.glDisableVertexAttribArray(0);//position 
        GL20.glDisableVertexAttribArray(1);//texture coordinates
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
