package game_engine_opengl2.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
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

    public static List<String> readAllLines(String fileName)
    {
        List<String> list = new ArrayList<String>();
        try
        {
            // Try to read as a file system path first
            if(Files.exists(Paths.get(fileName)))
            {
                return Files.readAllLines(Paths.get(fileName));
            }
            
            // Otherwise, try as a classpath resource
            try(BufferedReader br = new BufferedReader(new InputStreamReader(Class.forName(Utils.class.getName()).getResourceAsStream(fileName))))
            {
                String line;
                while((line = br.readLine()) != null)
                {
                    list.add(line);
                }
            }
        }
        catch(IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }

        return list;
    }
}
