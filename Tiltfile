# Build
custom_build(
    # Name of the container image
    ref = 'catalog-service',
    # Command to build the container image
    command = './gradlew bootBuildImage --imageName $EXPECTED_REF',
    # 새로운 빌드를 시작하기 위해 지켜봐야 하는 파일
    deps = ['build.gradle', 'src']
)

# Deploy
k8s_yaml(['k8s/deployment.yaml','k8s/service.yaml'])

# Manage
k8s_resource('catalog-service', port_forwards=['9001'])