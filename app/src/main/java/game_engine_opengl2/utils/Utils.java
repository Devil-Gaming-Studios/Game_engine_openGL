package game_engine_opengl2.utils;

import java.nio.FloatBuffer;

import org.lwjgl.system.MemoryUtil;

public class Utils {
    public static FloatBuffer storeDataInFloatBuffer(float[] data)
    {
        FloatBuffer buffer = MemoryUtil.memAllocFloat(data.length);//allocating memory to the buffer of the size of data 
        buffer.put(data).flip();//changing buffer from write to read mode with flip()
        return buffer;
    }
}
