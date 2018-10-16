@Library('jenkins-commons') _
import lu.luxtrust.jenkins.util.DeployUtil
import lu.luxtrust.jenkins.util.EnvVariables

pipeline {
    environment {
        flowersMailingList = "vladyslav.tkachuk-ext@luxtrust.lu;dmytro.lysikov@excelian.com;ivan.skrypka@excelian.com;" +
                "yevhen.korobov@excelian.com;oksana.moroziuk@excelian.com;anastasiia.kyselova@excelian.com;alexandre.squelin@luxtrust.lu"
        host = getEnvHost(BRANCH_NAME)
        envName = getEnvName(BRANCH_NAME)
    }

    agent {label 'master'}

    parameters {
        booleanParam(defaultValue: true, description: 'Execute API tests. Default value is true', name: 'executeAPITests')
    }

    options {
        skipDefaultCheckout(true)
        buildDiscarder(logRotator(numToKeepStr: '10', artifactNumToKeepStr: '10'))
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh "echo Building ${BRANCH_NAME}..."
                withMaven(
                        maven: 'Maven 3.5.2',
                ) {
                    script {
                        //rename profiles per env name
                        sh "mvn clean package -DskipTests -Denv.type=${envName} -Dbuild.war=true -Dui.build=${envName}"
                    }
                }
            }
        }

        stage('Run Tests') {
            steps {
                sh "echo Running tests ${BRANCH_NAME}..."
                withMaven(
                        maven: 'Maven 3.5.2',
                ) {
                    sh "mvn test -Dtest=*Test -DfailIfNoTests=false"
                }
            }
        }

        stage('Run API Tests') {
           when {
                 expression {
                     return params.executeAPITests;
                 }
            }
            steps {
                 sh "echo Running tests ${BRANCH_NAME}..."
                 withMaven(
                        maven: 'Maven 3.5.2',
                 ) {
                    sh "mvn test -Dtest=*TestAPI -DfailIfNoTests=false"
                 }
            }
        }

        stage('Artifacts Upload'){

            steps {
                sh "echo deploying artifacts to repo from ${BRANCH_NAME} ..."
                withMaven(
                        maven: 'Maven 3.5.2'
                ) {
                    sh "mvn deploy"
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    sh "scp ./flowers-api/target/flowers.war ${host}:/opt/flowers/install-packages/EARs/flowers.war"
                    sh "ssh ${host} -- sudo -u flowers -H /opt/flowers/install-packages/install_webapp_${envName}.sh"
                }
            }
        }
    }

    post {
        success {
            notifyBuildSuccessful(flowersMailingList)
        }
        failure {
            notifyBuildFailed(flowersMailingList)
        }
    }
}

private void notifyBuildSuccessful(mailList) {
    if (currentBuild?.getPreviousBuild()?.result == 'FAILURE') {
        if (currentBuild.resultIsBetterOrEqualTo(currentBuild.getPreviousBuild().result)) {
            notifyIfBuildIsBackToNormal(mailList)
        }
    }
    if (DeployUtil.isMasterBranch(BRANCH_NAME) && currentBuild?.result == 'SUCCESS') {
        notifyNewRelease(mailList)
    }
}

private void notifyNewRelease(mailList) {
    emailext(
            to: mailList,
            subject: "SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            body: "SUCCESS: New verion of '${env.JOB_NAME}' was released"
    )
}

private void notifyIfBuildIsBackToNormal(mailList) {
    emailext(
            to: mailList,
            subject: "SUCCESS: Job '${env.JOB_NAME} is back to normal",
            body: """SUCCESS: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}] is back to normal'"""
    )
}

private void notifyBuildFailed(mailList) {
    emailext(
            to: mailList,
            subject: "FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]'",
            body: """FAILED: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]': \nCheck console output at ${env.BUILD_URL}"""
    )
}

private static String getEnvHost(branchName) {
    EnvVariables envVariables = DeployUtil.getEnvVariables(DeployUtil.FLOWERS, branchName)
    return envVariables.host
}

private static String getEnvName(branchName) {
    EnvVariables envVariables = DeployUtil.getEnvVariables(DeployUtil.FLOWERS, branchName)
    return envVariables.envName
}
