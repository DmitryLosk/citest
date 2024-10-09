pipeline {
    agent any
    environment {
        DB_FILE = 'scripts.sql'
        DB_HOST = 'mysql-rfam-public.ebi.ac.uk'
        DB_USER = 'rfamro'
        DB_PORT = '4497'
        DB_NAME = 'Rfam'
    }

    stages {
        stage('Checkout') {
            steps {
                cleanWs()
                checkout scm
            }
        }
        stage('Run SQL Scripts') {
            steps {
                script {
// Выполнение SQL скриптов
                    sh "sudo apt install mysql-server"
                    sh """
                    mysql --user=${DB_USER} --host=${DB_HOST} --port=${DB_PORT} --database=${DB_NAME} < ${DB_FILE} > result.txt
                    cat result.txt
"""
                }
            }
        }
        stage('Archive Results') {
            steps {
// Архивирование результатов
                archiveArtifacts artifacts: 'result.txt', allowEmptyArchive: true
            }
        }
    }
    post {
        always {
// Вывод результатов в консоль
            if (fileExists('result.txt')){
                echo readFile('result.txt')
            }
        }
    }
}