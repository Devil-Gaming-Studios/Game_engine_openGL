package game_engine_opengl2;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import entity.Model;
import game_engine_opengl2.utils.Utils;

public class ObjectLoader {
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> vaos = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadModel(float[] vertices,float[] textureCoords, int[] indices)
    {
        int id =  createVAO();
        storeIndicesBuffer(indices);
        storeDataInAttribList(0, 3, vertices);
        storeDataInAttribList(1, 2, textureCoords);
        unbind();
        return new Model(id,indices.length);
    }

    public int loadTexture(String filename) throws Exception
    {
        int height,width;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush())//create a new stack frame 
        {
            IntBuffer w = stack.mallocInt(1);//allocate memory for 1 integer
            IntBuffer h = stack.mallocInt(1);
            IntBuffer c = stack.mallocInt(1);

            buffer = STBImage.stbi_load(filename, w, h, c, 4);
            if(buffer == null)
                throw new Exception("Image File "+filename+" not loaded " + STBImage.stbi_failure_reason());

            width = w.get();
            height = h.get();

        }
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D,id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT,1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA,width, height,0,GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);//transfers textures from RAM to VRAM 
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);//creates a smaller versions of the texture
        STBImage.stbi_image_free(buffer);//free the image from the RAM 
        return id;
    }

    public int loadResourceTexture(String resourcePath) throws Exception
    {
        try(InputStream in = ObjectLoader.class.getResourceAsStream(resourcePath))
        {
            if(in == null)
            {
                // Create a placeholder green texture if resource not found
                return createPlaceholderTexture();
            }
            
            Path tempFile = Files.createTempFile("texture_", ".png");
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            
            return loadTexture(tempFile.toAbsolutePath().toString());
        }
    }

    private int createPlaceholderTexture()
    {
        // Create a simple 32x32 green texture as placeholder
        int width = 32;
        int height = 32;
        ByteBuffer buffer = org.lwjgl.system.MemoryUtil.memAlloc(width * height * 4);
        
        for(int i = 0; i < width * height * 4; i += 4)
        {
            buffer.put(i, (byte) 0);      // R
            buffer.put(i + 1, (byte) 255); // G
            buffer.put(i + 2, (byte) 0);   // B
            buffer.put(i + 3, (byte) 255); // A
        }
        
        buffer.flip();
        
        int id = GL11.glGenTextures();
        textures.add(id);
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id);
        GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1);
        GL11.glTexImage2D(GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
        GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D);
        org.lwjgl.system.MemoryUtil.memFree(buffer);
        
        return id;
    }

    private int createVAO()
    {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;

    }

    private void storeIndicesBuffer(int[] indices)
    {
        int vbo = GL15.glGenBuffers();
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vbo);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, buffer, GL15.GL_STATIC_DRAW);
    }

    private void storeDataInAttribList(int attribNo, int vertexCount, float[] data)
    {
        int vbo = GL15.glGenBuffers();//creates the empty buffer
        vbos.add(vbo);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);//selects the buffer (GL15.GL_ARRAY_BUFFER specifies the type of buffer) & (vbo is which buffer to select)
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, buffer,  GL15.GL_STATIC_DRAW);//stores the buffer in GPU memory
        //STATIC_DRAW takes data one time to GPU and do not updates it 
        GL20.glVertexAttribPointer(attribNo, vertexCount, GL11.GL_FLOAT, false, 0,0);//upload data in the currenty bound buffer
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER,0);//unbind the buffer

    }
    private void unbind()
    {
        GL30.glBindVertexArray(0);
    }
    public void cleanup()
    {
        for(int vao : vaos)
            GL30.glDeleteVertexArrays(vao);
        for(int vbo : vbos)
            GL30.glDeleteBuffers(vbo);
        for(int texture : textures)
        {
            GL30.glDeleteTextures(texture);
        }

    }
}
