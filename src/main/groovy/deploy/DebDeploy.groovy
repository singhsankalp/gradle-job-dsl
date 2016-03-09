package deploy

import utils.Scm
import utils.LogRotate
import utils.Slack
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job


class DebDeploy implements Deploy {

  String app
  String jobLocation
  String environment
  LinkedHashMap appDeployConfig


  def etcd_server = [
                    "staging"     : "s-etcd-server-01",
                    "production"  : "p-etcd-server-01",
                    "test"        : "t-etcd-server-01",
                    "development" : "d-etcd-server-01"
  ]

  Job Setup(DslFactory dslFactory) {
    dslFactory.job("${jobLocation}") {

      String deb_name = app.replaceAll('_','-')
      LogRotate.integrate delegate
      parameters {
        stringParam("concurrencyPercentage", "30%","Percentage of servers on which chef-client will be run concurrently")
        stringParam("action", "Deploy","Choose \"Deploy\" to deploy new war or choose \"restarted\" to restart service")
      }
      scm {
        Scm.git delegate, "git@bitbucket.org:gojek/infrastructure-ansible.git"
      }
      label('slave')
      steps {
        shell('''
            case $action in
             "Deploy")
                latestVersion=`curl http://i-apt-repository:8080/api/repos/gojek_apt_repo/packages?q=$deb_name | jq '' | grep $deb_name | awk '{print \$3}' | sort -n | tail -1`;
                curl -L http://${this.etcd_server[environment]}:2379/v2/keys/$app/version -XPUT -d value=\$latestVersion;
                QUERY="chef_environment:${this.environment} AND run_list:recipe\\[${this.app}\\:\\:${this.recipe}\\]" ansible-playbook -i inventory/chef deployment.yml -e "concurrency=${concurrencyPercentage} max_fail_percentage=${this.maxFailPercentage} haproxy_query=${this.haproxyQuery} haproxy_backend=${this.haproxyBackend} action=${action}"
                ;;
             "restarted")
                QUERY="chef_environment:${this.environment} AND run_list:recipe\\[${this.app}\\:\\:${this.recipe}\\]" ansible-playbook -i inventory/chef deployment.yml -e "concurrency=${concurrencyPercentage} max_fail_percentage=${this.maxFailPercentage} haproxy_query=${this.haproxyQuery} haproxy_backend=${this.haproxyBackend} action=${action}"
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
