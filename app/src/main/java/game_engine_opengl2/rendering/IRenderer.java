package game_engine_opengl2.rendering;

import entity.Model;
import game_engine_opengl2.Camera;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;

public interface IRenderer<T> {
    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, DirectionalLight directionalLight);

    abstract void bind(Model model);

    public void unbind();

    public void prepare(T t,Camera camera);

    public void cleanup();
}
