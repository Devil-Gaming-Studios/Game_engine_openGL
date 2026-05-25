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

import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector3f;
import org.joml.Vector3i;
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


    public Model loadOBJModel(String fileName)
    {
        List<String> lines = Utils.readAllLines(fileName);

        List<Vector3f> vertices = new ArrayList<>();
        List<Vector3f> normals = new ArrayList<>();
        List<Vector2f> texture = new ArrayList<>();
        List<Vector3i> faces = new ArrayList<>();

        for(String line : lines)
        {
            String[] tokens = line.split("\\s");
            switch(tokens[0])
            {
                case "v":
                    //vertices
                    Vector3f verticesVec = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    vertices.add(verticesVec);
                    break;
                case "vt":
                    //vertex texture
                    Vector2f textureVec = new Vector2f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2])
                    );
                    texture.add(textureVec);
                    break;
                case "vn":
                    //vertices normal
                    Vector3f normalsVec = new Vector3f(
                        Float.parseFloat(tokens[1]),
                        Float.parseFloat(tokens[2]),
                        Float.parseFloat(tokens[3])
                    );
                    normals.add(normalsVec);
                    break;
                case "f":
                    processFace(tokens[1], faces);
                    processFace(tokens[2], faces);
                    processFace(tokens[3], faces);
                    // Triangle 2: v1, v3, v4  (fan triangulation)
                    if(tokens.length == 5)
                    {
                        processFace(tokens[1], faces);
                        processFace(tokens[3], faces);
                        processFace(tokens[4], faces);
                    }
                    break;
                default:
                    break;
            }
        }
        List<Integer> indices = new ArrayList<>();
        float[] verticesArr = new float[vertices.size() * 3];
        int i =0;
        for(Vector3f pos : vertices)
        {
            verticesArr[i * 3] = pos.x;
            verticesArr[i * 3 + 1] = pos.y;
            verticesArr[i * 3 + 2] = pos.z;
            i++;
        }

        float[] textCoordArr = new float[vertices.size() * 2];
        float[] normalArr = new float[vertices.size() * 3];

        for(Vector3i face : faces)
        {
            processVertex(face.x, face.y, face.z, texture ,normals, indices, textCoordArr, normalArr );
        } 

        int[] indicesArr = indices.stream().mapToInt((Integer v) -> v).toArray();

        return loadModel(verticesArr, textCoordArr, indicesArr);
    }
    
    private static void processVertex(int pos, int textCoord, int normal, List<Vector2f> textCoordList, List<Vector3f> normalList, List<Integer> indicesList, float[] textCoordArr, float[] normalArr)
    {
        indicesList.add(pos);

        if(textCoord >= 0)
        {
            Vector2f textCoordVec = textCoordList.get(textCoord);
            textCoordArr[pos * 2] = textCoordVec.x;
            textCoordArr[pos * 2 + 1] = 1 - textCoordVec.y;
        }

        if(normal >= 0)
        {
            Vector3f normalVec = normalList.get(normal);
            normalArr[pos * 3] = normalVec.x;
            normalArr[pos * 3 + 1] = normalVec.y;
            normalArr[pos * 3 + 2] = normalVec.z;

        }
    }

    private static void processFace(String token, List<Vector3i> faces)
    {
        String[] lineToken = token.split("/");
        int length = lineToken.length;
        int pos = -1, coords = -1, normal = -1;
        pos = Integer.parseInt(lineToken[0]) -1;
        if(length > 1)
        {
            String textCoord = lineToken[1];
            coords = textCoord.length() > 0? Integer.parseInt(textCoord) -1 : -1;
            if(length > 2)
                normal = Integer.parseInt(lineToken[2]) - 1;
        }
        Vector3i facesVec = new Vector3i(pos, coords, normal);
        faces.add(facesVec);
    }

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

     public Model loadResourceModel(String resourcePath) throws Exception
    {
        try(InputStream in = ObjectLoader.class.getResourceAsStream(resourcePath))
        {
            if(in == null)
            {
                // Create a placeholder default cube if resource not found
                return createPlaceholderModel();
            }
            
            Path tempFile = Files.createTempFile("Obj_", ".obj");
            Files.copy(in, tempFile, StandardCopyOption.REPLACE_EXISTING);
            tempFile.toFile().deleteOnExit();
            
            return loadOBJModel(tempFile.toAbsolutePath().toString());
        }
    }

    private Model createPlaceholderModel()
    {
                float[] vertices = new float[] {
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, 0.5f, -0.5f,
            0.5f, 0.5f, -0.5f,
            -0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, 0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
            -0.5f, 0.5f, 0.5f,
            -0.5f, -0.5f, 0.5f,
            -0.5f, -0.5f, -0.5f,
            0.5f, -0.5f, -0.5f,
            -0.5f, -0.5f, 0.5f,
            0.5f, -0.5f, 0.5f,
        };
        float[] textureCoords = new float[]{
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
                    0.0f, 0.0f,
                    0.5f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.0f, 0.5f,
                    0.5f, 0.5f,
                    0.0f, 1.0f,
                    0.5f, 1.0f,
                    0.0f, 0.0f,
                    0.0f, 0.5f,
                    0.5f, 0.0f,
                    0.5f, 0.5f,
                    0.5f, 0.0f,
                    1.0f, 0.0f,
                    0.5f, 0.5f,
                    1.0f, 0.5f,
        };
        int[] indices = new int[]{
                    0, 1, 3, 3, 1, 2,
                    8, 10, 11, 9, 8, 11,
                    12, 13, 7, 5, 12, 7,
                    14, 15, 6, 4, 14, 6,
                    16, 18, 19, 17, 16, 19,
                    4, 6, 7, 5, 4, 7,
        };
        return loadModel(vertices, textureCoords, indices);
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
