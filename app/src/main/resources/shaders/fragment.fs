#version 400 core

in vec2 fragTextureCoord;
in vec3 fragNormal;
in vec3 fragPos;

out vec4 fragColour;

struct Material
{
    vec4 ambient;
    vec4 diffuse;
    vec4 specular;
    int hasTexture;
    float reflectance;
};

uniform sampler2D textureSampler;
uniform vec3 ambientLight;
uniform Material material;

vec4 ambientC;
vec4 diffuseC;
vec4 specularC;

void setupColours(Material material, vec2 textCoords)
{
    if(material.hasTexture == 1)
    {
        ambientC = texture(textureSampler, textCoords);
        diffuseC = ambientC;
        specularC = ambientC;
    }
    else
    {
        ambientC = material.ambient;
        diffuseC = material.diffuse;
        specularC = material.specular;
    }
}

void main()
{
    //setupColours(material, fragTextureCoord);

    if(material.hasTexture == 1)
    {
        ambientC = texture(textureSampler, fragTextureCoord);
    }
    else
    {
        ambientC = material.ambient + material.specular + material.diffuse;
    }
    fragColour = ambientC * vec4(ambientLight,1);
    //fragColour = texture(textureSampler, fragTextureCoord);
}