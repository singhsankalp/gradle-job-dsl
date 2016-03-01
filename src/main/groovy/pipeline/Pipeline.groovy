package pipeline
import packages.Ruby
import packages.Packer
import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class Pipeline {

  Packer packer
//  Deploy deploy

  void createPackage(DslFactory dslFactory){
    packer.Package(dslFactory)
  }

}
