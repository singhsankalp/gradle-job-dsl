package docs

import helpers.*
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class AppDocs implements DeployDocs {

  String app
  String location

  Job Setup(DslFactory dslFactory) {
    dslFactory.job("${location}/DeployDocs") {

      String service_name = app.replaceAll('_','-')
      label('slave')
      LogRotate.integrate delegate

      scm {
        Scm.git delegate, 'git@bitbucket.org:gojek/infrastructure-ansible.git'
      }

      steps {
        copyArtifacts("${location}/Specs") {
          buildSelector {
            upstreamBuild(true)
          }
        }

        shell("ansible all -i 'i-documents-01,' -u gojek -m synchronize -a 'recursive=yes src=doc/ dest=/opt/documents/$service_name/'")
      }

      publishers {
        Slack.integrate delegate
      }
    }
  }
}

