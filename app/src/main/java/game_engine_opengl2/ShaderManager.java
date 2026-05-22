package game_engine_opengl2;

import org.lwjgl.opengl.GL20;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() throws Exception
    {
        programID = GL20.glCreateProgram();
        if(programID == 0)
            throw new Exception("Could not create shader");

    }

    public void createVertexShader(String shaderCode) throws Exception
    {
        vertexShaderID = createShader(shaderCode, GL20.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception
    {
        fragmentShaderID = createShader(shaderCode, GL20.GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL20.glCreateShader(shaderType);//creates a empty shader object
        if(shaderID == 0)
            throw new Exception("Error creating shader type : " + shaderType);

        GL20.glShaderSource(shaderID, shaderCode);//gives the shader code to the shader object
        GL20.glCompileShader(shaderID);//compiles the shaders to GPU readable

        if(GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS) == 0)
            throw new Exception("Error compiling shader code: TYPE : " + shaderType + " Info " + GL20.glGetShaderInfoLog(shaderID, 1024));
        
        GL20.glAttachShader(programID, shaderID);//Add this shader into this program.

        return shaderID;
    }

    public void link() throws Exception
    {
        GL20.glLinkProgram(programID);//connects all compiled shaders together into one usable GPU program
        if(GL20.glGetProgrami(programID ,GL20.GL_LINK_STATUS) == 0)
            throw new Exception("Error linking shader code: TYPE : " + " Info " + GL20.glGetProgramInfoLog(programID, 1024));

        if(vertexShaderID != 0)
            GL20.glDetachShader(programID, vertexShaderID);//Detaches vertexShader from the program once we link the program into the final GPU program

        if(fragmentShaderID != 0)
            GL20.glDetachShader(programID,fragmentShaderID);//Detaches fragment Shader from the program once we link the program into the final GPU program

        GL20.glValidateProgram(programID);
        /*glLinkProgram() only checks:

Are shaders compatible?
Do inputs/outputs match?
Is the program structurally valid?

But glValidateProgram() checks:

Can this program run on the current GPU state?
Are the currently bound VAOs/VBOs compatible?
Are uniforms/samplers valid?
Is the program executable right now? */

/* use glValidateProgram() only in debug mode
skip it in release builds*/

        if(GL20.glGetProgrami(programID, GL20.GL_VALIDATE_STATUS) == 0)
            throw new Exception("Unable to validate shader code: " + GL20.glGetProgramInfoLog(programID,1024));
    }
    
    public void bind()
    {
        GL20.glUseProgram(programID);
    }

    public void unbind()
    {
        GL20.glUseProgram(0);
    }

    public void cleanup()
    {
        unbind();
        if(programID != 0)
            GL20.glDeleteProgram(programID);
    }
}
