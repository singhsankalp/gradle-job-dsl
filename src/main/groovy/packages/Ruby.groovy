package packages

import utils.Scm
import utils.LogRotate
import utils.Slack
import utils.AptRepo
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Ruby implements Packer {

  String app
  String scm
  String location
  String downstream

  Job Package(DslFactory dslFactory) {

    dslFactory.job("${location}/Package") {

      LogRotate.integrate delegate
      scm {
        Scm.git delegate, "${this.scm}"
      }

      label('slave')
      steps {
        shell("rm -rf .bundle *.deb $app postrm.sh Gemfile.lock")
        shell('bundle install --path .local')
        shell('bundle package --all')
        shell('bundle install --local --deployment --without development:test')
        AptRepo.build delegate, "$app"
        AptRepo.archive delegate, "$app"
      }

      publishers {
        Slack.integrate delegate
        downstream("${this.downstream}", 'SUCCESS')
        archiveArtifacts {
          pattern("*.deb")
          onlyIfSuccessful()
        }
      }
    }
  }
}
