package deploy

import helpers.*
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class JettyDeploy {

  String        jobLocation
  LinkedHashMap jettyBackend
  String        app

  Job Setup(DslFactory dslFactory) {

      dslFactory.job("${jobLocation}/${jettyBackend.name}") {

        LogRotate.integrate delegate
        parameters {
          stringParam("concurrencyPercentage", "30%","Percentage of servers on which chef-client will be run concurrently")
          stringParam("action", "Deploy","Choose \"Deploy\" to deploy new war or choose \"Restart\" to restart jetties")
        }
        scm {
          Scm.git delegate, "git@bitbucket.org:gojek/infrastructure-ansible.git"
        }
        label('slave')
          steps {
            copyArtifacts("${this.app}/Package") {
              buildSelector {
                latestSuccessful(true)
              }
            }
            shell('''
                 case $action in
                  "Deploy")
                     scp ./gojek*.war gojek@p-storage-box-01:/var/www/html/stan-marsh/;
                     scp ./latest gojek@p-storage-box-01:/var/www/html/stan-marsh/production;
                     QUERY="${this.jettyBackend.query}" ansible-playbook -i inventory/chef jetty_deployment.yml -e "concurrency=${concurrencyPercentage} max_fail_percentage=${this.jettyBackend.maxFailPercentage} haproxy_query=${this.jettyBackend.haproxyQuery} haproxy_backend=${this.jettyBackend.haproxyBackend} action=${action}"
                     rm -f gojek*.war;
                     ;;
                  "Restart")
                     QUERY="${this.jettyBackend.query}" ansible-playbook -i inventory/chef jetty_deployment.yml -e "concurrency=${concurrencyPercentage} max_fail_percentage=${this.jettyBackend.maxFailPercentage} haproxy_query=${this.jettyBackend.haproxyQuery} haproxy_backend=${this.jettyBackend.haproxyBackend} action=${action}"
                     ;;
                  esac
                ''')
          }
        publishers {
          Slack.integrate delegate
        }
      }
  }
}
