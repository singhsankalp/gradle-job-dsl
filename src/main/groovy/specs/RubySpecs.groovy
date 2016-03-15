package specs

import helpers.*
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class RubySpecs implements Specs {

  String scm
  String location
  String[] downstreamJobs
  String artifactPattern

  Job Build(DslFactory dslFactory) {
    dslFactory.job("${location}/Specs") {

      label('slave')
      LogRotate.integrate delegate

      scm {
        Scm.git delegate, "$scm"
      }

      triggers {
        scm('* * * * *')
      }

      steps {
        shell('rm -rf Gemfile.lock')
        shell('ls config/*.sample | xargs -I {} sh -c \'cp -f $1 config/$(basename $1 .sample)\' - {}')
        shell('bundle install --path .local')
        shell('bundle exec rake db:drop:all db:create:all db:migrate')
      }

      publishers {
        Slack.integrate delegate
        downstreamJobs.each { job ->
          downstream("$location/$job", 'SUCCESS')
        }
        archiveArtifacts {
          pattern("$artifactPattern")
          onlyIfSuccessful()
        }
      }
    }
  }
}
