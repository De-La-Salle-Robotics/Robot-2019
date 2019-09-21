pipeline {
    agent {
        docker {
            image 'wpilib/roborio-cross-ubuntu:2019-18.04'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('Build and Test') {
            steps {
                sh './gradlew build'
            }
            post {
                always {
                    junit 'build/test-restuls/*.xml'
                }
            }
        }
    }
}
