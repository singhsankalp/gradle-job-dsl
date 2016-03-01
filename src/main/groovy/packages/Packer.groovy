package packages

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

interface Packer {
  Job Package(DslFactory dslFactory)
}
