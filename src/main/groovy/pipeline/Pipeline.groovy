package pipeline
import deploy.Deploy
import packages.Packer
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Packer packer
  Deploy deploy

  void createPackage(DslFactory dslFactory){
    packer.Package(dslFactory)
  }

  void deployApp(DslFactory dslFactory){
    deploy.Setup(dslFactory)
  }
}
