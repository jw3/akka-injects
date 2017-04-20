if [ -z ${UPDATEIMPACT_API_KEY} ]; then sbt ++$TRAVIS_SCALA_VERSION test; else sbt ++$TRAVIS_SCALA_VERSION test updateImpactSubmit; fi
