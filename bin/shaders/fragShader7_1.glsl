#version 430
in vec4 varyingColor;
out vec4 fragColor;
//  uniforms match those in the vertex shader,
//  but are not used directly in this fragment shader
struct PositionalLight
{ 
vec4 ambient;  
vec4 diffuse;  
vec4 specular;  
vec3 position;
};
struct Material
{ 
vec4 ambient;  
vec4 diffuse;  
vec4 specular;  
float shininess;
};
uniform vec4 globalAmbient;
uniform PositionalLight light;
uniform Material material;
uniform mat4 m_matrix;
uniform mat4 v_matrix;
uniform mat4 p_matrix;
uniform mat4 norm_matrix;
void main(void)
{ 
fragColor = varyingColor;
}
