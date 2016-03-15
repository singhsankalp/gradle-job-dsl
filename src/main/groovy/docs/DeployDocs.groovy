package docs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

interface DeployDocs {
  Job Setup(DslFactory dslFactory)
}
