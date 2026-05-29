pipeline {
    agent any

    parameters {
        string(name: 'DOCKER_HUB_USER', defaultValue: 'shabb', description: 'Docker Hub Username')
        string(name: 'IMAGE_NAME', defaultValue: 'devops-java-app', description: 'Docker Image Name')
        string(name: 'K8S_NAMESPACE', defaultValue: 'default', description: 'Kubernetes Namespace')
    }

    environment {
        DOCKER_HUB_CREDS = credentials('docker-hub-credentials-id') // Jenkins Credentials ID
        KUBECONFIG_CREDS = credentials('kubeconfig-credentials-id') // Jenkins Credentials ID for Kubernetes Kubeconfig
        VERSION          = "1.0.${BUILD_NUMBER}"
    }

    stages {
        stage('Checkout Source') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build & Lint') {
            steps {
                echo 'Compiling the application...'
                bat 'mvn clean compile'
            }
        }

        stage('Execute Tests') {
            steps {
                echo 'Running unit and integration tests...'
                bat 'mvn test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Dockerize Image') {
            steps {
                echo 'Packaging JAR file...'
                bat 'mvn package -DskipTests'
                
                echo "Building Docker image: ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION}..."
                bat "docker build -t ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION} ."
                bat "docker tag ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION} ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        stage('Push to Registry') {
            steps {
                echo 'Logging in to Docker Hub...'
                bat "docker login -u ${DOCKER_HUB_CREDS_USR} -p ${DOCKER_HUB_CREDS_PSW}"
                
                echo 'Pushing Docker images...'
                bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION}"
                bat "docker push ${DOCKER_HUB_USER}/${IMAGE_NAME}:latest"
            }
        }

        stage('Kubernetes Deploy') {
            steps {
                echo 'Configuring Kubeconfig and deploying to Kubernetes Cluster...'
                withEnv(["KUBECONFIG=${env.WORKSPACE}/.kubeconfig"]) {
                    // Write the credentials file for kubectl
                    bat "echo ${KUBECONFIG_CREDS} > .kubeconfig"
                    
                    // Deploy MySQL Database first
                    bat "kubectl apply -f k8s/mysql-deployment.yaml --namespace=${K8S_NAMESPACE}"
                    
                    // Deploy Application Service and Deployment configs
                    bat "kubectl apply -f k8s/app-service.yaml --namespace=${K8S_NAMESPACE}"
                    bat "kubectl apply -f k8s/app-deployment.yaml --namespace=${K8S_NAMESPACE}"
                    
                    // Trigger a rolling update by updating the container image
                    bat "kubectl set image deployment/employee-app-deployment employee-app=${DOCKER_HUB_USER}/${IMAGE_NAME}:${VERSION} --namespace=${K8S_NAMESPACE}"
                    
                    // Track rolling update status
                    bat "kubectl rollout status deployment/employee-app-deployment --namespace=${K8S_NAMESPACE}"
                }
            }
        }
    }

    post {
        success {
            echo "CI/CD Pipeline successfully executed for build ${BUILD_NUMBER}!"
        }
        failure {
            echo "CI/CD Pipeline failed for build ${BUILD_NUMBER}. Please check logs."
        }
        cleanup {
            echo 'Cleaning up workspaces and temp files...'
            deleteDir()
        }
    }
}
