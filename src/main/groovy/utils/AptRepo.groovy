package utils

class AptRepo{
  static void includeDeb(context, debPackage, applicationName){
    context.with {
      steps{
        shell("""#!/bin/bash --login
              latestPackageVersion=`curl http://i-apt-repository:8080/api/repos/gojek_apt_repo/packages?q=${applicationName} | jq 'max' | awk '{print \$3}'`;
              deployPackageVersion=\$( expr \$latestPackageVersion + 1);
              applicationPrefix=`echo ${applicationName} | cut -d'-' -f1`;

              curl -X POST -F file=@target/universal/${applicationName}_\$(echo \$deployPackageVersion)_all.deb http://i-apt-repository:8080/api/files/${applicationName};
              curl -X POST http://i-apt-repository:8080/api/repos/gojek_apt_repo/file/${applicationName}/${applicationName}_\$(echo \$deployPackageVersion)_all.deb;
              curl -X PUT -H 'Content-Type: application/json' --data '{}'  http://i-apt-repository:8080/api/publish//trusty;
              curl -L http://s-etcd-server-01:2379/v2/keys/\$applicationPrefix/${applicationName}/version/latest -XPUT -d value=\$deployPackageVersion;
              curl -L http://p-etcd-server-01:2379/v2/keys/\$applicationPrefix/${applicationName}/version/latest -XPUT -d value=\$deployPackageVersion
             """)
      }
    }
  }

  static void build(context, applicationName, packageUser=applicationName){
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

  static void buildCms(context, applicationName){
    String debName = applicationName.replaceAll('_','-')
    String deploymentPath = "/opt/${applicationName}"
    context.with {
    shell("""#!/bin/bash --login
          latestPackageVersion=\$(curl http://i-apt-repository:8080/api/repos/gojek_apt_repo/packages?q=${debName} | jq '' | grep ${debName} | awk '{print \$3}' | sort -n | tail -1 | awk '{print \$1 + 1}');
          if [ -z \$latestPackageVersion ]; then latestPackageVersion=1; fi

          mkdir ${applicationName}
          cp -r dist/ ${applicationName}

          printf '#!/bin/sh\nrm -rf ${deploymentPath}/${applicationName}\n' >> postrm.sh;

          rbenv local 2.2.3;
          fpm -s dir -t deb -n ${debName} -a all -v \$latestPackageVersion --prefix ${deploymentPath} --after-remove ./postrm.sh --deb-user ${applicationName} --deb-group ${applicationName} ./${applicationName};

          rm -rf postrm.sh ${applicationName}
         """)
    }
  }

  static void archive(context, applicationName){
    applicationName = applicationName.replaceAll('_','-')
    context.with {
      shell("""#!/bin/bash --login
            package=\$(find . -name *.deb)

            curl -X POST -F file=@\$package http://i-apt-repository:8080/api/files/${applicationName};
            curl -X POST http://i-apt-repository:8080/api/repos/gojek_apt_repo/file/${applicationName}/\$(basename \$package);
            curl -X PUT -H 'Content-Type: application/json' --data '{}'  http://i-apt-repository:8080/api/publish//trusty;
           """)
    }
  }
}
