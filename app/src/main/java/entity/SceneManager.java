package entity;

import java.util.ArrayList;
import java.util.List;

import org.joml.Vector3f;

import entity.terrain.Terrain;
import game_engine_opengl2.Consts;
import game_engine_opengl2.lighting.DirectionalLight;
import game_engine_opengl2.lighting.PointLight;
import game_engine_opengl2.lighting.SpotLight;

public class SceneManager {
    private List<Entity> entities;
    private final List<Terrain> terrains;
    private Vector3f ambientLight;
    private SpotLight[] spotLights;
    private PointLight[] pointLights;
    private DirectionalLight directionalLight;
    private float lightAngle;
    private float spotAngle = 0;
    private float spotInc = 1;

    public SceneManager(float lightAngle)
    {
        entities = new ArrayList<>();
        terrains = new ArrayList<>();
        ambientLight = Consts.AMBIENT_LIGHT;
        this.lightAngle = lightAngle;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public void addEntity(Entity entity)
    {
        this.entities.add(entity);
    }

    public Vector3f getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(Vector3f ambientLight) {
        this.ambientLight = ambientLight;
    }

    public void setAmbientLight(float x,float y, float z) {
        this.ambientLight = new Vector3f(x,y,z);
    }

    public SpotLight[] getSpotLights() {
        return spotLights;
    }

    public void setSpotLights(SpotLight[] spotLights) {
        this.spotLights = spotLights;
    }

    public PointLight[] getPointLights() {
        return pointLights;
    }

    public void setPointLights(PointLight[] pointLights) {
        this.pointLights = pointLights;
    }

    public DirectionalLight getDirectionalLight() {
        return directionalLight;
    }

    public void setDirectionalLight(DirectionalLight directionalLight) {
        this.directionalLight = directionalLight;
    }

    public float getLightAngle() {
        return lightAngle;
    }

    public void setLightAngle(float lightAngle) {
        this.lightAngle = lightAngle;
    }

    public float getSpotAngle() {
        return spotAngle;
    }

    public void setSpotAngle(float spotAngle) {
        this.spotAngle = spotAngle;
    }

    public float getSpotInc() {
        return spotInc;
    }

    public void setSpotInc(float spotInc) {
        this.spotInc = spotInc;
    }

    public List<Terrain> getTerrains() {
        return terrains;
    }

    public void addTerrains(Terrain terrain) {
        this.terrains.add(terrain);
    }

    public void incLightAngle(float inc)
    {
        this.lightAngle += inc;
    }

    public void incSpotAngle(float inc)
    {
        this.spotAngle *= inc;
    }
}
