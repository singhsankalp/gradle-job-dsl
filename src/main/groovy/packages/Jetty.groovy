package packages

import utils.Scm
import utils.LogRotate
import utils.Slack
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class JettyPackage implements Packer {

  String app
  String downstream
  String scm

  Job Package(DslFactory dslFactory) {

      dslFactory.job("${app}/Package") {

        LogRotate.integrate delegate
        scm {
          Scm.git delegate, "${this.scm}"
        }
        triggers {
          scm('* * * * *')
        }
        label('slave')
        steps {
          maven {
            mavenInstallation('maven_3.3.9')
            rootPOM('pom.xml')
            goals('clean install -DskipTests=true')
          }
         shell('mv backend/target/*.war ./gojek-$BUILD_NUMBER.war')
         shell('echo $BUILD_NUMBER > latest')
        }
        publishers {
          Slack.integrate delegate
          downstream("${this.app}/${this.downstream}", 'SUCCESS')
          archiveArtifacts {
            pattern("gojek*.war")
            pattern("latest")
            onlyIfSuccessful()
          }
        }
    }
  }
}
