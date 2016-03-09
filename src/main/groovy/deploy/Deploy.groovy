package deploy

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

interface Deploy {
  Job Setup(DslFactory dslFactory)
}
