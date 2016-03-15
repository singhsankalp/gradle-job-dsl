package specs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

interface Specs {
  Job Build(DslFactory dslFactory)
}
