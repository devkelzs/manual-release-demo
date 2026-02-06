pipeline {
    agent any

    tools {
        jdk 'jdk17'
        maven 'maven3'
    }

    stages {

        // ------------------------------
        stage('Check OS') {
            steps {
                sh 'uname -a || echo Windows detected'
            }
        }

        // ------------------------------
        stage('Checkout Code') {
            steps {
                echo 'Checking out source code...'
                checkout scm
            }
        }

        // ------------------------------
        stage('Build') {
            steps {
                echo 'Building the application with Maven...'
                sh 'mvn clean package'
            }
        }

        // ------------------------------
        stage('Test Artifact') {
            steps {
                echo 'Verifying JAR exists...'
                sh 'ls -l target'
            }
        }

        // ------------------------------
        stage('Publish Artifact') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version

                    echo "Publishing artifact version ${version} to Artifactory simulation"

                    sh """
                        mkdir -p ${env.HOME}/artifactory/com/example/employee-api/${version}
                        cp target/employee-api-${version}.jar ${env.HOME}/artifactory/com/example/employee-api/${version}/
                    """
                }
            }
        }

        // ------------------------------
        stage('Deploy to TEST') {
            steps {
                script {
                    def pom = readMavenPom file: 'pom.xml'
                    def version = pom.version
                    def artifactPath = "${env.HOME}/artifactory/com/example/employee-api/${version}/employee-api-${version}.jar"
                    def testEnvPath = "${env.HOME}/environments/test"
                    def testPort = 8082 
                    echo "Deploying version ${version} to TEST"

                    sh """
                        echo "Stopping any running TEST instance..."
                        PID=\$(ps -ef | grep employee-api | grep -v grep | awk '{print \$2}' | head -n 1)
                        if [ -n "\$PID" ]; then
                            echo "Stopping existing process with PID \$PID"
                            kill -9 \$PID
                        else
                            echo "No existing TEST process found"
                        fi

                        echo "Copying artifact to TEST environment..."
                        mkdir -p ${testEnvPath}
                        cp ${artifactPath} ${testEnvPath}/

                        # Start application safely in background
                        nohup java -jar /var/lib/jenkins/environments/test/employee-api-1.0.0.jar \
                            --server.address=0.0.0.0 --server.port=8082 > /var/lib/jenkins/environments/test/test.log 2>&1 &

                    """
                }
            }
        }

        stage('Deploy to UAT') {
    steps {
        script {
            def pom = readMavenPom file: 'pom.xml'
            def version = pom.version
            def artifactPath = "${env.HOME}/artifactory/com/example/employee-api/${version}/employee-api-${version}.jar"
            def uatEnvPath = "${env.HOME}/environments/uat"
            def uatPort = "8083"

            // Manual approval step
            input message: "Approve deployment to UAT?", ok: "Deploy"

            echo "Deploying version ${version} to UAT"

            sh """
                echo "Stopping any existing UAT instance..."
                PID=\$(ps -ef | grep employee-api | grep -v grep | awk '{print \$2}' | head -n 1)
                if [ -n "\$PID" ]; then
                    echo "Stopping existing process with PID \$PID"
                    kill -9 \$PID
                else
                    echo "No existing UAT process found"
                fi

                echo "Copying artifact to UAT environment..."
                mkdir -p ${uatEnvPath}
                cp ${artifactPath} ${uatEnvPath}/

                echo "Starting application in UAT..."
                sudo -u jenkins nohup java -jar /var/lib/jenkins/environments/uat/employee-api-1.0.0.jar \
                 --server.address=0.0.0.0 \
                 --server.port=8083 \
                 > /var/lib/jenkins/environments/uat/uat.log 2>&1 &
            """
        }
    }
}


    } // stages

    post {
        success {
            echo 'Build and TEST deployment successful 🎉'
        }
        failure {
            echo 'Build or TEST deployment failed ❌'
        }
    }
}
