package game_engine_opengl2;

public interface ILogic {
    void init() throws Exception;
    
    void input();

    void update(MouseInput mouseInput);

    void render();

    void cleanup();
}
