package game_engine_opengl2.utils;

import java.io.InputStream;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import org.lwjgl.system.MemoryUtil;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data)
    {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);//allocating memory to the buffer of the size of data 
        buffer.put(data).flip();//changing buffer from write to read mode with flip()
        return buffer;
    }

    public static IntBuffer storeDataInIntBuffer(int[] data)
    {
        IntBuffer buffer = MemoryUtil.memAllocInt(data.length);//allocating memory to the buffer of the size of data 
        buffer.put(data).flip();//changing buffer from write to read mode with flip()
        return buffer;
    }

    public static String loadResource(String filename) throws Exception
    {
        String result;
        try(InputStream in = Utils.class.getResourceAsStream(filename);)
        {
            result = new String(in.readAllBytes(),StandardCharsets.UTF_8);
        }
        return result;
    }
}
