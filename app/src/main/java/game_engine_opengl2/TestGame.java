package game_engine_opengl2;

public class TestGame implements ILogic {
    private final RenderManager renderManager;

    public TestGame() {
        renderManager = new RenderManager();
    }

    @Override
    public void init() throws Exception {
        renderManager.init();
    }

    @Override
    public void input() {
        // Handle input here
    }

    @Override
    public void update() {
        // Update game logic here
    }

    @Override
    public void render() {
        renderManager.render();
    }

    @Override
    public void cleanup() {
        renderManager.cleanup();
    }
}
