package entity.terrain;

public class BlendMapTerrain {
    TerrainTexture background, redTexture, blueTexture, greenTexture;

    public BlendMapTerrain(TerrainTexture background, TerrainTexture blueTexture, TerrainTexture greenTexture, TerrainTexture redTexture) {
        this.background = background;
        this.blueTexture = blueTexture;
        this.greenTexture = greenTexture;
        this.redTexture = redTexture;
    }

    public TerrainTexture getBackground()
    {
        return background;
    }

    public TerrainTexture getRedTexture() {
        return redTexture;
    }

    public TerrainTexture getBlueTexture() {
        return blueTexture;
    }

    public TerrainTexture getGreenTexture() {
        return greenTexture;
    }

}

