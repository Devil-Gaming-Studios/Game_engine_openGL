package game_engine_opengl2;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL11;

import entity.Model;
import game_engine_opengl2.utils.Utils;

public class ObjectLoader {
    private List<Integer> vbos = new ArrayList<>();
    private List<Integer> vaos = new ArrayList<>();

    public Model loadModel(float[] vertices)
    {
        int id =  createVAO();
        storeDataInAttribList(0, 3, vertices);
        unbind();
        return new Model(id,vertices.length/3);
    }

    private int createVAO()
    {
        int id = GL30.glGenVertexArrays();
        vaos.add(id);
        GL30.glBindVertexArray(id);
        return id;

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

    }
}
