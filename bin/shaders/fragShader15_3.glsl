#version 430

// Inputs từ vertex shader
in vec2 tc;  // Tọa độ UV để vẽ bàn cờ
in vec3 varyingNormal;      // Vector pháp tuyến
in vec3 varyingLightDir;    // Vector hướng ánh sáng
in vec3 varyingVertPos;     // Vị trí đỉnh
in vec3 varyingHalfVector;  // Vector trung bình giữa hướng nhìn và ánh sáng

// Output của fragment shader
out vec4 fragColor;

// Struct định nghĩa ánh sáng vị trí
struct PositionalLight {
    vec4 ambient;  
    vec4 diffuse;  
    vec4 specular;  
    vec3 position;
};

// Struct định nghĩa vật liệu
struct Material {
    vec4 ambient;  
    vec4 diffuse;  
    vec4 specular;  
    float shininess;
};

// Uniforms
uniform vec4 globalAmbient;       // Ánh sáng toàn cục
uniform PositionalLight light;    // Thông tin ánh sáng
uniform Material material;        // Thông tin vật liệu
uniform mat4 v_matrix;            // Ma trận view

// Hàm tạo hiệu ứng bàn cờ
vec3 checkerboard(vec2 tc) { 
    float tileScale = 64.0;  // Kích thước ô bàn cờ
    float tile = mod(floor(tc.x * tileScale) + floor(tc.y * tileScale), 2.0);
    return tile * vec3(1.0, 1.0, 1.0);  // Màu trắng cho ô sáng, đen cho ô tối
}

void main(void) {
    // Tính ánh sáng Phong (ADS)

    // Chuẩn hóa các vector
    vec3 L = normalize(varyingLightDir);                  // Vector ánh sáng
    vec3 N = normalize(varyingNormal);                   // Vector pháp tuyến
    vec3 V = normalize(-v_matrix[3].xyz - varyingVertPos); // Vector từ camera đến bề mặt
    vec3 H = normalize(varyingHalfVector);               // Vector trung bình

    // Tính góc giữa vector ánh sáng và pháp tuyến
    float cosTheta = max(dot(L, N), 0.0);
    // Tính góc giữa vector phản xạ và hướng nhìn
    float cosPhi = pow(max(dot(H, N), 0.0), material.shininess);

    // Thành phần ánh sáng
    vec3 ambient = ((globalAmbient * material.ambient) + (light.ambient * material.ambient)).xyz;
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * cosTheta;
    vec3 specular = light.specular.xyz * material.specular.xyz * cosPhi;

    // Kết hợp màu bàn cờ và ánh sáng
    vec3 checkerColor = checkerboard(tc);  // Màu từ bàn cờ
    vec3 lighting = ambient + diffuse + specular;  // Màu từ ánh sáng

    // Kết hợp ánh sáng với bàn cờ
    vec3 finalColor = checkerColor * lighting;

    // Gán giá trị cho fragment
    fragColor = vec4(finalColor, 1.0);
}
