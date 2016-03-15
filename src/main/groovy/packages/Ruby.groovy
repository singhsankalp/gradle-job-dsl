package packages

import helpers.*
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
        Scm.git delegate, "${scm}"
      }

      label('slave')
      steps {
        shell("rm -rf .bundle *.deb $app postrm.sh Gemfile.lock")
        shell('bundle install --path .local')
        shell('bundle package --all')
        shell('bundle install --local --deployment --without development:test')
        build delegate, "$app"
        AptRepo.archive delegate, "$app"
      }

      publishers {
        Slack.integrate delegate
        downstream("${downstream}", 'SUCCESS')
        archiveArtifacts {
          pattern("*.deb")
          onlyIfSuccessful()
        }
      }
    }
  }

  private static void build(context, applicationName, packageUser=applicationName){
    String debName = applicationName.replaceAll('_','-')
    String deploymentPath = "/opt/${applicationName}"
    context.with {
      shell("""#!/bin/bash --login
            latestPackageVersion=\$(curl http://i-apt-repository:8080/api/repos/gojek_apt_repo/packages?q=${debName} | jq '' | grep ${debName} | awk '{print \$3}' | sort -n | tail -1 | awk '{print \$1 + 1}');
            if [ -z \$latestPackageVersion ]; then latestPackageVersion=1; fi

            mkdir ${applicationName}
            ls | grep -vw ${applicationName} | grep -vw artifacts | xargs -I {} cp -r {} ${applicationName}
            if [ -d .bundle ]; then cp -r .bundle ./${applicationName}; fi
            printf '#!/bin/sh\nrm -rf ${deploymentPath}/${applicationName}\n' >> postrm.sh;

            rbenv local 2.2.3;
            fpm -s dir -t deb -n ${debName} -a all -v \$latestPackageVersion --prefix ${deploymentPath} --after-remove ./postrm.sh --deb-user ${packageUser} --deb-group ${packageUser} ./${applicationName};

            rm -rf postrm.sh ${applicationName}
           """)
    }
  }
}
