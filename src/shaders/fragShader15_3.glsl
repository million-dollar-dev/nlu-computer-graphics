#version 430

// Đầu vào từ vertex shader
in vec2 tc;                  // Tọa độ UV cho hiệu ứng bàn cờ
in vec3 varyingNormal;       // Vector pháp tuyến sau khi biến đổi
in vec3 varyingLightDir;     // Vector hướng ánh sáng
in vec3 varyingVertPos;      // Vị trí đỉnh trong không gian thế giới
in vec3 varyingViewDir;      // Vector hướng từ camera tới đỉnh

// Đầu ra màu sắc
out vec4 fragColor;

// Cấu trúc ánh sáng
struct PositionalLight {
    vec4 ambient;  
    vec4 diffuse;  
    vec4 specular;  
    vec3 position;
};

// Cấu trúc vật liệu
struct Material {
    vec4 ambient;  
    vec4 diffuse;  
    vec4 specular;  
    float shininess;
};

// Uniforms
uniform vec4 globalAmbient;  // Ánh sáng môi trường toàn cục
uniform PositionalLight light;
uniform Material material;

// Hàm tạo hiệu ứng bàn cờ
vec3 checkerboard(vec2 tc) {
    float tileScale = 10.0;  // Kích thước ô bàn cờ
    float tile = mod(floor(tc.x * tileScale) + floor(tc.y * tileScale), 2.0);
    return mix(vec3(1.0, 1.0, 1.0), vec3(0.0, 0.0, 0.0), tile);
}

// Main function
void main(void) {
    // Normal hóa các vector
    vec3 N = normalize(varyingNormal);
    vec3 L = normalize(varyingLightDir);
    vec3 V = normalize(varyingViewDir);
    vec3 H = normalize(L + V);  // Vector trung bình

    // Tính toán các thành phần ánh sáng
    vec3 ambient = (globalAmbient.xyz * material.ambient.xyz) + 
                   (light.ambient.xyz * material.ambient.xyz);

    float diff = max(dot(N, L), 0.0);
    vec3 diffuse = light.diffuse.xyz * material.diffuse.xyz * diff;

    float spec = pow(max(dot(N, H), 0.0), material.shininess);
    vec3 specular = light.specular.xyz * material.specular.xyz * spec;

    // Kết hợp các thành phần ánh sáng
    vec3 lighting = ambient + diffuse + specular;

    // Kết hợp hiệu ứng bàn cờ
    vec3 surfaceColor = checkerboard(tc);
    vec3 finalColor = surfaceColor * lighting;

    // Gán giá trị màu sắc cuối cùng
    fragColor = vec4(finalColor, 1.0);
}
