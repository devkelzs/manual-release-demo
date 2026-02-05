pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {

        stage('Check OS') {
            steps {
                sh 'uname -a || echo Windows detected'
            }
        }

        stage('Checkout Code') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building the application with Maven...'
                sh 'mvn clean package'
            }
        }

        stage('Test Artifact') {
            steps {
                echo 'Verifying JAR exists...'
                sh 'ls -l target'
            }
        }

        stage('Publish Artifact') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version

                    echo "Publishing artifact version ${version} to Artifactory simulation"

                    sh "mkdir -p ${env.HOME}/artifactory/com/example/employee-api/${version}"
                    sh "cp target/employee-api-${version}.jar ${env.HOME}/artifactory/com/example/employee-api/${version}/"
                }
            }
        }

        stage('Deploy to TEST') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def artifactPath = "${env.HOME}/artifactory/com/example/employee-api/${version}/employee-api-${version}.jar"
                    def testEnvPath = "${env.HOME}/environments/test"

                    echo "Deploying version ${version} to TEST"

                    sh """
                        echo "Stopping any existing TEST instance"
                        pkill -f employee-api || true

                        echo "Copying artifact to TEST environment"
                        cp ${artifactPath} ${testEnvPath}/

                        echo "Starting application in TEST"
                        nohup java -jar ${testEnvPath}/employee-api-${version}.jar \
                          --server.port=8081 > ${testEnvPath}/test.log 2>&1 &
                    """
                }
            }
        }
    }

    post {
        success {
            echo 'Build successful 🎉'
        }
        failure {
            echo 'Build failed ❌'
        }
    }
}
