pipeline {
    agent {
        docker {
            image 'wpilib/roborio-cross-ubuntu:2019-18.04'
            args '-v /root/.m2:/root/.m2'
        }
    }
    stages {
        stage('build') {
            steps {
                sh './gradlew build'
            }
        }
    }
}
