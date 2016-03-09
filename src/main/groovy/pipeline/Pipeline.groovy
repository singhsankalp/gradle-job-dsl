package pipeline
import deploy.Deploy
import packages.Packer
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Packer packer
  Deploy deploy

  Pipeline createPackage(DslFactory dslFactory){
    packer.Package(dslFactory)
    return this
  }

  Pipeline deployApp(DslFactory dslFactory){
    deploy.Setup(dslFactory)
    return this
  }
}
