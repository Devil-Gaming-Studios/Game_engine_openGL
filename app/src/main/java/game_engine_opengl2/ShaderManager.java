package game_engine_opengl2;

import java.util.HashMap;
import java.util.Map;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;

import entity.Material;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    private final Map<String,Integer> uniforms;

    public ShaderManager() throws Exception
    {
        programID = GL20.glCreateProgram();
        
        if(programID == 0)
            throw new Exception("Could not create shader");

        this.uniforms = new HashMap<>();
    }

    public void createUniform(String uniformName) throws Exception
    {
        int uniformLocation = GL20.glGetUniformLocation(programID, uniformName);
        if(uniformLocation < 0)
            throw new Exception("could not find uniform" + uniformName);
        uniforms.put(uniformName, uniformLocation);
    }

    public void createDirectionalLightUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".direction");
        createUniform(uniformName + ".intensity");
    }

    public void createMaterialUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".ambient");
        createUniform(uniformName + ".diffuse");
        createUniform(uniformName + ".specular");
        createUniform(uniformName + ".hasTexture");
        createUniform(uniformName + ".reflectance");
    }

    public void createPointLightUniform(String uniformName) throws Exception
    {
        createUniform(uniformName + ".colour");
        createUniform(uniformName + ".position");
        createUniform(uniformName + ".intensity");
        createUniform(uniformName + ".linear");
        createUniform(uniformName + ".exponent");
        createUniform(uniformName + ".constant");
    }

   public void createSpotLightUniform(String uniformName) throws Exception
   {
        createUniform(uniformName + ".cutoff");
        createPointLightUniform(uniformName + ".pl");
        createUniform(uniformName + ".conedir");
   }


   public void createPointLightListUniform(String uniformName, int size) throws Exception
   {
    for(int i = 0; i < size;  i++)
    {
        createPointLightUniform(uniformName + "[" + i + "]");
    }
   }

   public void createSpotLightListUniform(String uniformName, int size) throws Exception
   {
    for(int i = 0; i < size;  i++)
    {
        createSpotLightUniform(uniformName + "[" + i + "]");
    }
   }

    public void setUniform(String uniformname,Matrix4f value)
    {
        try(MemoryStack stack = MemoryStack.stackPush())
        {
            GL20.glUniformMatrix4fv(uniforms.get(uniformname),false,value.get(stack.mallocFloat(16)));//uploads the matrix to the GPU
        }
    }

     public void setUniform(String uniformName,Vector4f value)
    {
        GL20.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }

    public void setUniform(String uniformName,Vector3f value)
    {
        GL20.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, boolean value)
    {
        float res = 0;
        if(value)
            res = 1;
        GL20.glUniform1f(uniforms.get(uniformName),res);
    }

    public void setUniform(String uniformName, Material material)
    {

        setUniform(uniformName + ".ambient", material.getAmbientColour());
        setUniform(uniformName + ".diffuse",material.getDiffuseColour());
        setUniform(uniformName + ".specular",material.getSpecularColour());
        setUniform(uniformName + ".hasTexture",material.hasTexture() ? 1:0);
        setUniform(uniformName + ".reflectance",material.getReflectance());
    }

    public void setUniform(String uniformName,DirectionalLight directionalLight)
    {
        setUniform(uniformName + ".colour", directionalLight.getColour());
        setUniform(uniformName + ".direction", directionalLight.getDirection());
        setUniform(uniformName + ".intensity", directionalLight.getIntensity());
    }

    public void setUniform(String uniformName,PointLight pointLight)
    {
        setUniform(uniformName+".colour",pointLight.getColour());
        setUniform(uniformName+".position",pointLight.getPosition());
        setUniform(uniformName+".intensity",pointLight.getIntensity());
        setUniform(uniformName+".linear",pointLight.getLinear());
        setUniform(uniformName+".exponent",pointLight.getExponent());
        setUniform(uniformName+".constant", pointLight.getConstant());
    }

    public void setUniform(String uniformName, SpotLight spotLight)
    {
        setUniform(uniformName+".cutoff", spotLight.getCutoff());
        setUniform(uniformName+".pl", spotLight.getPointLight());
        setUniform(uniformName+".conedir", spotLight.getConeDirection());
    }

    public void setUniform(String uniformName, PointLight[] pointLights)
    {
        int numLights = pointLights != null ? pointLights.length : 0;
        for(int i = 0; i < numLights; i++)
        {
            setUniform(uniformName,pointLights[i], i);
        }
    }

    public void setUniform(String uniformName, PointLight pointLight, int pos)
    {
        setUniform(uniformName + "[" + pos + "]", pointLight);
    }

    public void setUniform(String uniformName, SpotLight[] spotLights)
    {
        int numLights = spotLights != null ? spotLights.length : 0;
        for(int i = 0; i < numLights; i++)
        {
            setUniform(uniformName,spotLights[i], i);
        }
    }

    public void setUniform(String uniformName, SpotLight spotLight, int pos)
    {
        setUniform(uniformName + "[" + pos + "]", spotLight);
    }

    public void setUniform( String uniformname,int value)
    {
        GL20.glUniform1i(uniforms.get(uniformname),value);
    }

    public void setUniform( String uniformname,float value)
    {
        GL20.glUniform1f(uniforms.get(uniformname), value);
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
