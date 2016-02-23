package utils

class ZipToDeb{
  static void convert(context, zipPackage, debPackage){
    context.with {
      steps{
        shell("rm -rf zipContents")
        shell("unzip ${zipPackage} -d zipContents")
        shell("fpm -s dir -t deb -C zipContents --name ${debPackage}")
        shell("mv *.deb target/universal/")
      }
    }
  }
}
