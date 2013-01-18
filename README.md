jubatus-tutorial-java
=====================

see http://jubat.us/en/tutorial.html

Quick Start
--------------------

    git clone git://github.com/naokikimura/jubatus-tutorial-java.git
    cd jubatus-tutorial-java

    curl -O http://people.csail.mit.edu/jrennie/20Newsgroups/20news-bydate.tar.gz
    tar -xvzf 20news-bydate.tar.gz

    jubaclassifier --configpath=src/main/config/config.json --rpc-port=9190 --name=tutorial &

    mvn -q compile exec:java -Dexec.args="-p 9190"

Output a classpath string of dependencies
--------------------

    mvn dependency:build-classpath -DoutputFile=classpath.txt

or

    mvn -q exec:exec -Dexec.executable="echo" -Dexec.args="%classpath"

###  e.g. jrunscript

    rlwrap jrunscript -cp $(mvn -q exec:exec -Dexec.executable="echo" -Dexec.args="%classpath")
