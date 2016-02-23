package utils

String app = 'GoBox'
String sub_app = 'GoBoxCms'

folder(app) {
  description "Folder for gobox"
}

folder("${app}/${sub_app}") {
  description "Folder for gobox_cms"
  primaryView('Pipeline')
}

job("${app}/${sub_app}/Package") {
  LogRotate.integrate delegate
  label('slave')

  scm {
    Scm.git delegate, 'git@bitbucket.org:gojek/gobox-cms.git'
  }

  triggers {
    scm('* * * * *')
  }

  steps {
    shell('npm install')
    shell('bower install')
    shell('gulp build')
    shell('cp -r dist gobox_cms')
    shell('knife download /data_bags/deployed_versions/gobox_cms.json --chef-repo-path .')
    shell('echo $(($(grep latest data_bags/deployed_versions/gobox_cms.json | awk \'{print $2}\' | awk -F, \'{print $1}\') + 1 )) > latest')
    shell("printf '#!/bin/sh\nrm -rf /opt/gobox/gobox_cms\n' > postrm.sh;")
    shell('''#!/bin/bash --login
      rbenv local 2.2.3;fpm -s dir -t deb -n gobox_cms -a all -v $(cat latest) --prefix /opt/gobox --after-remove ./postrm.sh ./gobox_cms
    ''')
    shell('sed -i "s/\\"latest\\": \\(.*\\),/\\"latest\\": $(cat latest),/g" data_bags/deployed_versions/gobox_cms.json')
    shell('knife data bag from file deployed_versions gobox_cms.json')
    shell('rm -rf dest gobox_cms *.deb')
  }

  publishers {
    Slack.integrate delegate
    downstream("${app}/${sub_app}/StagingDeploy", 'SUCCESS')
    archiveArtifacts {
      pattern("dist/**/*")
      onlyIfSuccessful()
    }
  }
}

job("${app}/${sub_app}/StagingDeploy") {
  LogRotate.integrate delegate
  label('slave')

  steps {
    shell("knife ssh 'chef_environment:staging AND run_list:recipe\\[gobox\\:\\:gobox_cms\\]' 'sudo chef-client' -x gojek -a hostname --no-host-key-verify")
  }

  publishers {
    Slack.integrate delegate
  }
}

buildPipelineView("${app}/${sub_app}/Pipeline") {
  title("gobox_cms Build Pipeline")
  selectedJob("${app}/${sub_app}/Package")
  displayedBuilds(5)
  filterBuildQueue()
  filterExecutors()
  alwaysAllowManualTrigger()
  showPipelineParameters()
  refreshFrequency(60)
  customCssUrl('/userContent/style.css')
}
