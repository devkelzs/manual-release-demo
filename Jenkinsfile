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
                    // Read version from pom.xml
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version

                    echo "Publishing artifact version ${version} to Artifactory simulation"

                    // Create versioned folder
                    sh "mkdir -p ~/artifactory/com/example/employee-api/${version}"

                    // Copy JAR
                    sh "cp target/employee-api-${version}.jar ~/artifactory/com/example/employee-api/${version}/"
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
